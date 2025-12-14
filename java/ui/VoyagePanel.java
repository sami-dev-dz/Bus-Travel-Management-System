package ui;

import javax.swing.*;
import javax.swing.table. DefaultTableModel;
import dao.VoyageDAO;
import dao.BusDAO;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time. LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import model.Voyage;
import model.Bus;

public class VoyagePanel extends JPanel {
    private JTextField txtCodeVoyage;
    private JTextField txtDateDepart;
    private JTextField txtHeureDepart;
    private JSpinner spinHeures;
    private JSpinner spinMinutes;
    private JComboBox<String> cmbBus;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnActualiserBus;
    private JButton btnRetour;

    private JTable tableVoyage;
    private DefaultTableModel tableModel;

    private ArrayList<Voyage> listeVoyage;
    private HashMap<String, Integer> busMap;
    private int idAdministrateurConnecte = 1;

    public VoyagePanel() {
        listeVoyage = new ArrayList<>();
        busMap = new HashMap<>();
        initComponents();
        layoutComponents();
        chargerBus();
        chargerVoyages();
    }

    public VoyagePanel(int idAdministrateur) {
        this.idAdministrateurConnecte = idAdministrateur;
        listeVoyage = new ArrayList<>();
        busMap = new HashMap<>();
        initComponents();
        layoutComponents();
        chargerBus();
        chargerVoyages();
    }

