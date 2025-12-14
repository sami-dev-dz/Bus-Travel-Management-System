package model;

import org.mindrot.jbcrypt.BCrypt;

public class Receptionniste {
    private int idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;

    public Receptionniste() {
        this.role = "Réceptionniste";
    }

    public Receptionniste(String nom, String prenom, String email, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = "Réceptionniste";
    }

    public Receptionniste(int idUtilisateur, String nom, String prenom, String email, String motDePasse, String role) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean verifierMotDePasse(String motDePasseClair) {
        return BCrypt.checkpw(motDePasseClair, this.motDePasse);
    }
    
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    public boolean isAdmin() {
        return "Administrateur".equals(role);
    }
    
    public boolean isReceptionniste() {
        return "Réceptionniste".equals(role);
    }
}