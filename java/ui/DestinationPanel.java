package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.DestinationDAO;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import model.Destination;

public class DestinationPanel extends JPanel {
    private JTextField txtCodeDestination;
    private JTextField txtNomVille;
    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnRetour; 
    private JTable tableDestination;
    private DefaultTableModel tableModel;
    private ArrayList<Destination> listeDestinations;
    private int idAdministrateurConnecte = 1;

    public DestinationPanel() {
        listeDestinations = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerDestinations();
    }

    public DestinationPanel(int idAdministrateur) {
        this.idAdministrateurConnecte = idAdministrateur;
        listeDestinations = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerDestinations();
    }

    private void initComponents() {
        txtCodeDestination = new JTextField(15);
        txtNomVille = new JTextField(15);

        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnNouveau = new JButton("Nouveau");
        btnRetour = new JButton("Retour"); 

        String[] colonnes = {"Code Destination", "Nom Ville"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDestination = new JTable(tableModel);
        tableDestination.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAjouter.addActionListener(e -> ajouterDestination());
        btnModifier.addActionListener(e -> modifierDestination());
        btnSupprimer.addActionListener(e -> supprimerDestination());
        btnNouveau.addActionListener(e -> nouvelleDestination());
        btnRetour.addActionListener(e -> retourner()); 

        tableDestination.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerDestination();
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
        panelFormulaire.setBorder(BorderFactory.createTitledBorder("Informations de la Destination"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulaire.add(new JLabel("Code Destination:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtCodeDestination, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulaire.add(new JLabel("Nom Ville:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtNomVille, gbc);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnRetour); 

        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelFormulaire, BorderLayout.CENTER);
        panelHaut.add(panelBoutons, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(tableDestination);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Destinations"));

        add(panelHaut, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void chargerDestinations() {
        listeDestinations = DestinationDAO.getAllDestination();
        tableModel.setRowCount(0);
        for (Destination d : listeDestinations) {
            tableModel.addRow(new Object[]{d.getCodeDestination(), d.getNomVille()});
        }
    }

    private void ajouterDestination() {
        if (validerChamps()) {
            String codeDestination = txtCodeDestination.getText().trim();
            String nomVille = txtNomVille.getText().trim();

            if (DestinationDAO.codeDestinationExiste(codeDestination)) {
                JOptionPane.showMessageDialog(this,
                        "Une destination avec ce code existe déjà!",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                txtCodeDestination.requestFocus();
                return;
            }

            Destination destination = new Destination();
            destination.setCodeDestination(codeDestination);
            destination.setNomVille(nomVille);
            destination.setIdAdministrateur(idAdministrateurConnecte);

            if (DestinationDAO.ajouterDestination(destination)) {
                listeDestinations.add(destination);
                tableModel.addRow(new Object[]{codeDestination, nomVille});
                JOptionPane.showMessageDialog(this, "Destination ajoutée avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                nouvelleDestination();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la destination",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierDestination() {
        int selectedRow = tableDestination.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une destination à modifier",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validerChamps()) {
            String codeDestination = txtCodeDestination.getText().trim();
            String nomVille = txtNomVille.getText().trim();

            Destination destination = listeDestinations.get(selectedRow);
            destination.setCodeDestination(codeDestination);
            destination.setNomVille(nomVille);
            destination.setIdAdministrateur(idAdministrateurConnecte);

            if (DestinationDAO.modifierDestination(destination)) {
                tableModel.setValueAt(codeDestination, selectedRow, 0);
                tableModel.setValueAt(nomVille, selectedRow, 1);
                JOptionPane.showMessageDialog(this, "Destination modifiée avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                nouvelleDestination();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerDestination() {
        int selectedRow = tableDestination.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une destination à supprimer",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cette destination?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            String codeDestination = listeDestinations.get(selectedRow).getCodeDestination();
            
            if (DestinationDAO.supprimerDestination(codeDestination)) {
                listeDestinations.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Destination supprimée avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                nouvelleDestination();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerDestination() {
        int selectedRow = tableDestination.getSelectedRow();
        if (selectedRow != -1) {
            txtCodeDestination.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtNomVille.setText(tableModel.getValueAt(selectedRow, 1).toString());

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouvelleDestination() {
        txtCodeDestination.setText("");
        txtNomVille.setText("");

        tableDestination.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (txtCodeDestination.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le code destination est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtCodeDestination.requestFocus();
            return false;
        }

        if (txtNomVille.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom de la ville est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtNomVille.requestFocus();
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

    public static void main(String[] args) {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
    }
}