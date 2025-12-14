package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import utils.DBConnection;
import model.Receptionniste;

public class LoginPanel extends JFrame {
    
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtEmail;
    private JPasswordField txtMotDePasse;
    private JComboBox<String> comboRole;
    private JButton btnConnexion;
    private JButton btnAnnuler;
    private JLabel lblMessageErreur;
    
    private int tentativesEchouees = 0;
    private static final int MAX_TENTATIVES = 5;
    
    public LoginPanel() {
        setTitle("Connexion - Système de Gestion de Transport");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(22, 92, 70));
        titlePanel.setPreferredSize(new Dimension(450, 80));
        JLabel lblTitle = new JLabel("CONNEXION");
        lblTitle. setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle. setForeground(Color.WHITE);
        titlePanel.add(lblTitle);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory. createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints. HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblNom = new JLabel("Nom :");
        lblNom.setFont(new Font("Segoe UI", Font. BOLD, 14));
        formPanel.add(lblNom, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        txtNom = new JTextField(20);
        txtNom.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNom.setPreferredSize(new Dimension(250, 35));
        formPanel.add(txtNom, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lblPrenom = new JLabel("Prénom :");
        lblPrenom.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblPrenom, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        txtPrenom = new JTextField(20);
        txtPrenom.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPrenom. setPreferredSize(new Dimension(250, 35));
        formPanel.add(txtPrenom, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel lblEmail = new JLabel("Email :");
        lblEmail.setFont(new Font("Segoe UI", Font. BOLD, 14));
        formPanel.add(lblEmail, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Segoe UI", Font. PLAIN, 14));
        txtEmail.setPreferredSize(new Dimension(250, 35));
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel lblMotDePasse = new JLabel("Mot de passe :");
        lblMotDePasse.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblMotDePasse, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        txtMotDePasse = new JPasswordField(20);
        txtMotDePasse.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMotDePasse.setPreferredSize(new Dimension(250, 35));
        formPanel. add(txtMotDePasse, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc. weightx = 0;
        JLabel lblRole = new JLabel("Rôle :");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblRole, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        String[] roles = {"Administrateur", "Réceptionniste"};
        comboRole = new JComboBox<>(roles);
        comboRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboRole.setPreferredSize(new Dimension(250, 35));
        comboRole.setBackground(Color. WHITE);
        formPanel.add(comboRole, gbc);
        
        lblMessageErreur = new JLabel(" ");
        lblMessageErreur.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMessageErreur.setForeground(new Color(192, 57, 43));
        lblMessageErreur.setHorizontalAlignment(SwingConstants. CENTER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc. gridwidth = 2;
        formPanel.add(lblMessageErreur, gbc);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        btnConnexion = new JButton("Se connecter");
        btnConnexion.setFont(new Font("Segoe UI", Font. BOLD, 14));
        btnConnexion.setPreferredSize(new Dimension(150, 40));
        btnConnexion.setBackground(new Color(46, 204, 113));
        btnConnexion.setForeground(Color.WHITE);
        btnConnexion.setFocusPainted(false);
        btnConnexion.setBorderPainted(false);
        btnConnexion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAnnuler. setPreferredSize(new Dimension(150, 40));
        btnAnnuler.setBackground(new Color(149, 165, 166));
        btnAnnuler.setForeground(Color.WHITE);
        btnAnnuler.setFocusPainted(false);
        btnAnnuler.setBorderPainted(false);
        btnAnnuler.setCursor(new Cursor(Cursor. HAND_CURSOR));
        
        buttonPanel.add(btnConnexion);
        buttonPanel.add(btnAnnuler);
        
        btnConnexion.addActionListener(e -> connecter());
        btnAnnuler.addActionListener(e -> annuler());
        txtMotDePasse.addActionListener(e -> connecter());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        footerPanel.setPreferredSize(new Dimension(900, 40));
        JLabel footerLabel = new JLabel("© 2025 - Système de Transport | Version 1.0. 0");
        footerLabel. setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(189, 195, 199));
        footerPanel.add(footerLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void afficherErreur(String message) {
        lblMessageErreur.setText(message);
        lblMessageErreur.setForeground(new Color(192, 57, 43));
    }
    
    private void cacherErreur() {
        lblMessageErreur.setText(" ");
    }
    
    private void connecter() {
        cacherErreur();
        
        if (tentativesEchouees >= MAX_TENTATIVES) {
            JOptionPane.showMessageDialog(this, 
                "Nombre maximum de tentatives atteint (" + MAX_TENTATIVES + ").\nL'application va se fermer pour des raisons de sécurité.", 
                "Accès bloqué", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }
        
        String nom = txtNom.getText(). trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail. getText().trim();
        String motDePasse = new String(txtMotDePasse. getPassword()).trim();
        String roleSelectionne = (String) comboRole.getSelectedItem();
        
        if (nom.isEmpty()) {
            afficherErreur("Le champ 'Nom' est obligatoire");
            txtNom.requestFocus();
            return;
        }
        
        if (prenom.isEmpty()) {
            afficherErreur("Le champ 'Prénom' est obligatoire");
            txtPrenom.requestFocus();
            return;
        }
        
        if (email.isEmpty()) {
            afficherErreur("Le champ 'Email' est obligatoire");
            txtEmail.requestFocus();
            return;
        }
        
        if (! email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            afficherErreur("Format d'email invalide");
            txtEmail.requestFocus();
            return;
        }
        
        if (motDePasse. isEmpty()) {
            afficherErreur("Le champ 'Mot de passe' est obligatoire");
            txtMotDePasse.requestFocus();
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String query = "SELECT idUtilisateur, nom, prenom, email, motDePasse, role " +
                          "FROM Utilisateur " +
                          "WHERE nom = ? AND prenom = ?  AND email = ?  AND role = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setString(4, roleSelectionne);
            
            rs = pstmt.executeQuery();
            
            if (rs. next()) {
                String motDePasseHash = rs.getString("motDePasse");
                
                boolean motDePasseCorrect = BCrypt.checkpw(motDePasse, motDePasseHash);
                
                if (motDePasseCorrect) {
                    tentativesEchouees = 0;
                    
                    int idUtilisateur = rs. getInt("idUtilisateur");
                    String nomBD = rs.getString("nom");
                    String prenomBD = rs.getString("prenom");
                    String emailBD = rs.getString("email");
                    String roleBD = rs.getString("role");
                    
                    Receptionniste currentUser = new Receptionniste(idUtilisateur, nomBD, prenomBD, emailBD, motDePasseHash, roleBD);
                    
                    this.dispose();
                    
                    SwingUtilities.invokeLater(() -> new MainFrame(currentUser). setVisible(true));
                    
                } else {
                    tentativesEchouees++;
                    afficherErreur("Identifiants incorrects (Tentative " + tentativesEchouees + "/" + MAX_TENTATIVES + ")");
                    txtMotDePasse.setText("");
                    txtMotDePasse.requestFocus();
                }
            } else {
                tentativesEchouees++;
                afficherErreur("Identifiants incorrects (Tentative " + tentativesEchouees + "/" + MAX_TENTATIVES + ")");
                txtMotDePasse.setText("");
            }
            
        } catch (SQLException e) {
            afficherErreur("Erreur de connexion à la base de données");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void annuler() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtEmail.setText("");
        txtMotDePasse.setText("");
        comboRole.setSelectedIndex(0);
        cacherErreur();
        tentativesEchouees = 0;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager. getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}