    private void initComponents() {
        txtCodeVoyage = new JTextField(15);
        txtDateDepart = new JTextField(12);
        txtHeureDepart = new JTextField(8);

        SpinnerNumberModel heuresModel = new SpinnerNumberModel(1, 0, 99, 1);
        spinHeures = new JSpinner(heuresModel);
        JSpinner. NumberEditor heuresEditor = new JSpinner.NumberEditor(spinHeures, "00");
        spinHeures. setEditor(heuresEditor);

        SpinnerNumberModel minutesModel = new SpinnerNumberModel(0, 0, 59, 15);
        spinMinutes = new JSpinner(minutesModel);
        JSpinner.NumberEditor minutesEditor = new JSpinner.NumberEditor(spinMinutes, "00");
        spinMinutes.setEditor(minutesEditor);

        cmbBus = new JComboBox<>();

        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnNouveau = new JButton("Nouveau");
        btnActualiserBus = new JButton("Actualiser Bus");
        btnRetour = new JButton("Retour");

        String[] colonnes = {"Code Voyage", "Date et Heure Départ", "Durée", "Bus Affecté"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVoyage = new JTable(tableModel);
        tableVoyage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAjouter.addActionListener(e -> ajouterVoyage());
        btnModifier.addActionListener(e -> modifierVoyage());
        btnSupprimer.addActionListener(e -> supprimerVoyage());
        btnNouveau.addActionListener(e -> nouveauVoyage());
        btnActualiserBus.addActionListener(e -> chargerBus());
        btnRetour. addActionListener(e -> retourner());

        tableVoyage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerVoyage();
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
        panelFormulaire. setBorder(BorderFactory.createTitledBorder("Informations du Voyage"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc. gridx = 0;
        gbc.gridy = 0;
        panelFormulaire. add(new JLabel("Code Voyage: "), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelFormulaire.add(txtCodeVoyage, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelFormulaire.add(new JLabel("Date et Heure Départ:"), gbc);
        gbc.gridx = 1;
        JPanel panelDateTime = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelDateTime.add(txtDateDepart);
        panelDateTime. add(new JLabel("à"));
        panelDateTime.add(txtHeureDepart);
        gbc.gridwidth = 2;
        panelFormulaire.add(panelDateTime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panelFormulaire.add(new JLabel("Durée:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JPanel panelDuree = new JPanel(new FlowLayout(FlowLayout. LEFT, 5, 0));
        spinHeures.setPreferredSize(new Dimension(60, 25));
        spinMinutes.setPreferredSize(new Dimension(60, 25));
        panelDuree. add(spinHeures);
        panelDuree.add(new JLabel("h"));
        panelDuree.add(spinMinutes);
        panelDuree.add(new JLabel("min"));
        panelFormulaire. add(panelDuree, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panelFormulaire.add(new JLabel("Bus Affecté:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JPanel panelBus = new JPanel(new BorderLayout(5, 0));
        panelBus.add(cmbBus, BorderLayout.CENTER);
        panelBus.add(btnActualiserBus, BorderLayout.EAST);
        panelFormulaire.add(panelBus, gbc);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons. add(btnSupprimer);
        panelBoutons.add(btnRetour);

        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelFormulaire, BorderLayout.CENTER);
        panelHaut.add(panelBoutons, BorderLayout. SOUTH);

        JScrollPane scrollPane = new JScrollPane(tableVoyage);
        scrollPane.setBorder(BorderFactory. createTitledBorder("Liste des Voyages"));

        add(panelHaut, BorderLayout.NORTH);
        add(scrollPane, BorderLayout. CENTER);
    }

    private void chargerVoyages() {
        listeVoyage = VoyageDAO.getAllVoyage();
        tableModel.setRowCount(0);
        for (Voyage v : listeVoyage) {
            String busInfo = v.getBusImmatriculation() != null ? v.getBusImmatriculation() : "Non assigné";
            String dureeAffichage = formatDuree(v.getDuree());
            String dateHeureAffichage = "";
            if (v.getDateDepart() != null) {
                dateHeureAffichage = v.getDateDepart().toString();
                if (v.getHeureDepart() != null && !v.getHeureDepart().isEmpty()) {
                    dateHeureAffichage += " à " + v.getHeureDepart();
                }
            }
            tableModel.addRow(new Object[]{
                v.getCode(),
                dateHeureAffichage,
                dureeAffichage,
                busInfo
            });
        }
    }

    private String formatDuree(int dureeMinutes) {
        if (dureeMinutes <= 0) {
            return "0h";
        }
        int heures = dureeMinutes / 60;
        int minutes = dureeMinutes % 60;
        if (minutes == 0) {
            return heures + "h";
        }
        return heures + "h" + String. format("%02d", minutes) + "min";
    }

    private void chargerBus() {
        cmbBus.removeAllItems();
        busMap.clear();
        ArrayList<Bus> listeBus = BusDAO.getAllBus();
        for (Bus b : listeBus) {
            String busDisplay = b.getImmatriculation() + " - " + b.getMarque() + " " + b.getModele() + " (" + b.getNbPlaces() + " places)";
            cmbBus. addItem(busDisplay);
            busMap.put(busDisplay, b. getIdBus());
        }
        if (cmbBus.getItemCount() > 0) {
            cmbBus.setSelectedIndex(0);
        }
    }

    private void ajouterVoyage() {
        if (! validerChamps()) return;

        String codeVoyage = txtCodeVoyage.getText().trim();
        String dateStr = txtDateDepart.getText().trim();
        String heureStr = txtHeureDepart.getText().trim();
        int heures = (Integer) spinHeures.getValue();
        int minutes = (Integer) spinMinutes.getValue();
        int dureeMinutes = heures * 60 + minutes;
        String busDisplay = (String) cmbBus.getSelectedItem();
        Integer idBus = busMap.get(busDisplay);

        if (VoyageDAO.codeVoyageExiste(codeVoyage)) {
            JOptionPane. showMessageDialog(this,
                "Un voyage avec ce code existe déjà! ",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            txtCodeVoyage. requestFocus();
            return;
        }

        LocalDate dateDepart;
        try {
            dateDepart = LocalDate. parse(dateStr);

            String[] heureParts = heureStr.split(":");
            if (heureParts.length != 2) {
                throw new DateTimeParseException("Format invalide", heureStr, 0);
            }
            int heure = Integer.parseInt(heureParts[0]);
            int minute = Integer.parseInt(heureParts[1]);

            if (heure < 0 || heure > 23 || minute < 0 || minute > 59) {
                throw new DateTimeParseException("Heure invalide", heureStr, 0);
            }

            LocalDateTime dateHeureDepart = dateDepart.atTime(heure, minute);

            if (dateHeureDepart. isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                    "La date et l'heure de départ ne peuvent pas être antérieures à maintenant",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                txtDateDepart. requestFocus();
                return;
            }

            if (dureeMinutes <= 0) {
                JOptionPane. showMessageDialog(this,
                    "La durée doit être supérieure à 0",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                spinHeures.requestFocus();
                return;
            }

            if (! VoyageDAO.busDisponible(idBus, dateDepart, heureStr, dureeMinutes, null)) {
                String conflit = VoyageDAO.getVoyageEnConflit(idBus, dateDepart, heureStr, dureeMinutes, null);
                JOptionPane.showMessageDialog(this,
                    "Ce bus n'est pas disponible à cette période!\n\n" +
                    "Conflit avec le voyage:\n" + conflit + "\n\n" +
                    "Veuillez choisir un autre bus ou modifier les horaires.",
                    "Bus non disponible", JOptionPane.ERROR_MESSAGE);
                cmbBus.requestFocus();
                return;
            }

        } catch (DateTimeParseException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Format de date ou d'heure invalide.  Utilisez yyyy-MM-dd et HH:mm",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            txtDateDepart.requestFocus();
            return;
        }

        Voyage voyage = new Voyage(codeVoyage, dateDepart, dureeMinutes, idBus, idAdministrateurConnecte);
        voyage.setHeureDepart(heureStr);

        if (VoyageDAO.ajouterVoyage(voyage)) {
            chargerVoyages();
            JOptionPane.showMessageDialog(this,
                "Voyage ajouté avec succès!",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            nouveauVoyage();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ajout du voyage",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierVoyage() {
        int selectedRow = tableVoyage.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un voyage à modifier",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validerChamps()) return;

        String codeVoyage = txtCodeVoyage. getText().trim();
        String dateStr = txtDateDepart. getText().trim();
        String heureStr = txtHeureDepart.getText().trim();
        int heures = (Integer) spinHeures.getValue();
        int minutes = (Integer) spinMinutes.getValue();
        int dureeMinutes = heures * 60 + minutes;
        String busDisplay = (String) cmbBus.getSelectedItem();
        Integer idBus = busMap. get(busDisplay);

        Voyage voyageOriginal = listeVoyage. get(selectedRow);

        LocalDate dateDepart;
        try {
            dateDepart = LocalDate.parse(dateStr);

            String[] heureParts = heureStr.split(":");
            if (heureParts.length != 2) {
                throw new DateTimeParseException("Format invalide", heureStr, 0);
            }
            int heure = Integer.parseInt(heureParts[0]);
            int minute = Integer.parseInt(heureParts[1]);

            if (heure < 0 || heure > 23 || minute < 0 || minute > 59) {
                throw new DateTimeParseException("Heure invalide", heureStr, 0);
            }

            LocalDateTime dateHeureDepart = dateDepart.atTime(heure, minute);

            if (dateHeureDepart.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                    "La date et l'heure de départ ne peuvent pas être antérieures à maintenant",
                    "Erreur", JOptionPane. ERROR_MESSAGE);
                txtDateDepart.requestFocus();
                return;
            }

            if (dureeMinutes <= 0) {
                JOptionPane.showMessageDialog(this,
                    "La durée doit être supérieure à 0",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                spinHeures.requestFocus();
                return;
            }

            if (!VoyageDAO.busDisponible(idBus, dateDepart, heureStr, dureeMinutes, voyageOriginal.getIdVoyage())) {
                String conflit = VoyageDAO.getVoyageEnConflit(idBus, dateDepart, heureStr, dureeMinutes, voyageOriginal.getIdVoyage());
                JOptionPane. showMessageDialog(this,
                    "Ce bus n'est pas disponible à cette période!\n\n" +
                    "Conflit avec le voyage:\n" + conflit + "\n\n" +
                    "Veuillez choisir un autre bus ou modifier les horaires.",
                    "Bus non disponible", JOptionPane.ERROR_MESSAGE);
                cmbBus.requestFocus();
                return;
            }

        } catch (DateTimeParseException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Format de date ou d'heure invalide",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Voyage voyage = listeVoyage. get(selectedRow);
        voyage.setCode(codeVoyage);
        voyage.setDateDepart(dateDepart);
        voyage.setHeureDepart(heureStr);
        voyage.setDuree(dureeMinutes);
        voyage.setIdBus(idBus);
        voyage.setIdAdministrateur(idAdministrateurConnecte);

        if (VoyageDAO.modifierVoyage(voyage)) {
            chargerVoyages();
            JOptionPane.showMessageDialog(this,
                "Voyage modifié avec succès! ",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            nouveauVoyage();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la modification du voyage",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerVoyage() {
        int selectedRow = tableVoyage.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane. showMessageDialog(this,
                "Veuillez sélectionner un voyage à supprimer",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane. showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce voyage?",
            "Confirmation", JOptionPane. YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            String codeVoyage = listeVoyage. get(selectedRow).getCode();

            if (VoyageDAO.supprimerVoyage(codeVoyage)) {
                chargerVoyages();
                JOptionPane.showMessageDialog(this,
                    "Voyage supprimé avec succès! ",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                nouveauVoyage();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression du voyage\nCe voyage a peut-être des réservations.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerVoyage() {
        int selectedRow = tableVoyage.getSelectedRow();
        if (selectedRow != -1 && selectedRow < listeVoyage.size()) {
            Voyage v = listeVoyage.get(selectedRow);
            txtCodeVoyage. setText(v.getCode());
            if (v.getDateDepart() != null) {
                txtDateDepart.setText(v.getDateDepart().toString());
            }
            txtHeureDepart. setText(v.getHeureDepart() != null ? v. getHeureDepart() : "");

            int dureeMinutes = v.getDuree();
            int heures = dureeMinutes / 60;
            int minutes = dureeMinutes % 60;
            spinHeures.setValue(heures);
            spinMinutes.setValue(minutes);

            String busInfo = v.getBusImmatriculation();
            if (busInfo != null) {
                for (int i = 0; i < cmbBus.getItemCount(); i++) {
                    String item = cmbBus.getItemAt(i);
                    if (item.startsWith(busInfo)) {
                        cmbBus.setSelectedIndex(i);
                        break;
                    }
                }
            }

            btnAjouter.setEnabled(false);
            btnModifier. setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void nouveauVoyage() {
        txtCodeVoyage. setText("");
        txtDateDepart.setText("");
        txtHeureDepart.setText("");
        spinHeures.setValue(1);
        spinMinutes.setValue(0);
        if (cmbBus.getItemCount() > 0) {
            cmbBus.setSelectedIndex(0);
        }

        tableVoyage.clearSelection();
        btnAjouter.setEnabled(true);
        btnModifier. setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        if (txtCodeVoyage.getText().trim().isEmpty()) {
            JOptionPane. showMessageDialog(this,
                "Le code voyage est obligatoire",
                "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtCodeVoyage. requestFocus();
            return false;
        }
        if (txtDateDepart.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La date de départ est obligatoire",
                "Erreur de validation", JOptionPane. ERROR_MESSAGE);
            txtDateDepart.requestFocus();
            return false;
        }
        if (txtHeureDepart.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "L'heure de départ est obligatoire",
                "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtHeureDepart.requestFocus();
            return false;
        }
        if (cmbBus.getSelectedIndex() == -1 || cmbBus.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un bus (ajoutez d'abord des bus)",
                "Erreur de validation", JOptionPane.ERROR_MESSAGE);
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