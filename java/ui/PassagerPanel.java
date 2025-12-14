package ui;

import dao.PassagerDAO;
import model.Passager;

import javax.swing.*;
import javax.swing.table. DefaultTableModel;
import java. awt.*;
import java.awt. event.*;
import java.util.ArrayList;

public class PassagerPanel extends JPanel {
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JTextField txtAdresse;
    private JTextField txtTelephone;
    private JTextField txtRecherche;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnRechercher;
    private JButton btnActualiser;
    private JButton btnRetour;

    private JTable tablePassagers;
    private DefaultTableModel tableModel;

    private ArrayList<Passager> listePassagers;

    public PassagerPanel() {
        listePassagers = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerPassagers();
    }

    private void initComponents() {
        txtNom = new JTextField(15);
        txtPrenom = new JTextField(15);
        txtAdresse = new JTextField(15);
        txtTelephone = new JTextField(15);
        txtRecherche = new JTextField(20);

        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnNouveau = new JButton("Nouveau");
        btnRechercher = new JButton("Rechercher");
        btnActualiser = new JButton("Actualiser");
        btnRetour = new JButton("Retour");

        String[] colonnes = {"Nom", "Prénom", "Adresse", "Téléphone"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePassagers = new JTable(tableModel);
        tablePassagers. setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAjouter.addActionListener(e -> ajouterPassager());
        btnModifier.addActionListener(e -> modifierPassager());
        btnSupprimer.addActionListener(e -> supprimerPassager());
        btnNouveau.addActionListener(e -> nouveauPassager());
        btnRechercher. addActionListener(e -> rechercherPassagers());
        btnActualiser. addActionListener(e -> chargerPassagers());
        btnRetour.addActionListener(e -> retourner());

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    rechercherPassagers();
                }
            }
        });

        tablePassagers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerPassager();
                }
            }
        });

        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelRecherche.setBorder(BorderFactory.createTitledBorder("Recherche"));
        panelRecherche. add(new JLabel("Rechercher: "));
        panelRecherche.add(txtRecherche);
        panelRecherche.add(btnRechercher);
        panelRecherche.add(btnActualiser);

        JPanel panelFormulaire = new JPanel(new GridBagLayout());
        panelFormulaire.setBorder(BorderFactory.createTitledBorder("Informations du Passager"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulaire.add(new JLabel("Nom: "), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtNom, gbc);

        gbc.gridx = 0;
        gbc. gridy = 1;
        panelFormulaire.add(new JLabel("Prénom: "), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtPrenom, gbc);

        gbc.gridx = 0;
        gbc. gridy = 2;
        panelFormulaire.add(new JLabel("Adresse: "), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtAdresse, gbc);

        gbc.gridx = 0;
        gbc. gridy = 3;
        panelFormulaire.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1;
        panelFormulaire. add(txtTelephone, gbc);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons. add(btnSupprimer);
        panelBoutons.add(btnRetour);

        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelRecherche, BorderLayout. NORTH);
        panelHaut.add(panelFormulaire, BorderLayout.CENTER);
        panelHaut.add(panelBoutons, BorderLayout. SOUTH);

        JScrollPane scrollPane = new JScrollPane(tablePassagers);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Passagers"));

        add(panelHaut, BorderLayout. NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void chargerPassagers() {
        listePassagers = PassagerDAO.getAllPassagers();
        afficherPassagersDansTable();
        nouveauPassager();
    }

    private void rechercherPassagers() {
        String critere = txtRecherche.getText().trim();
        if (critere.isEmpty()) {
            chargerPassagers();
        } else {
            listePassagers = PassagerDAO.rechercher(critere);
            afficherPassagersDansTable();
        }
        nouveauPassager();
    }

    private void afficherPassagersDansTable() {
        tableModel.setRowCount(0);
        if (listePassagers == null) {
            listePassagers = new ArrayList<>();
        }
        for (Passager p : listePassagers) {
            tableModel.addRow(new Object[]{
                p.getNom(),
                p.getPrenom(),
                p.getAdresse(),
                p.getTelephone()
            });
        }
    }

    private void ajouterPassager() {
        if (validerChamps()) {
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom. getText().trim();
            String adresse = txtAdresse.getText().trim();
            String telephone = txtTelephone.getText().trim();

            Passager passager = new Passager(nom, prenom, adresse, telephone);

            if (PassagerDAO.ajouter(passager)) {
                JOptionPane.showMessageDialog(this, "Passager ajouté avec succès! ",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerPassagers();
                nouveauPassager();
            } else {
                JOptionPane. showMessageDialog(this, "Erreur lors de l'ajout du passager",
                        "Erreur", JOptionPane. ERROR_MESSAGE);
            }
        }
    }

    private void modifierPassager() {
        int selectedRow = tablePassagers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un passager à modifier",
                    "Avertissement", JOptionPane. WARNING_MESSAGE);
            return;
        }

        if (validerChamps()) {
            Passager passagerOriginal = listePassagers.get(selectedRow);
            int id = passagerOriginal.getIdPassager();

            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            String adresse = txtAdresse.getText().trim();
            String telephone = txtTelephone. getText().trim();

            Passager passager = new Passager(nom, prenom, adresse, telephone);
            passager. setIdPassager(id);

            if (PassagerDAO.modifier(passager)) {
                JOptionPane.showMessageDialog(this, "Passager modifié avec succès! ",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerPassagers();
                nouveauPassager();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerPassager() {
        int selectedRow = tablePassagers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane. showMessageDialog(this, "Veuillez sélectionner un passager à supprimer",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Passager passager = listePassagers.get(selectedRow);

        int confirmation = JOptionPane. showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce passager?\n" +
                "Nom: " + passager.getNom() + " " + passager.getPrenom(),
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            if (PassagerDAO.supprimer(passager.getIdPassager())) {
                JOptionPane.showMessageDialog(this, "Passager supprimé avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerPassagers();
                nouveauPassager();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression\nCe passager a peut-être des réservations.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerPassager() {
        int selectedRow = tablePassagers.getSelectedRow();
        if (selectedRow != -1 && selectedRow < listePassagers.size()) {
            Passager passager = listePassagers.get(selectedRow);

            txtNom.setText(passager.getNom());
            txtPrenom.setText(passager. getPrenom());
            txtAdresse.setText(passager.getAdresse());
            txtTelephone.setText(passager.getTelephone());

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouveauPassager() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtAdresse.setText("");
        txtTelephone.setText("");

        tablePassagers.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (txtNom. getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtNom.requestFocus();
            return false;
        }

        if (txtPrenom.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le prénom est obligatoire",
                    "Erreur de validation", JOptionPane. ERROR_MESSAGE);
            txtPrenom.requestFocus();
            return false;
        }

        if (txtAdresse.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'adresse est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtAdresse.requestFocus();
            return false;
        }

        if (txtTelephone. getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le téléphone est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtTelephone.requestFocus();
            return false;
        }

        return true;
    }

    private void retourner() {
        Container parent = this.getParent();
        if (parent instanceof JPanel) {
            CardLayout layout = (CardLayout) parent.getLayout();
            layout.show(parent, "MENU");
        }
    }
}