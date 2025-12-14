package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ReceptionnisteDAO;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import model.Receptionniste;
import org.mindrot.jbcrypt.BCrypt;

public class ReceptionnistePanel extends JPanel {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$";
    private static final String TITRE_ERREUR = "Erreur de validation";
    private static final String TITRE_SUCCES = "Opération réussie";
    private static final String TITRE_CONFIRMATION = "Confirmation";
    
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtEmail;
    private JPasswordField txtMotDePasse;
    private JToggleButton btnVoirMotDePasse;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnRetour;

    private JTable tableReceptionniste;
    private DefaultTableModel tableModel;

    private ArrayList<Receptionniste> listeReceptionnistes;
    private String emailSelectionne;

    public ReceptionnistePanel() {
        listeReceptionnistes = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerReceptionnistes();
    }

    private void initComponents() {
        txtNom = new JTextField(15);
        txtPrenom = new JTextField(15);
        txtEmail = new JTextField(20);
        txtMotDePasse = new JPasswordField(15);
        btnVoirMotDePasse = new JToggleButton("👁");
        btnVoirMotDePasse.setPreferredSize(new Dimension(45, 25));
        btnVoirMotDePasse.setFocusable(false);

        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnNouveau = new JButton("Nouveau");
        btnRetour = new JButton("Retour");

        String[] colonnes = {"Nom", "Prénom", "Email"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReceptionniste = new JTable(tableModel);
        tableReceptionniste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAjouter.addActionListener(e -> ajouterReceptionniste());
        btnModifier.addActionListener(e -> modifierReceptionniste());
        btnSupprimer.addActionListener(e -> supprimerReceptionniste());
        btnNouveau.addActionListener(e -> nouveauReceptionniste());
        btnRetour.addActionListener(e -> retourner());

        btnVoirMotDePasse.addActionListener(e -> {
            if (btnVoirMotDePasse.isSelected()) {
                txtMotDePasse.setEchoChar((char) 0);
            } else {
                txtMotDePasse.setEchoChar('•');
            }
        });

        tableReceptionniste.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerReceptionniste();
                }
            }
        });

        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormulaire = new JPanel(new GridBagLayout());
        panelFormulaire.setBorder(BorderFactory.createTitledBorder("Informations du Réceptionniste"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulaire.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtNom, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulaire.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtPrenom, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulaire.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulaire.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        JPanel panelMotDePasse = new JPanel(new BorderLayout(5, 0));
        panelMotDePasse.add(txtMotDePasse, BorderLayout.CENTER);
        panelMotDePasse.add(btnVoirMotDePasse, BorderLayout.EAST);
        panelFormulaire.add(panelMotDePasse, gbc);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnRetour);

        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelFormulaire, BorderLayout.CENTER);
        panelHaut.add(panelBoutons, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(tableReceptionniste);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Réceptionnistes"));

        add(panelHaut, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void chargerReceptionnistes() {
        listeReceptionnistes = ReceptionnisteDAO.getAllReceptionnistes();
        tableModel.setRowCount(0);
        for (Receptionniste r : listeReceptionnistes) {
            tableModel.addRow(new Object[]{r.getNom(), r.getPrenom(), r.getEmail()});
        }
    }

    private void ajouterReceptionniste() {
        String messageErreur = validerChamps();
        if (messageErreur != null) {
            afficherErreur(messageErreur);
            return;
        }

        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String motDePasse = new String(txtMotDePasse.getPassword());

        if (ReceptionnisteDAO.emailExiste(email)) {
            afficherErreur("Un utilisateur avec l'adresse email \"" + email + "\" existe déjà dans le système.");
            txtEmail.requestFocus();
            txtEmail.selectAll();
            return;
        }

        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Receptionniste receptionniste = new Receptionniste(nom, prenom, email, hash);

        if (ReceptionnisteDAO.ajouterReceptionniste(receptionniste)) {
            chargerReceptionnistes();
            afficherSucces("Le réceptionniste " + prenom + " " + nom + " a été ajouté avec succès.");
            nouveauReceptionniste();
        } else {
            afficherErreur("Erreur lors de l'ajout du réceptionniste.");
        }
    }

    private void modifierReceptionniste() {
        int selectedRow = tableReceptionniste.getSelectedRow();
        if (selectedRow == -1) {
            afficherErreur("Veuillez sélectionner un réceptionniste à modifier.");
            return;
        }

        String messageErreur = validerChamps();
        if (messageErreur != null) {
            afficherErreur(messageErreur);
            return;
        }

        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String motDePasse = new String(txtMotDePasse.getPassword());

        if (!emailSelectionne.equalsIgnoreCase(email) && ReceptionnisteDAO.emailExiste(email)) {
            afficherErreur("L'adresse email \"" + email + "\" est déjà utilisée par un autre utilisateur.");
            txtEmail.requestFocus();
            txtEmail.selectAll();
            return;
        }

        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Receptionniste receptionniste = new Receptionniste(nom, prenom, email, hash);

        if (ReceptionnisteDAO.modifierReceptionniste(receptionniste, emailSelectionne)) {
            chargerReceptionnistes();
            afficherSucces("Les informations du réceptionniste ont été mises à jour avec succès.");
            nouveauReceptionniste();
        } else {
            afficherErreur("Erreur lors de la modification du réceptionniste.");
        }
    }

    private void supprimerReceptionniste() {
        int selectedRow = tableReceptionniste.getSelectedRow();
        if (selectedRow == -1) {
            afficherErreur("Veuillez sélectionner un réceptionniste à supprimer.");
            return;
        }

        String nom = tableModel.getValueAt(selectedRow, 0).toString();
        String prenom = tableModel.getValueAt(selectedRow, 1).toString();
        String message = "Êtes-vous sûr de vouloir supprimer le réceptionniste " + 
                        prenom + " " + nom + " ?\n\n" +
                        "Cette action est irréversible.";

        int confirmation = JOptionPane.showConfirmDialog(
            this,
            message,
            TITRE_CONFIRMATION,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            if (ReceptionnisteDAO.supprimerReceptionniste(emailSelectionne)) {
                chargerReceptionnistes();
                afficherSucces("Le réceptionniste a été supprimé avec succès.");
                nouveauReceptionniste();
            } else {
                afficherErreur("Erreur lors de la suppression du réceptionniste.");
            }
        }
    }

    private void selectionnerReceptionniste() {
        int selectedRow = tableReceptionniste.getSelectedRow();
        if (selectedRow != -1) {
            txtNom.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtPrenom.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtEmail.setText(tableModel.getValueAt(selectedRow, 2).toString());
            emailSelectionne = tableModel.getValueAt(selectedRow, 2).toString();
            txtMotDePasse.setText("");

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouveauReceptionniste() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtEmail.setText("");
        txtMotDePasse.setText("");
        emailSelectionne = null;
        tableReceptionniste.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
        txtNom.requestFocus();
    }

    private String validerChamps() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        char[] motDePasse = txtMotDePasse.getPassword();

        if (nom.isEmpty()) {
            txtNom.requestFocus();
            return "Le champ 'Nom' est obligatoire.";
        }

        if (nom.length() < 2) {
            txtNom.requestFocus();
            txtNom.selectAll();
            return "Le nom doit contenir au moins 2 caractères.";
        }

        if (prenom.isEmpty()) {
            txtPrenom.requestFocus();
            return "Le champ 'Prénom' est obligatoire.";
        }

        if (prenom.length() < 2) {
            txtPrenom.requestFocus();
            txtPrenom.selectAll();
            return "Le prénom doit contenir au moins 2 caractères.";
        }

        if (email.isEmpty()) {
            txtEmail.requestFocus();
            return "Le champ 'Email' est obligatoire.";
        }

        if (!email.matches(EMAIL_REGEX)) {
            txtEmail.requestFocus();
            txtEmail.selectAll();
            return "L'adresse email \"" + email + "\" n'est pas valide.\nFormat attendu: exemple@domaine.com";
        }

        if (motDePasse.length == 0) {
            txtMotDePasse.requestFocus();
            return "Le champ 'Mot de passe' est obligatoire.";
        }

        if (motDePasse.length < 6) {
            txtMotDePasse.requestFocus();
            txtMotDePasse.selectAll();
            return "Le mot de passe doit contenir au moins 6 caractères.";
        }

        return null;
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            TITRE_ERREUR,
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void afficherSucces(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            TITRE_SUCCES,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void retourner() {
        Container parent = this.getParent();
        if (parent instanceof JPanel) {
            CardLayout layout = (CardLayout) parent.getLayout();
            layout.show(parent, "MENU");
        }
    }

    public static void main(String[] args) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}