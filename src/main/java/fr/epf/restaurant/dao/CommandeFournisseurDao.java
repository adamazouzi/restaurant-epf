package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.CommandeFournisseur;
import fr.epf.restaurant.model.CommandeFournisseurStatut;
import fr.epf.restaurant.model.LigneCommandeFournisseur;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class CommandeFournisseurDao {

    private final JdbcTemplate jdbcTemplate;

    public CommandeFournisseurDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insertCommande(Long fournisseurId) {
        String sql = "INSERT INTO COMMANDE_FOURNISSEUR (fournisseur_id, statut) VALUES (?, 'EN_ATTENTE')";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, fournisseurId);
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("ID") != null) {
            return ((Number) keys.get("ID")).longValue();
        }
        if (keys != null && keys.get("id") != null) {
            return ((Number) keys.get("id")).longValue();
        }

        throw new IllegalStateException("Impossible de récupérer l'id de la commande fournisseur créée");
    }

    public void insertLigne(Long commandeId,
                            Long ingredientId,
                            double quantiteCommandee,
                            java.math.BigDecimal prixUnitaire) {
        String sql = """
                INSERT INTO LIGNE_COMMANDE_FOURNISSEUR
                (commande_fournisseur_id, ingredient_id, quantite_commandee, prix_unitaire)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql, commandeId, ingredientId, quantiteCommandee, prixUnitaire);
    }

    public List<CommandeFournisseur> findAll() {
        String sql = """
                SELECT id, fournisseur_id, date_commande, statut
                FROM COMMANDE_FOURNISSEUR
                ORDER BY id DESC
                """;
        List<CommandeFournisseur> commandes =
                jdbcTemplate.query(sql, (rs, rowNum) -> mapCommande(rs));
        commandes.forEach(c -> c.setLignes(findLignesByCommandeId(c.getId())));
        return commandes;
    }

    public CommandeFournisseur findById(Long id) {
        String sql = """
                SELECT id, fournisseur_id, date_commande, statut
                FROM COMMANDE_FOURNISSEUR
                WHERE id = ?
                """;
        List<CommandeFournisseur> result =
                jdbcTemplate.query(sql, (rs, rowNum) -> mapCommande(rs), id);

        if (result.isEmpty()) {
            return null;
        }

        CommandeFournisseur commande = result.get(0);
        commande.setLignes(findLignesByCommandeId(id));
        return commande;
    }

    public List<LigneCommandeFournisseur> findLignesByCommandeId(Long commandeId) {
        String sql = """
                SELECT id, commande_fournisseur_id, ingredient_id, quantite_commandee, prix_unitaire
                FROM LIGNE_COMMANDE_FOURNISSEUR
                WHERE commande_fournisseur_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
            ligne.setId(rs.getLong("id"));
            ligne.setCommandeFournisseurId(rs.getLong("commande_fournisseur_id"));
            ligne.setIngredientId(rs.getLong("ingredient_id"));
            ligne.setQuantiteCommandee(rs.getDouble("quantite_commandee"));
            ligne.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
            return ligne;
        }, commandeId);
    }

    public void updateStatut(Long id, CommandeFournisseurStatut statut) {
        String sql = "UPDATE COMMANDE_FOURNISSEUR SET statut = ? WHERE id = ?";
        jdbcTemplate.update(sql, statut.name(), id);
    }

    public void delete(Long id) {
        jdbcTemplate.update(
                "DELETE FROM LIGNE_COMMANDE_FOURNISSEUR WHERE commande_fournisseur_id = ?",
                id
        );
        jdbcTemplate.update(
                "DELETE FROM COMMANDE_FOURNISSEUR WHERE id = ?",
                id
        );
    }

    private CommandeFournisseur mapCommande(java.sql.ResultSet rs) throws java.sql.SQLException {
        CommandeFournisseur commande = new CommandeFournisseur();
        commande.setId(rs.getLong("id"));
        commande.setFournisseurId(rs.getLong("fournisseur_id"));

        Timestamp timestamp = rs.getTimestamp("date_commande");
        commande.setDateCommande(timestamp == null ? null : timestamp.toLocalDateTime());

        commande.setStatut(CommandeFournisseurStatut.valueOf(rs.getString("statut")));
        return commande;
    }
}