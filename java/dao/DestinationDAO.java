package dao;

import java.sql.*;
import java.util.ArrayList;
import model.Destination;
import utils.DBConnection;

public class DestinationDAO {
    
    public static boolean ajouterDestination(Destination dest) {
        String sql = "INSERT INTO Destination (codeDestination, nomVille, idAdministrateur) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dest.getCodeDestination());
            ps.setString(2, dest.getNomVille());
            ps.setInt(3, dest.getIdAdministrateur());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean modifierDestination(Destination dest) {
        String sql = "UPDATE Destination SET nomVille=?, idAdministrateur=? WHERE idDestination=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dest.getNomVille());
            ps.setInt(2, dest.getIdAdministrateur());
            ps.setInt(3, dest.getIdDestination());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean supprimerDestination(String codeDestination) {
        String sql = "DELETE FROM Destination WHERE codeDestination=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codeDestination);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Destination> getAllDestination() {
        ArrayList<Destination> listeDest = new ArrayList<>();
        String sql = "SELECT * FROM Destination";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Destination dest = new Destination();
                dest.setIdDestination(rs.getInt("idDestination"));
                dest.setCodeDestination(rs.getString("codeDestination"));
                dest.setNomVille(rs.getString("nomVille"));
                dest.setIdAdministrateur(rs.getInt("idAdministrateur"));
                listeDest.add(dest);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listeDest;
    }

    public static boolean codeDestinationExiste(String codeDestination) {
        String sql = "SELECT COUNT(*) FROM Destination WHERE codeDestination=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codeDestination);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Destination getDestinationByCode(String codeDestination) {
        String sql = "SELECT * FROM Destination WHERE codeDestination=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codeDestination);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Destination dest = new Destination();
                dest.setIdDestination(rs.getInt("idDestination"));
                dest.setCodeDestination(rs.getString("codeDestination"));
                dest.setNomVille(rs.getString("nomVille"));
                dest.setIdAdministrateur(rs.getInt("idAdministrateur"));
                return dest;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}