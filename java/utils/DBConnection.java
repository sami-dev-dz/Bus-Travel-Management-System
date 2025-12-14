package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_URL = "jdbc:sqlite:C:/Users/sami-gh/Desktop/GestionVoyage/ressources/gestionvoyages.db";
    private static final String SQL_FILE = "C:/Users/sami-gh/Desktop/GestionVoyage/ressources/initial_db.sql";
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initDB(); 
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la base :");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void initDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            boolean tableExists = false;
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='Utilisateur'")) {
                tableExists = rs.next();
            }

            if (!tableExists) {
                System.out.println("Initialisation de la base de données...");
                
                if (Files.exists(Paths.get(SQL_FILE))) {
                    String sqlFile = new String(Files.readAllBytes(Paths.get(SQL_FILE)));
                    String[] statements = sqlFile.split(";");
                    
                    for (String sql : statements) {
                        sql = sql.trim();
                        if (!sql.isEmpty() && !sql.startsWith("--")) {
                            try {
                                stmt.execute(sql);
                            } catch (SQLException e) {
                                System.err.println("Erreur SQL: " + e.getMessage());
                                System.err.println("Statement: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                            }
                        }
                    }
                    System.out.println("✅ Base et tables initialisées avec succès !");
                } else {
                    System.err.println("❌ Fichier SQL introuvable : " + SQL_FILE);
                }
            } else {
                System.out.println("✅ Base de données déjà initialisée.");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de la base :");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connexion réussie à la base de données !");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM Utilisateur");
            if (rs.next()) {
                System.out.println("Nombre d'utilisateurs : " + rs.getInt("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion :");
            e.printStackTrace();
        }
    }
}