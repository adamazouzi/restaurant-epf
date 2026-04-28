package fr.epf.restaurant.model;

public class Client {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    public Client() {
    }

    public Client(Long id, String nom, String prenom, String email, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idValue) {
        this.id = idValue;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nomValue) {
        this.nom = nomValue;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenomValue) {
        this.prenom = prenomValue;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emailValue) {
        this.email = emailValue;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephoneValue) {
        this.telephone = telephoneValue;
    }
}