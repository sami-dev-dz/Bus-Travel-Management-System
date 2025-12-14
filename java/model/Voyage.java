package model;

import java. time.LocalDate;

public class Voyage {
    private int idVoyage;
    private String code;
    private LocalDate dateDepart;
    private String heureDepart;
    private int duree;
    private int idBus;
    private int idAdministrateur;
    private String busImmatriculation;
    private int busNbPlaces;

    public Voyage() {}

    public Voyage(String code, LocalDate dateDepart, int duree, int idBus, int idAdministrateur) {
        this. code = code;
        this.dateDepart = dateDepart;
        this. duree = duree;
        this.idBus = idBus;
        this.idAdministrateur = idAdministrateur;
    }

    public Voyage(int idVoyage, String code, LocalDate dateDepart, String heureDepart, int duree, int idBus, int idAdministrateur) {
        this.idVoyage = idVoyage;
        this.code = code;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.duree = duree;
        this.idBus = idBus;
        this.idAdministrateur = idAdministrateur;
    }

    public int getIdVoyage() {
        return idVoyage;
    }

    public void setIdVoyage(int idVoyage) {
        this. idVoyage = idVoyage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDate dateDepart) {
        this.dateDepart = dateDepart;
    }

    public String getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(String heureDepart) {
        this.heureDepart = heureDepart;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this. duree = duree;
    }

    public int getIdBus() {
        return idBus;
    }

    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }

    public int getIdAdministrateur() {
        return idAdministrateur;
    }

    public void setIdAdministrateur(int idAdministrateur) {
        this.idAdministrateur = idAdministrateur;
    }

    public String getBusImmatriculation() {
        return busImmatriculation;
    }

    public void setBusImmatriculation(String busImmatriculation) {
        this.busImmatriculation = busImmatriculation;
    }

    public int getBusNbPlaces() {
        return busNbPlaces;
    }

    public void setBusNbPlaces(int busNbPlaces) {
        this. busNbPlaces = busNbPlaces;
    }
}