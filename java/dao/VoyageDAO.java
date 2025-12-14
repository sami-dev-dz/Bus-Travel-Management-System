package dao;

import java. sql.*;
import java.time.LocalDate;
import java. time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import model.Voyage;
import utils.DBConnection;

public class VoyageDAO {

    public static boolean ajouterVoyage(Voyage v) {
        String sql = "INSERT INTO Voyage (code, dateDepart, heureDepart, duree, idBus, idAdministrateur) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v. getDateDepart().toString());
            ps.setString(3, v.getHeureDepart());
            ps.setInt(4, v.getDuree());
            ps.setInt(5, v. getIdBus());
            ps.setInt(6, v. getIdAdministrateur());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean modifierVoyage(Voyage v) {
        String sql = "UPDATE Voyage SET code=?, dateDepart=?, heureDepart=?, duree=?, idBus=?, idAdministrateur=? WHERE idVoyage=? ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v. getDateDepart().toString());
            ps.setString(3, v.getHeureDepart());
            ps.setInt(4, v. getDuree());
            ps.setInt(5, v. getIdBus());
            ps.setInt(6, v.getIdAdministrateur());
            ps.setInt(7, v.getIdVoyage());
            return ps. executeUpdate() == 1;
        } catch (SQLException e) {
            e. printStackTrace();
            return false;
        }
    }

