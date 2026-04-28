package fr.epf.restaurant.dao;

import fr.epf.restaurant.dto.IngredientPrixDto;
import fr.epf.restaurant.model.FournisseurIngredientPrix;
import fr.epf.restaurant.model.Ingredient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IngredientDao {

    private final JdbcTemplate jdbcTemplate;

    public IngredientDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ingredient> findAll() {
        String sql = "SELECT id, nom, unite, stock_actuel, seuil_alerte FROM INGREDIENT ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapIngredient(rs));
    }

    public Ingredient findById(Long id) {
        String sql = "SELECT id, nom, unite, stock_actuel, seuil_alerte FROM INGREDIENT WHERE id = ?";
        List<Ingredient> result = jdbcTemplate.query(sql, (rs, rowNum) -> mapIngredient(rs), id);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Ingredient> findSousAlerte() {
        String sql = """
                SELECT id, nom, unite, stock_actuel, seuil_alerte
                FROM INGREDIENT
                WHERE stock_actuel < seuil_alerte
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapIngredient(rs));
    }

    public void updateStock(Long ingredientId, double nouveauStock) {
        String sql = "UPDATE INGREDIENT SET stock_actuel = ? WHERE id = ?";
        jdbcTemplate.update(sql, nouveauStock, ingredientId);
    }

    public List<IngredientPrixDto> findPrixByIngredientId(Long ingredientId) {
        String sql = """
                SELECT
                    f.id AS fournisseur_id,
                    f.nom AS fournisseur_nom,
                    fi.prix_unitaire
                FROM FOURNISSEUR_INGREDIENT fi
                JOIN FOURNISSEUR f ON f.id = fi.fournisseur_id
                WHERE fi.ingredient_id = ?
                ORDER BY fi.prix_unitaire ASC, f.id ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new IngredientPrixDto(
                        rs.getLong("fournisseur_id"),
                        rs.getString("fournisseur_nom"),
                        rs.getBigDecimal("prix_unitaire")
                ), ingredientId);
    }

    public FournisseurIngredientPrix findMeilleureOffrePourIngredient(Long ingredientId) {
        String sql = """
                SELECT
                    fi.fournisseur_id,
                    f.nom AS fournisseur_nom,
                    fi.ingredient_id,
                    i.nom AS ingredient_nom,
                    fi.prix_unitaire
                FROM FOURNISSEUR_INGREDIENT fi
                JOIN FOURNISSEUR f ON f.id = fi.fournisseur_id
                JOIN INGREDIENT i ON i.id = fi.ingredient_id
                WHERE fi.ingredient_id = ?
                ORDER BY fi.prix_unitaire ASC, f.id ASC
                LIMIT 1
                """;

        List<FournisseurIngredientPrix> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            FournisseurIngredientPrix prix = new FournisseurIngredientPrix();
            prix.setFournisseurId(rs.getLong("fournisseur_id"));
            prix.setFournisseurNom(rs.getString("fournisseur_nom"));
            prix.setIngredientId(rs.getLong("ingredient_id"));
            prix.setIngredientNom(rs.getString("ingredient_nom"));
            prix.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
            return prix;
        }, ingredientId);

        return result.isEmpty() ? null : result.get(0);
    }

    private Ingredient mapIngredient(java.sql.ResultSet rs) throws java.sql.SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getLong("id"));
        ingredient.setNom(rs.getString("nom"));
        ingredient.setUnite(rs.getString("unite"));
        ingredient.setStockActuel(rs.getDouble("stock_actuel"));
        ingredient.setSeuilAlerte(rs.getDouble("seuil_alerte"));
        return ingredient;
    }
}