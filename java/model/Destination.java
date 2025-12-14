package model;

public class Destination {

    private int idDestination;
    private String codeDestination;
    private String nomVille;
    private int idAdministrateur;

    public Destination() {}

    public Destination(int idDestination, String codeDestination, String nomVille, int idAdministrateur) {
        this.idDestination = idDestination;
        this.codeDestination = codeDestination;
        this.nomVille = nomVille;
        this.idAdministrateur = idAdministrateur;
    }

    public Destination(String codeDestination, String nomVille, int idAdministrateur) {
        this.codeDestination = codeDestination;
        this.nomVille = nomVille;
        this.idAdministrateur = idAdministrateur;
    }

    public int getIdDestination() {
        return idDestination;
    }

    public void setIdDestination(int idDestination) {
        this.idDestination = idDestination;
    }

    public String getCodeDestination() {
        return codeDestination;
    }

    public void setCodeDestination(String codeDestination) {
        this.codeDestination = codeDestination;
    }

    public String getNomVille() {
        return nomVille;
    }

    public void setNomVille(String nomVille) {
        this.nomVille = nomVille;
    }

    public int getIdAdministrateur() {
        return idAdministrateur;
    }

    public void setIdAdministrateur(int idAdministrateur) {
        this.idAdministrateur = idAdministrateur;
    }
}