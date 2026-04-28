package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.model.CommandeClientStatut;
import fr.epf.restaurant.model.LigneCommandeClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class CommandeClientDao {

    private final JdbcTemplate jdbcTemplate;

    public CommandeClientDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insertCommande(Long clientId) {
        String sql = "INSERT INTO COMMANDE_CLIENT (client_id, statut) VALUES (?, 'EN_ATTENTE')";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, clientId);
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("ID") != null) {
            return ((Number) keys.get("ID")).longValue();
        }
        if (keys != null && keys.get("id") != null) {
            return ((Number) keys.get("id")).longValue();
        }

        throw new IllegalStateException("Impossible de récupérer l'id de la commande client créée");
    }

    public void insertLigne(Long commandeId, Long platId, int quantite) {
        String sql = """
                INSERT INTO LIGNE_COMMANDE_CLIENT (commande_client_id, plat_id, quantite)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(sql, commandeId, platId, quantite);
    }

    public List<CommandeClient> findAll(String statut) {
        if (statut == null || statut.isBlank()) {
            String sql = """
                    SELECT id, client_id, date_commande, statut
                    FROM COMMANDE_CLIENT
                    ORDER BY id DESC
                    """;
            List<CommandeClient> commandes = jdbcTemplate.query(sql, (rs, rowNum) -> mapCommande(rs));
            commandes.forEach(c -> c.setLignes(findLignesByCommandeId(c.getId())));
            return commandes;
        }

        String sql = """
                SELECT id, client_id, date_commande, statut
                FROM COMMANDE_CLIENT
                WHERE statut = ?
                ORDER BY id DESC
                """;
        List<CommandeClient> commandes = jdbcTemplate.query(sql, (rs, rowNum) -> mapCommande(rs), statut);
        commandes.forEach(c -> c.setLignes(findLignesByCommandeId(c.getId())));
        return commandes;
    }

    public CommandeClient findById(Long id) {
        String sql = """
                SELECT id, client_id, date_commande, statut
                FROM COMMANDE_CLIENT
                WHERE id = ?
                """;
        List<CommandeClient> result = jdbcTemplate.query(sql, (rs, rowNum) -> mapCommande(rs), id);
        if (result.isEmpty()) {
            return null;
        }
        CommandeClient commande = result.get(0);
        commande.setLignes(findLignesByCommandeId(id));
        return commande;
    }

    public List<LigneCommandeClient> findLignesByCommandeId(Long commandeId) {
        String sql = """
                SELECT id, commande_client_id, plat_id, quantite
                FROM LIGNE_COMMANDE_CLIENT
                WHERE commande_client_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            LigneCommandeClient ligne = new LigneCommandeClient();
            ligne.setId(rs.getLong("id"));
            ligne.setCommandeClientId(rs.getLong("commande_client_id"));
            ligne.setPlatId(rs.getLong("plat_id"));
            ligne.setQuantite(rs.getInt("quantite"));
            return ligne;
        }, commandeId);
    }

    public void updateStatut(Long id, CommandeClientStatut statut) {
        String sql = "UPDATE COMMANDE_CLIENT SET statut = ? WHERE id = ?";
        jdbcTemplate.update(sql, statut.name(), id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM LIGNE_COMMANDE_CLIENT WHERE commande_client_id = ?", id);
        jdbcTemplate.update("DELETE FROM COMMANDE_CLIENT WHERE id = ?", id);
    }

    private CommandeClient mapCommande(java.sql.ResultSet rs) throws java.sql.SQLException {
        CommandeClient commande = new CommandeClient();
        commande.setId(rs.getLong("id"));
        commande.setClientId(rs.getLong("client_id"));

        Timestamp timestamp = rs.getTimestamp("date_commande");
        commande.setDateCommande(timestamp == null ? null : timestamp.toLocalDateTime());

        commande.setStatut(CommandeClientStatut.valueOf(rs.getString("statut")));
        return commande;
    }
}