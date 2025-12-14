package dao;

import model.Bus;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;

public class BusDAO {

    public static ArrayList<Bus> getAllBus() {
        ArrayList<Bus> liste = new ArrayList<>();
        String sql = "SELECT * FROM Bus";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bus bus = new Bus();
                bus.setIdBus(rs.getInt("idBus"));
                bus.setImmatriculation(rs.getString("immatriculation"));
                bus.setMarque(rs.getString("marque"));
                bus.setModele(rs.getString("modele"));
                bus.setNbPlaces(rs.getInt("nbPlaces"));
                bus.setIdAdministrateur(rs.getInt("idAdministrateur"));
                liste.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static boolean ajouterBus(Bus bus) {
        String sql = "INSERT INTO Bus (immatriculation, marque, modele, nbPlaces, idAdministrateur) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bus.getImmatriculation());
            pstmt.setString(2, bus.getMarque());
            pstmt.setString(3, bus.getModele());
            pstmt.setInt(4, bus.getNbPlaces());
            pstmt.setInt(5, bus.getIdAdministrateur());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean modifierBus(Bus bus) {
        String sql = "UPDATE Bus SET immatriculation = ?, marque = ?, modele = ?, nbPlaces = ?, idAdministrateur = ? WHERE idBus = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bus.getImmatriculation());
            pstmt.setString(2, bus.getMarque());
            pstmt.setString(3, bus.getModele());
            pstmt.setInt(4, bus.getNbPlaces());
            pstmt.setInt(5, bus.getIdAdministrateur());
            pstmt.setInt(6, bus.getIdBus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean supprimerBus(String immatriculation) {
        String sql = "DELETE FROM Bus WHERE immatriculation = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, immatriculation);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean immatriculationExiste(String immatriculation) {
        String sql = "SELECT COUNT(*) FROM Bus WHERE immatriculation = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, immatriculation);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}