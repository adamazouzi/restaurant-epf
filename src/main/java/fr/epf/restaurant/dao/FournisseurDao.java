package fr.epf.restaurant.dao;

import fr.epf.restaurant.dto.FournisseurCatalogueDto;
import fr.epf.restaurant.model.Fournisseur;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class FournisseurDao {

    private final JdbcTemplate jdbcTemplate;

    public FournisseurDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Fournisseur> findAll() {
        String sql = "SELECT id, nom, contact, email FROM FOURNISSEUR ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapFournisseur(rs));
    }

    public Fournisseur findById(Long id) {
        String sql = "SELECT id, nom, contact, email FROM FOURNISSEUR WHERE id = ?";
        List<Fournisseur> result = jdbcTemplate.query(sql, (rs, rowNum) -> mapFournisseur(rs), id);
        return result.isEmpty() ? null : result.get(0);
    }

    public Long create(Fournisseur fournisseur) {
        String sql = "INSERT INTO FOURNISSEUR (nom, contact, email) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, fournisseur.getNom());
            ps.setString(2, fournisseur.getContact());
            ps.setString(3, fournisseur.getEmail());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Impossible de récupérer l'id du fournisseur créé");
        }
        return key.longValue();
    }

    public List<FournisseurCatalogueDto> findCatalogueByFournisseurId(Long fournisseurId) {
        String sql = """
                SELECT
                    i.id AS ingredient_id,
                    i.nom AS ingredient_nom,
                    i.unite AS ingredient_unite,
                    fi.prix_unitaire
                FROM FOURNISSEUR_INGREDIENT fi
                JOIN INGREDIENT i ON i.id = fi.ingredient_id
                WHERE fi.fournisseur_id = ?
                ORDER BY i.id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new FournisseurCatalogueDto(
                        rs.getLong("ingredient_id"),
                        rs.getString("ingredient_nom"),
                        rs.getString("ingredient_unite"),
                        rs.getBigDecimal("prix_unitaire")
                ), fournisseurId);
    }

    private Fournisseur mapFournisseur(java.sql.ResultSet rs) throws java.sql.SQLException {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(rs.getLong("id"));
        fournisseur.setNom(rs.getString("nom"));
        fournisseur.setContact(rs.getString("contact"));
        fournisseur.setEmail(rs.getString("email"));
        return fournisseur;
    }
}