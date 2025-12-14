package model;

import java.time.LocalDate;

public class Reservation {
    private int idReservation;
    private LocalDate dateReservation;
    private String statut;
    private int nbPlaces;
    private int idVoyage;
    private int idPassager;
    private int idReceptionniste;

    public Reservation() {
        this. dateReservation = LocalDate.now();
        this.statut = "En attente";
        this.nbPlaces = 1;
    }

    public Reservation(int idVoyage, int idPassager, int nbPlaces, int idReceptionniste) {
        this. dateReservation = LocalDate.now();
        this.statut = "En attente";
        this.nbPlaces = nbPlaces;
        this.idVoyage = idVoyage;
        this.idPassager = idPassager;
        this.idReceptionniste = idReceptionniste;
    }

    public Reservation(int idReservation, LocalDate dateReservation, String statut, int nbPlaces,
                       int idVoyage, int idPassager, int idReceptionniste) {
        this.idReservation = idReservation;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.nbPlaces = nbPlaces;
        this.idVoyage = idVoyage;
        this.idPassager = idPassager;
        this.idReceptionniste = idReceptionniste;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this. dateReservation = dateReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public int getIdVoyage() {
        return idVoyage;
    }

    public void setIdVoyage(int idVoyage) {
        this.idVoyage = idVoyage;
    }

    public int getIdPassager() {
        return idPassager;
    }

    public void setIdPassager(int idPassager) {
        this.idPassager = idPassager;
    }

    public int getIdReceptionniste() {
        return idReceptionniste;
    }

    public void setIdReceptionniste(int idReceptionniste) {
        this.idReceptionniste = idReceptionniste;
    }
}