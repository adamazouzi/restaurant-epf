package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Client;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ClientDao {

    private final JdbcTemplate jdbcTemplate;

    public ClientDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Client> findAll() {
        String sql = "SELECT id, nom, prenom, email, telephone FROM CLIENT ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapClient(rs));
    }

    public Client findById(Long id) {
        String sql = "SELECT id, nom, prenom, email, telephone FROM CLIENT WHERE id = ?";
        List<Client> result = jdbcTemplate.query(sql, (rs, rowNum) -> mapClient(rs), id);
        return result.isEmpty() ? null : result.get(0);
    }

    public Long create(Client client) {
        String sql = "INSERT INTO CLIENT (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, client.getNom());
            ps.setString(2, client.getPrenom());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelephone());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Impossible de récupérer l'id du client créé");
        }
        return key.longValue();
    }

    private Client mapClient(java.sql.ResultSet rs) throws java.sql.SQLException {
        Client client = new Client();
        client.setId(rs.getLong("id"));
        client.setNom(rs.getString("nom"));
        client.setPrenom(rs.getString("prenom"));
        client.setEmail(rs.getString("email"));
        client.setTelephone(rs.getString("telephone"));
        return client;
    }
}