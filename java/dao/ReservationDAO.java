package dao;

import model. Reservation;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util. ArrayList;

public class ReservationDAO {

    public static ArrayList<Reservation> getAllReservations() {
        ArrayList<Reservation> list = new ArrayList<>();
        String sql = "SELECT idReservation, dateReservation, statut, nbPlaces, idVoyage, idPassager, idReceptionniste FROM Reservation ORDER BY idReservation DESC";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reservation r = new Reservation(
                        rs.getInt("idReservation"),
                        LocalDate.parse(rs.getString("dateReservation")),
                        rs.getString("statut"),
                        rs.getInt("nbPlaces"),
                        rs.getInt("idVoyage"),
                        rs.getInt("idPassager"),
                        rs.getInt("idReceptionniste")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<Reservation> rechercher(String critere) {
        ArrayList<Reservation> list = new ArrayList<>();
        String sql = "SELECT DISTINCT r.idReservation, r.dateReservation, r.statut, r.nbPlaces, r.idVoyage, r.idPassager, r.idReceptionniste " +
                     "FROM Reservation r " +
                     "LEFT JOIN Voyage v ON r.idVoyage = v.idVoyage " +
                     "LEFT JOIN Passager p ON r. idPassager = p.idPassager " +
                     "LEFT JOIN VoyageDestination vd ON v.idVoyage = vd.idVoyage " +
                     "LEFT JOIN Destination d ON vd. idDestination = d.idDestination " +
                     "WHERE p.nom LIKE ?  OR p.prenom LIKE ? OR d.nomVille LIKE ? OR d.codeDestination LIKE ?  " +
                     "ORDER BY r. idReservation DESC";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            String recherche = "%" + critere + "%";
            ps.setString(1, recherche);
            ps.setString(2, recherche);
            ps.setString(3, recherche);
            ps.setString(4, recherche);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation(
                            rs.getInt("idReservation"),
                            LocalDate. parse(rs.getString("dateReservation")),
                            rs.getString("statut"),
                            rs.getInt("nbPlaces"),
                            rs.getInt("idVoyage"),
                            rs.getInt("idPassager"),
                            rs.getInt("idReceptionniste")
                    );
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e. printStackTrace();
        }
        return list;
    }

    public static boolean existe(int idVoyage, int idPassager) {
        String sql = "SELECT 1 FROM Reservation WHERE idVoyage = ? AND idPassager = ?  AND statut != 'Annulée' LIMIT 1";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps. setInt(1, idVoyage);
            ps.setInt(2, idPassager);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e. printStackTrace();
        }
        return false;
    }

    public static boolean ajouter(Reservation r) {
        int placesDisponibles = getPlacesDisponibles(r.getIdVoyage());
        if (!"Annulée". equals(r.getStatut()) && placesDisponibles < r.getNbPlaces()) {
            return false;
        }

        String insertSql = "INSERT INTO Reservation (dateReservation, statut, nbPlaces, idVoyage, idPassager, idReceptionniste) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(insertSql)) {
            ps.setString(1, r.getDateReservation().toString());
            ps.setString(2, r.getStatut());
            ps.setInt(3, r.getNbPlaces());
            ps.setInt(4, r.getIdVoyage());
            ps.setInt(5, r.getIdPassager());
            ps.setInt(6, r.getIdReceptionniste());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean modifier(Reservation r) {
        Reservation ancienne = getById(r.getIdReservation());
        if (ancienne == null) {
            return false;
        }

        boolean changementVoyage = ancienne.getIdVoyage() != r.getIdVoyage();
        boolean reactivation = "Annulée". equals(ancienne.getStatut()) && !"Annulée".equals(r. getStatut());
        boolean augmentationPlaces = r.getNbPlaces() > ancienne.getNbPlaces();

        if (!"Annulée". equals(r.getStatut())) {
            int placesDisponibles = getPlacesDisponibles(r.getIdVoyage());

            if (changementVoyage) {
                if (placesDisponibles < r.getNbPlaces()) {
                    return false;
                }
            } else if (reactivation || augmentationPlaces) {
                int placesSupplementaires = r.getNbPlaces();
                if (! reactivation && !"Annulée".equals(ancienne.getStatut())) {
                    placesSupplementaires = r.getNbPlaces() - ancienne.getNbPlaces();
                }
                if (placesDisponibles < placesSupplementaires) {
                    return false;
                }
            }
        }

        String updateSql = "UPDATE Reservation SET dateReservation = ?, statut = ?, nbPlaces = ?, idVoyage = ?, idPassager = ?  WHERE idReservation = ?";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(updateSql)) {
            ps. setString(1, r.getDateReservation().toString());
            ps.setString(2, r.getStatut());
            ps.setInt(3, r.getNbPlaces());
            ps.setInt(4, r.getIdVoyage());
            ps.setInt(5, r.getIdPassager());
            ps.setInt(6, r. getIdReservation());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean supprimer(int idReservation) {
        String sql = "DELETE FROM Reservation WHERE idReservation = ?";
        try (Connection cnx = DBConnection. getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idReservation);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getPlacesDisponibles(int idVoyage) {
        String sql = "SELECT " +
                     "COALESCE(b.nbPlaces, 0) - " +
                     "COALESCE((SELECT SUM(nbPlaces) FROM Reservation r WHERE r.idVoyage = v. idVoyage AND r.statut != 'Annulée'), 0) AS placesDisponibles " +
                     "FROM Voyage v " +
                     "LEFT JOIN Bus b ON v.idBus = b.idBus " +
                     "WHERE v.idVoyage = ? ";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx. prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Math.max(rs.getInt("placesDisponibles"), 0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPlacesReservees(int idVoyage) {
        String sql = "SELECT COALESCE(SUM(nbPlaces), 0) FROM Reservation WHERE idVoyage = ?  AND statut != 'Annulée'";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPlacesEnAttente(int idVoyage) {
        String sql = "SELECT COALESCE(SUM(nbPlaces), 0) FROM Reservation WHERE idVoyage = ? AND statut = 'En attente'";
        try (Connection cnx = DBConnection. getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPlacesConfirmees(int idVoyage) {
        String sql = "SELECT COALESCE(SUM(nbPlaces), 0) FROM Reservation WHERE idVoyage = ? AND statut = 'Confirmée'";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCapaciteBus(int idVoyage) {
        String sql = "SELECT COALESCE(b.nbPlaces, 0) AS capacite " +
                     "FROM Voyage v " +
                     "LEFT JOIN Bus b ON v.idBus = b.idBus " +
                     "WHERE v. idVoyage = ?";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("capacite");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Reservation getById(int idReservation) {
        String sql = "SELECT idReservation, dateReservation, statut, nbPlaces, idVoyage, idPassager, idReceptionniste FROM Reservation WHERE idReservation = ?";
        try (Connection cnx = DBConnection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps. setInt(1, idReservation);
            try (ResultSet rs = ps. executeQuery()) {
                if (rs. next()) {
                    return new Reservation(
                            rs.getInt("idReservation"),
                            LocalDate. parse(rs.getString("dateReservation")),
                            rs.getString("statut"),
                            rs.getInt("nbPlaces"),
                            rs.getInt("idVoyage"),
                            rs.getInt("idPassager"),
                            rs.getInt("idReceptionniste")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}