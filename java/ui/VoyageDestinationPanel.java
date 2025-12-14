package ui;

import javax. swing.*;
import javax.swing.table.DefaultTableModel;
import dao.VoyageDAO;
import dao. DestinationDAO;
import dao.VoyageDestinationDAO;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import model.Voyage;
import model.Destination;

public class VoyageDestinationPanel extends JPanel {
    private JComboBox<String> cmbVoyage;
    private JComboBox<String> cmbDestination;

    private JButton btnAssocier;
    private JButton btnDissocier;
    private JButton btnNouveau;
    private JButton btnActualiser;
    private JButton btnRetour;

    private JTable tableAssociations;
    private DefaultTableModel tableModel;

    private ArrayList<Voyage> listeVoyages;
    private ArrayList<Destination> listeDestinations;
    private HashMap<String, Integer> voyageMap;
    private HashMap<String, Integer> destinationMap;

    public VoyageDestinationPanel() {
        listeVoyages = new ArrayList<>();
        listeDestinations = new ArrayList<>();
        voyageMap = new HashMap<>();
        destinationMap = new HashMap<>();
        initComponents();
        layoutComponents();
        chargerDonnees();
    }

    private void initComponents() {
        cmbVoyage = new JComboBox<>();
        cmbDestination = new JComboBox<>();

        btnAssocier = new JButton("Associer");
        btnDissocier = new JButton("Dissocier");
        btnNouveau = new JButton("Nouveau");
        btnActualiser = new JButton("Actualiser");
        btnRetour = new JButton("Retour");

        String[] colonnes = {"Code Voyage", "Date Départ", "Étape", "Code Destination", "Ville"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAssociations = new JTable(tableModel);
        tableAssociations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAssocier.addActionListener(e -> associer());
        btnDissocier.addActionListener(e -> dissocier());
        btnNouveau.addActionListener(e -> nouveau());
        btnActualiser.addActionListener(e -> chargerDonnees());
        btnRetour.addActionListener(e -> retourner());

        tableAssociations.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerAssociation();
                }
            }
        });

        btnDissocier.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory. createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormulaire = new JPanel(new GridBagLayout());
        panelFormulaire. setBorder(BorderFactory.createTitledBorder("Association Voyage - Destination"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulaire.add(new JLabel("Voyage:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelFormulaire. add(cmbVoyage, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panelFormulaire.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelFormulaire.add(cmbDestination, gbc);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAssocier);
        panelBoutons.add(btnDissocier);
        panelBoutons.add(btnActualiser);
        panelBoutons.add(btnRetour);

        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelFormulaire, BorderLayout.CENTER);
        panelHaut.add(panelBoutons, BorderLayout. SOUTH);

        JPanel panelTable = new JPanel(new BorderLayout(5, 5));
        
        JLabel lblInfo = new JLabel("Les destinations sont listées dans l'ordre du trajet (étape 1 → étape 2 → ...)");
        lblInfo.setFont(new Font("Segoe UI", Font. ITALIC, 12));
        lblInfo.setForeground(new Color(100, 100, 100));
        lblInfo.setBorder(BorderFactory. createEmptyBorder(5, 10, 5, 10));
        
        JScrollPane scrollPane = new JScrollPane(tableAssociations);
        scrollPane. setBorder(BorderFactory.createTitledBorder("Liste des Associations Voyage-Destination"));
        
        panelTable.add(lblInfo, BorderLayout.NORTH);
        panelTable. add(scrollPane, BorderLayout.CENTER);

        add(panelHaut, BorderLayout.NORTH);
        add(panelTable, BorderLayout.CENTER);
    }

    private void chargerDonnees() {
        chargerVoyages();
        chargerDestinations();
        chargerAssociations();
    }

    private void chargerVoyages() {
        cmbVoyage. removeAllItems();
        voyageMap.clear();
        listeVoyages = VoyageDAO. getAllVoyage();

        if (listeVoyages == null || listeVoyages.isEmpty()) {
            cmbVoyage. addItem("Aucun voyage disponible");
            cmbVoyage. setEnabled(false);
            btnAssocier.setEnabled(false);
            return;
        }

        cmbVoyage. setEnabled(true);
        btnAssocier.setEnabled(true);

        for (Voyage v : listeVoyages) {
            String dateStr = v.getDateDepart() != null ? v.getDateDepart().toString() : "Date inconnue";
            String display = v.getCode() + " - " + dateStr;
            cmbVoyage.addItem(display);
            voyageMap.put(display, v. getIdVoyage());
        }
    }

    private void chargerDestinations() {
        cmbDestination.removeAllItems();
        destinationMap.clear();
        listeDestinations = DestinationDAO.getAllDestination();

        if (listeDestinations == null || listeDestinations.isEmpty()) {
            cmbDestination.addItem("Aucune destination disponible");
            cmbDestination.setEnabled(false);
            btnAssocier.setEnabled(false);
            return;
        }

        cmbDestination.setEnabled(true);

        for (Destination d : listeDestinations) {
            String display = d.getCodeDestination() + " - " + d.getNomVille();
            cmbDestination.addItem(display);
            destinationMap.put(display, d. getIdDestination());
        }
    }

    private void chargerAssociations() {
        tableModel.setRowCount(0);
        ArrayList<Object[]> associations = VoyageDestinationDAO.getAllAssociations();
        if (associations != null) {
            for (Object[] assoc : associations) {
                tableModel. addRow(assoc);
            }
        }
        btnDissocier.setEnabled(false);
        tableAssociations.clearSelection();
    }

    private void associer() {
        if (! validerChamps()) {
            return;
        }

        String voyageDisplay = (String) cmbVoyage.getSelectedItem();
        String destinationDisplay = (String) cmbDestination.getSelectedItem();

        Integer idVoyage = voyageMap.get(voyageDisplay);
        Integer idDestination = destinationMap. get(destinationDisplay);

        if (idVoyage == null || idDestination == null) {
            JOptionPane. showMessageDialog(this,
                "Erreur lors de la récupération des données",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (VoyageDestinationDAO.associationExiste(idVoyage, idDestination)) {
            JOptionPane.showMessageDialog(this,
                "Cette destination est déjà associée à ce voyage! ",
                "Information", JOptionPane. WARNING_MESSAGE);
            return;
        }

        if (VoyageDestinationDAO.associer(idVoyage, idDestination)) {
            int ordre = VoyageDestinationDAO.countDestinationsByVoyage(idVoyage);
            JOptionPane.showMessageDialog(this,
                "Destination ajoutée avec succès!\nPosition:  Étape " + ordre,
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerAssociations();
            nouveau();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'association",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dissocier() {
        int selectedRow = tableAssociations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane. showMessageDialog(this,
                "Veuillez sélectionner une association à dissocier",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codeVoyage = tableModel.getValueAt(selectedRow, 0).toString();
        String codeDestination = tableModel.getValueAt(selectedRow, 3).toString();
        String nomVille = tableModel.getValueAt(selectedRow, 4).toString();

        int confirmation = JOptionPane. showConfirmDialog(this,
            "Êtes-vous sûr de vouloir retirer cette étape?\n\n" +
            "Voyage: " + codeVoyage + "\n" +
            "Destination: " + nomVille,
            "Confirmation", JOptionPane. YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            Voyage voyage = VoyageDAO.getVoyageByCode(codeVoyage);
            Destination destination = DestinationDAO.getDestinationByCode(codeDestination);

            if (voyage != null && destination != null) {
                if (VoyageDestinationDAO.dissocier(voyage. getIdVoyage(), destination.getIdDestination())) {
                    JOptionPane.showMessageDialog(this,
                        "Étape retirée avec succès! ",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    chargerAssociations();
                    nouveau();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la dissociation",
                        "Erreur", JOptionPane. ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Impossible de trouver le voyage ou la destination",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerAssociation() {
        int selectedRow = tableAssociations.getSelectedRow();
        if (selectedRow != -1) {
            String codeVoyage = tableModel.getValueAt(selectedRow, 0).toString();
            String codeDestination = tableModel.getValueAt(selectedRow, 3).toString();

            for (int i = 0; i < cmbVoyage. getItemCount(); i++) {
                String item = cmbVoyage. getItemAt(i);
                if (item.startsWith(codeVoyage)) {
                    cmbVoyage.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < cmbDestination.getItemCount(); i++) {
                String item = cmbDestination.getItemAt(i);
                if (item.startsWith(codeDestination)) {
                    cmbDestination.setSelectedIndex(i);
                    break;
                }
            }

            btnAssocier.setEnabled(false);
            btnDissocier.setEnabled(true);
        }
    }

    private void nouveau() {
        if (cmbVoyage. getItemCount() > 0 && cmbVoyage.isEnabled()) {
            cmbVoyage. setSelectedIndex(0);
        }
        if (cmbDestination.getItemCount() > 0 && cmbDestination.isEnabled()) {
            cmbDestination.setSelectedIndex(0);
        }

        tableAssociations.clearSelection();
        btnAssocier.setEnabled(cmbVoyage.isEnabled() && cmbDestination.isEnabled());
        btnDissocier.setEnabled(false);
    }

    private boolean validerChamps() {
        if (cmbVoyage.getSelectedIndex() == -1 || ! cmbVoyage.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un voyage",
                "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (cmbDestination.getSelectedIndex() == -1 || !cmbDestination.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une destination",
                "Erreur de validation", JOptionPane. ERROR_MESSAGE);
            return false;
        }

        String voyageDisplay = (String) cmbVoyage.getSelectedItem();
        if ("Aucun voyage disponible".equals(voyageDisplay)) {
            JOptionPane.showMessageDialog(this,
                "Aucun voyage disponible.  Veuillez d'abord créer un voyage.",
                "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String destinationDisplay = (String) cmbDestination.getSelectedItem();
        if ("Aucune destination disponible".equals(destinationDisplay)) {
            JOptionPane.showMessageDialog(this,
                "Aucune destination disponible.  Veuillez d'abord créer une destination.",
                "Erreur de validation", JOptionPane. ERROR_MESSAGE);
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