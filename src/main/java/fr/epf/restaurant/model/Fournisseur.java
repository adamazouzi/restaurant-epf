package fr.epf.restaurant.model;

public class Fournisseur {

    private Long id;
    private String nom;
    private String contact;
    private String email;

    public Fournisseur() {
    }

    public Fournisseur(Long id, String nom, String contact, String email) {
        this.id = id;
        this.nom = nom;
        this.contact = contact;
        this.email = email;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contactValue) {
        this.contact = contactValue;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emailValue) {
        this.email = emailValue;
    }
}