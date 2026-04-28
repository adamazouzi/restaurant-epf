package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Plat;
import fr.epf.restaurant.model.PlatIngredient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PlatDao {

    private final JdbcTemplate jdbcTemplate;

    public PlatDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Plat> findAll() {
        String sql = "SELECT id, nom, description, prix FROM PLAT ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapPlat(rs));
    }

    public Plat findById(Long id) {
        String sql = "SELECT id, nom, description, prix FROM PLAT WHERE id = ?";
        List<Plat> result = jdbcTemplate.query(sql, (rs, rowNum) -> mapPlat(rs), id);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<PlatIngredient> findIngredientsByPlatId(Long platId) {
        String sql = """
                SELECT
                    pi.plat_id,
                    pi.ingredient_id,
                    i.nom AS ingredient_nom,
                    i.unite AS ingredient_unite,
                    pi.quantite_requise
                FROM PLAT_INGREDIENT pi
                JOIN INGREDIENT i ON i.id = pi.ingredient_id
                WHERE pi.plat_id = ?
                ORDER BY i.id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PlatIngredient platIngredient = new PlatIngredient();
            platIngredient.setPlatId(rs.getLong("plat_id"));
            platIngredient.setIngredientId(rs.getLong("ingredient_id"));
            platIngredient.setIngredientNom(rs.getString("ingredient_nom"));
            platIngredient.setIngredientUnite(rs.getString("ingredient_unite"));
            platIngredient.setQuantiteRequise(rs.getDouble("quantite_requise"));
            return platIngredient;
        }, platId);
    }

    public Long create(Plat plat) {
        if (plat == null) {
            throw new IllegalArgumentException("Le plat est obligatoire");
        }
        if (plat.getNom() == null || plat.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du plat est obligatoire");
        }
        if (plat.getPrix() == null || plat.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix du plat doit être strictement positif");
        }

        String sql = "INSERT INTO PLAT (nom, description, prix) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, plat.getNom());
            ps.setString(2, plat.getDescription());
            ps.setBigDecimal(3, plat.getPrix());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Impossible de récupérer l'id du plat créé");
        }
        return key.longValue();
    }

    private Plat mapPlat(java.sql.ResultSet rs) throws java.sql.SQLException {
        Plat plat = new Plat();
        plat.setId(rs.getLong("id"));
        plat.setNom(rs.getString("nom"));
        plat.setDescription(rs.getString("description"));
        plat.setPrix(rs.getBigDecimal("prix"));
        return plat;
    }
}