package dao;

import java. sql.*;
import java.util.ArrayList;
import utils.DBConnection;

public class VoyageDestinationDAO {

    public static boolean associer(int idVoyage, int idDestination) {
        int prochainOrdre = getProchainOrdre(idVoyage);
        String sql = "INSERT INTO VoyageDestination (idVoyage, idDestination, ordre) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            ps.setInt(2, idDestination);
            ps.setInt(3, prochainOrdre);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean dissocier(int idVoyage, int idDestination) {
        String sql = "DELETE FROM VoyageDestination WHERE idVoyage=? AND idDestination=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn. prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            ps.setInt(2, idDestination);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                reorganiserOrdre(idVoyage);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean associationExiste(int idVoyage, int idDestination) {
        String sql = "SELECT COUNT(*) FROM VoyageDestination WHERE idVoyage=? AND idDestination=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn. prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            ps.setInt(2, idDestination);
            ResultSet rs = ps. executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Object[]> getAllAssociations() {
        ArrayList<Object[]> liste = new ArrayList<>();
        String sql = "SELECT v.code, v.dateDepart, d.codeDestination, d.nomVille, vd.ordre " +
                    "FROM VoyageDestination vd " +
                    "JOIN Voyage v ON vd.idVoyage = v. idVoyage " +
                    "JOIN Destination d ON vd.idDestination = d.idDestination " +
                    "ORDER BY v.code, vd.ordre";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt. executeQuery(sql)) {
            while (rs.next()) {
                Object[] row = {
                    rs.getString("code"),
                    rs.getString("dateDepart"),
                    rs. getInt("ordre"),
                    rs.getString("codeDestination"),
                    rs.getString("nomVille")
                };
                liste.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static ArrayList<String> getDestinationsByVoyage(int idVoyage) {
        ArrayList<String> destinations = new ArrayList<>();
        String sql = "SELECT d.nomVille FROM VoyageDestination vd " +
                    "JOIN Destination d ON vd. idDestination = d.idDestination " +
                    "WHERE vd.idVoyage = ?  ORDER BY vd.ordre";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            ResultSet rs = ps. executeQuery();
            while (rs.next()) {
                destinations.add(rs. getString("nomVille"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return destinations;
    }

    public static int countDestinationsByVoyage(int idVoyage) {
        String sql = "SELECT COUNT(*) FROM VoyageDestination WHERE idVoyage=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps. setInt(1, idVoyage);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs. getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getProchainOrdre(int idVoyage) {
        String sql = "SELECT COALESCE(MAX(ordre), 0) + 1 AS prochainOrdre FROM VoyageDestination WHERE idVoyage = ?";
        try (Connection conn = DBConnection. getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVoyage);
            ResultSet rs = ps.executeQuery();
            if (rs. next()) {
                return rs.getInt("prochainOrdre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static void reorganiserOrdre(int idVoyage) {
        String sqlSelect = "SELECT idDestination FROM VoyageDestination WHERE idVoyage = ?  ORDER BY ordre";
        String sqlUpdate = "UPDATE VoyageDestination SET ordre = ? WHERE idVoyage = ?  AND idDestination = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
             PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
            psSelect.setInt(1, idVoyage);
            ResultSet rs = psSelect.executeQuery();
            int nouvelOrdre = 1;
            while (rs.next()) {
                int idDestination = rs.getInt("idDestination");
                psUpdate.setInt(1, nouvelOrdre);
                psUpdate.setInt(2, idVoyage);
                psUpdate.setInt(3, idDestination);
                psUpdate.executeUpdate();
                nouvelOrdre++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}