    public static boolean supprimerVoyage(String code) {
        String sql = "DELETE FROM Voyage WHERE code=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn. prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Voyage> getAllVoyage() {
        ArrayList<Voyage> listeVoyages = new ArrayList<>();
        String sql = "SELECT v.idVoyage, v.code, v. dateDepart, v.heureDepart, v.duree, v.idBus, v.idAdministrateur, " +
                     "b.immatriculation AS busImmatriculation, b.nbPlaces AS busNbPlaces " +
                     "FROM Voyage v " +
                     "LEFT JOIN Bus b ON v.idBus = b.idBus " +
                     "ORDER BY v. dateDepart DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt. executeQuery(sql)) {
            while (rs.next()) {
                Voyage v = new Voyage();
                v.setIdVoyage(rs.getInt("idVoyage"));
                v.setCode(rs.getString("code"));
                String dateStr = rs.getString("dateDepart");
                if (dateStr != null && !dateStr.isEmpty()) {
                    v.setDateDepart(LocalDate. parse(dateStr));
                }
                v.setHeureDepart(rs.getString("heureDepart"));
                v.setDuree(rs. getInt("duree"));
                v. setIdBus(rs.getInt("idBus"));
                v.setIdAdministrateur(rs.getInt("idAdministrateur"));
                v. setBusImmatriculation(rs.getString("busImmatriculation"));
                v. setBusNbPlaces(rs.getInt("busNbPlaces"));
                listeVoyages.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listeVoyages;
    }

    public static boolean codeVoyageExiste(String code) {
        String sql = "SELECT COUNT(*) FROM Voyage WHERE code=?";
        try (Connection conn = DBConnection. getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Voyage getVoyageByCode(String code) {
        String sql = "SELECT v.idVoyage, v.code, v.dateDepart, v.heureDepart, v. duree, v. idBus, v.idAdministrateur, " +
                     "b.immatriculation AS busImmatriculation, b.nbPlaces AS busNbPlaces " +
                     "FROM Voyage v " +
                     "LEFT JOIN Bus b ON v.idBus = b.idBus " +
                     "WHERE v.code=?";
        try (Connection conn = DBConnection. getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voyage v = new Voyage();
                v.setIdVoyage(rs.getInt("idVoyage"));
                v.setCode(rs.getString("code"));
                String dateStr = rs.getString("dateDepart");
                if (dateStr != null && !dateStr. isEmpty()) {
                    v.setDateDepart(LocalDate.parse(dateStr));
                }
                v.setHeureDepart(rs.getString("heureDepart"));
                v.setDuree(rs.getInt("duree"));
                v.setIdBus(rs.getInt("idBus"));
                v.setIdAdministrateur(rs.getInt("idAdministrateur"));
                v.setBusImmatriculation(rs.getString("busImmatriculation"));
                v. setBusNbPlaces(rs.getInt("busNbPlaces"));
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBusInfoById(int idBus) {
        String sql = "SELECT immatriculation, marque, modele FROM Bus WHERE idBus=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps. setInt(1, idBus);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("immatriculation") + " - " +
                       rs. getString("marque") + " " +
                       rs. getString("modele");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Non assigné";
    }

    public static Voyage getVoyageById(int idVoyage) {
        String sql = "SELECT v.idVoyage, v.code, v.dateDepart, v.heureDepart, v. duree, v. idBus, v.idAdministrateur, " +
                     "b. immatriculation AS busImmatriculation, b.nbPlaces AS busNbPlaces " +
                     "FROM Voyage v " +
                     "LEFT JOIN Bus b ON v. idBus = b.idBus " +
                     "WHERE v.idVoyage=?";
        try (Connection conn = DBConnection. getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            ResultSet rs = ps.executeQuery();
            if (rs. next()) {
                Voyage v = new Voyage();
                v.setIdVoyage(rs. getInt("idVoyage"));
                v.setCode(rs.getString("code"));
                String dateStr = rs.getString("dateDepart");
                if (dateStr != null && ! dateStr.isEmpty()) {
                    v.setDateDepart(LocalDate.parse(dateStr));
                }
                v.setHeureDepart(rs.getString("heureDepart"));
                v. setDuree(rs.getInt("duree"));
                v.setIdBus(rs.getInt("idBus"));
                v. setIdAdministrateur(rs.getInt("idAdministrateur"));
                v.setBusImmatriculation(rs.getString("busImmatriculation"));
                v.setBusNbPlaces(rs.getInt("busNbPlaces"));
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean busDisponible(int idBus, LocalDate dateDepart, String heureDepart, int dureeMinutes, Integer idVoyageExclu) {
        ArrayList<Voyage> voyages = getVoyagesByBus(idBus);
        
        LocalTime heureDebut = parseHeure(heureDepart);
        LocalDateTime debutNouveau = dateDepart.atTime(heureDebut);
        LocalDateTime finNouveau = debutNouveau.plusMinutes(dureeMinutes);

        for (Voyage v : voyages) {
            if (idVoyageExclu != null && v.getIdVoyage() == idVoyageExclu) {
                continue;
            }

            LocalTime heureExistant = parseHeure(v.getHeureDepart());
            LocalDateTime debutExistant = v.getDateDepart().atTime(heureExistant);
            LocalDateTime finExistant = debutExistant. plusMinutes(v.getDuree());

            if (periodesSeChevauche(debutNouveau, finNouveau, debutExistant, finExistant)) {
                return false;
            }
        }

        return true;
    }

    public static ArrayList<Voyage> getVoyagesByBus(int idBus) {
        ArrayList<Voyage> voyages = new ArrayList<>();
        String sql = "SELECT idVoyage, code, dateDepart, heureDepart, duree, idBus, idAdministrateur " +
                     "FROM Voyage WHERE idBus = ?  ORDER BY dateDepart, heureDepart";
        try (Connection conn = DBConnection. getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBus);
            ResultSet rs = ps. executeQuery();
            while (rs.next()) {
                Voyage v = new Voyage();
                v.setIdVoyage(rs.getInt("idVoyage"));
                v.setCode(rs.getString("code"));
                String dateStr = rs.getString("dateDepart");
                if (dateStr != null && !dateStr.isEmpty()) {
                    v. setDateDepart(LocalDate.parse(dateStr));
                }
                v. setHeureDepart(rs.getString("heureDepart"));
                v.setDuree(rs.getInt("duree"));
                v.setIdBus(rs.getInt("idBus"));
                v.setIdAdministrateur(rs.getInt("idAdministrateur"));
                voyages.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voyages;
    }

    public static String getVoyageEnConflit(int idBus, LocalDate dateDepart, String heureDepart, int dureeMinutes, Integer idVoyageExclu) {
        ArrayList<Voyage> voyages = getVoyagesByBus(idBus);
        
        LocalTime heureDebut = parseHeure(heureDepart);
        LocalDateTime debutNouveau = dateDepart.atTime(heureDebut);
        LocalDateTime finNouveau = debutNouveau.plusMinutes(dureeMinutes);

        for (Voyage v : voyages) {
            if (idVoyageExclu != null && v.getIdVoyage() == idVoyageExclu) {
                continue;
            }

            LocalTime heureExistant = parseHeure(v.getHeureDepart());
            LocalDateTime debutExistant = v. getDateDepart().atTime(heureExistant);
            LocalDateTime finExistant = debutExistant.plusMinutes(v. getDuree());

            if (periodesSeChevauche(debutNouveau, finNouveau, debutExistant, finExistant)) {
                return v.getCode() + " (" + v.getDateDepart() + " " + v.getHeureDepart() + " - Durée: " + formatDuree(v.getDuree()) + ")";
            }
        }

        return null;
    }

    private static LocalTime parseHeure(String heure) {
        if (heure == null || heure. isEmpty()) {
            return LocalTime.of(0, 0);
        }
        try {
            String[] parts = heure.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = parts. length > 1 ? Integer.parseInt(parts[1]) : 0;
            return LocalTime.of(h, m);
        } catch (Exception e) {
            return LocalTime.of(0, 0);
        }
    }

    private static boolean periodesSeChevauche(LocalDateTime debut1, LocalDateTime fin1, LocalDateTime debut2, LocalDateTime fin2) {
        return debut1.isBefore(fin2) && fin1.isAfter(debut2);
    }

    private static String formatDuree(int dureeMinutes) {
        int heures = dureeMinutes / 60;
        int minutes = dureeMinutes % 60;
        if (minutes == 0) {
            return heures + "h";
        }
        return heures + "h" + String.format("%02d", minutes) + "min";
    }
}