package dao;

import java.sql.*;
import java.util.ArrayList;
import model.Receptionniste;
import utils.DBConnection;

public class ReceptionnisteDAO {

    public static boolean ajouterReceptionniste(Receptionniste r) {
        Connection conn = null;
        PreparedStatement psUtilisateur = null;
        PreparedStatement psReceptionniste = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlUtilisateur = "INSERT INTO Utilisateur (nom, prenom, email, motDePasse, role) VALUES (?, ?, ?, ?, ?)";
            psUtilisateur = conn.prepareStatement(sqlUtilisateur, Statement.RETURN_GENERATED_KEYS);
            psUtilisateur.setString(1, r.getNom());
            psUtilisateur.setString(2, r.getPrenom());
            psUtilisateur.setString(3, r.getEmail());
            psUtilisateur.setString(4, r.getMotDePasse());
            psUtilisateur.setString(5, "Réceptionniste");
            psUtilisateur.executeUpdate();

            generatedKeys = psUtilisateur.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idUtilisateur = generatedKeys.getInt(1);

                String sqlReceptionniste = "INSERT INTO Receptionniste (idReceptionniste) VALUES (?)";
                psReceptionniste = conn.prepareStatement(sqlReceptionniste);
                psReceptionniste.setInt(1, idUtilisateur);
                psReceptionniste.executeUpdate();

                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (psReceptionniste != null) psReceptionniste.close();
                if (psUtilisateur != null) psUtilisateur.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean modifierReceptionniste(Receptionniste r, String ancienEmail) {
        String sql = "UPDATE Utilisateur SET nom=?, prenom=?, email=?, motDePasse=? WHERE email=? AND role='Réceptionniste'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getNom());
            ps.setString(2, r.getPrenom());
            ps.setString(3, r.getEmail());
            ps.setString(4, r.getMotDePasse());
            ps.setString(5, ancienEmail);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean supprimerReceptionniste(String email) {
        String sql = "DELETE FROM Utilisateur WHERE email=? AND role='Réceptionniste'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Receptionniste> getAllReceptionnistes() {
        ArrayList<Receptionniste> liste = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur WHERE role='Réceptionniste' ORDER BY nom, prenom";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Receptionniste r = new Receptionniste();
                r.setIdUtilisateur(rs.getInt("idUtilisateur"));
                r.setNom(rs.getString("nom"));
                r.setPrenom(rs.getString("prenom"));
                r.setEmail(rs.getString("email"));
                r.setMotDePasse(rs.getString("motDePasse"));
                r.setRole(rs.getString("role"));
                liste.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }

    public static boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM Utilisateur WHERE email=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Receptionniste getReceptionnisteByEmail(String email) {
        String sql = "SELECT * FROM Utilisateur WHERE email=? AND role='Réceptionniste'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Receptionniste r = new Receptionniste();
                r.setIdUtilisateur(rs.getInt("idUtilisateur"));
                r.setNom(rs.getString("nom"));
                r.setPrenom(rs.getString("prenom"));
                r.setEmail(rs.getString("email"));
                r.setMotDePasse(rs.getString("motDePasse"));
                r.setRole(rs.getString("role"));
                return r;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}