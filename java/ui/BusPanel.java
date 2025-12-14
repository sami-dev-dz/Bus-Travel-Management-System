package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.BusDAO;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import model.Bus;

public class BusPanel extends JPanel {
    private JTextField txtMatricule;
    private JTextField txtMarque;
    private JTextField txtModele;
    private JTextField txtCapacite;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnRetour; 

    private JTable tableBus;
    private DefaultTableModel tableModel;

    private ArrayList<Bus> listeBus;
    private int idAdministrateurConnecte = 1;

    public BusPanel() {
        listeBus = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerBus();
    }

    public BusPanel(int idAdministrateur) {
        this.idAdministrateurConnecte = idAdministrateur;
        listeBus = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerBus();
    }

    private void initComponents() {
        txtMatricule = new JTextField(15);
        txtMarque = new JTextField(15);
        txtModele = new JTextField(15);
        txtCapacite = new JTextField(10);

        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnNouveau = new JButton("Nouveau");
        btnRetour = new JButton("Retour");

        String[] colonnes = {"Immatriculation", "Marque", "Modèle", "Nombre de places"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBus = new JTable(tableModel);
        tableBus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAjouter.addActionListener(e -> ajouterBus());
        btnModifier.addActionListener(e -> modifierBus());
        btnSupprimer.addActionListener(e -> supprimerBus());
        btnNouveau.addActionListener(e -> nouveauBus());
        btnRetour.addActionListener(e -> retourner());

        tableBus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerBus();
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
        panelFormulaire.setBorder(BorderFactory.createTitledBorder("Informations du Bus"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulaire.add(new JLabel("Immatriculation:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtMatricule, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulaire.add(new JLabel("Marque:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtMarque, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulaire.add(new JLabel("Modèle:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtModele, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulaire.add(new JLabel("Nombre de places:"), gbc);
        gbc.gridx = 1;
        panelFormulaire.add(txtCapacite, gbc);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnRetour);
        
        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelFormulaire, BorderLayout.CENTER);
        panelHaut.add(panelBoutons, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(tableBus);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Bus"));

        add(panelHaut, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void chargerBus() {
        listeBus = BusDAO.getAllBus();
        tableModel.setRowCount(0);
        for (Bus b : listeBus) {
            tableModel.addRow(new Object[]{
                b.getImmatriculation(), 
                b.getMarque(), 
                b.getModele(), 
                b.getNbPlaces()
            });
        }
    }

    private void ajouterBus() {
        if (validerChamps()) {
            String immatriculation = txtMatricule.getText().trim();
            String marque = txtMarque.getText().trim();
            String modele = txtModele.getText().trim();
            int nbPlaces = Integer.parseInt(txtCapacite.getText().trim());

            if (BusDAO.immatriculationExiste(immatriculation)) {
                JOptionPane.showMessageDialog(this,
                        "Un bus avec cette immatriculation existe déjà !",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                txtMatricule.requestFocus();
                return;
            }

            Bus bus = new Bus();
            bus.setImmatriculation(immatriculation);
            bus.setMarque(marque);
            bus.setModele(modele);
            bus.setNbPlaces(nbPlaces);
            bus.setIdAdministrateur(idAdministrateurConnecte);

            if (BusDAO.ajouterBus(bus)) {
                listeBus.add(bus);
                tableModel.addRow(new Object[]{immatriculation, marque, modele, nbPlaces});
                JOptionPane.showMessageDialog(this, "Bus ajouté avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                nouveauBus();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du bus",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierBus() {
        int selectedRow = tableBus.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un bus à modifier",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validerChamps()) {
            String immatriculation = txtMatricule.getText().trim();
            String marque = txtMarque.getText().trim();
            String modele = txtModele.getText().trim();
            int nbPlaces = Integer.parseInt(txtCapacite.getText().trim());

            Bus bus = listeBus.get(selectedRow);
            bus.setImmatriculation(immatriculation);
            bus.setMarque(marque);
            bus.setModele(modele);
            bus.setNbPlaces(nbPlaces);
            bus.setIdAdministrateur(idAdministrateurConnecte);

            if (BusDAO.modifierBus(bus)) {
                tableModel.setValueAt(immatriculation, selectedRow, 0);
                tableModel.setValueAt(marque, selectedRow, 1);
                tableModel.setValueAt(modele, selectedRow, 2);
                tableModel.setValueAt(nbPlaces, selectedRow, 3);
                JOptionPane.showMessageDialog(this, "Bus modifié avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                nouveauBus();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerBus() {
        int selectedRow = tableBus.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un bus à supprimer",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce bus?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            String immatriculation = listeBus.get(selectedRow).getImmatriculation();
            
            if (BusDAO.supprimerBus(immatriculation)) {
                listeBus.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Bus supprimé avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                nouveauBus();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerBus() {
        int selectedRow = tableBus.getSelectedRow();
        if (selectedRow != -1) {
            txtMatricule.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtMarque.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtModele.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtCapacite.setText(tableModel.getValueAt(selectedRow, 3).toString());

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouveauBus() {
        txtMatricule.setText("");
        txtMarque.setText("");
        txtModele.setText("");
        txtCapacite.setText("");

        tableBus.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (txtMatricule.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'immatriculation est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtMatricule.requestFocus();
            return false;
        }

        if (txtMarque.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La marque est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtMarque.requestFocus();
            return false;
        }

        if (txtModele.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le modèle est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtModele.requestFocus();
            return false;
        }

        try {
            int nbPlaces = Integer.parseInt(txtCapacite.getText().trim());
            if (nbPlaces <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le nombre de places doit être un nombre positif",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtCapacite.requestFocus();
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