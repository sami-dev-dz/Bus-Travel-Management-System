package ui;

import dao.PassagerDAO;
import dao.ReservationDAO;
import dao.VoyageDAO;
import dao.VoyageDestinationDAO;
import model.Passager;
import model. Reservation;
import model.Voyage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java. awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util. ArrayList;
import java.util.HashMap;

public class ReservationPanel extends JPanel {
    private JComboBox<String> cmbVoyage;
    private JComboBox<String> cmbPassager;
    private JComboBox<String> cmbStatut;
    private JTextField txtDateReservation;
    private JSpinner spnNbPlaces;
    private JTextField txtRecherche;
    private JTextArea txtDestinations;
    private JLabel lblBusInfo;
    private JLabel lblPlacesInfo;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnNouveau;
    private JButton btnActualiser;
    private JButton btnRechercher;
    private JButton btnRetour;

    private JTable tableReservations;
    private DefaultTableModel tableModel;

    private ArrayList<Voyage> listeVoyages;
    private ArrayList<Passager> listePassagers;
    private ArrayList<Reservation> listeReservations;

    private HashMap<String, Integer> voyageMap = new HashMap<>();
    private HashMap<String, Integer> passagerMap = new HashMap<>();

    private int idReceptionnisteConnecte = 1;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReservationPanel() {
        listeReservations = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerDonnees();
    }

    public ReservationPanel(int idReceptionniste) {
        this.idReceptionnisteConnecte = idReceptionniste;
        listeReservations = new ArrayList<>();
        initComponents();
        layoutComponents();
        chargerDonnees();
    }

    private void initComponents() {
        cmbVoyage = new JComboBox<>();
        cmbPassager = new JComboBox<>();

        String[] statuts = {"En attente", "Confirmée", "Annulée"};
        cmbStatut = new JComboBox<>(statuts);

        txtDateReservation = new JTextField(15);
        txtDateReservation.setText(LocalDate.now().format(dateFormatter));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 50, 1);
        spnNbPlaces = new JSpinner(spinnerModel);

        txtRecherche = new JTextField(20);

        txtDestinations = new JTextArea(2, 20);
        txtDestinations. setEditable(false);
        txtDestinations.setLineWrap(true);
        txtDestinations. setWrapStyleWord(true);
        txtDestinations. setBackground(new Color(245, 245, 245));

        lblBusInfo = new JLabel("Bus:  -");
        lblBusInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblPlacesInfo = new JLabel("");
        lblPlacesInfo.setFont(new Font("Segoe UI", Font. BOLD, 12));

        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnNouveau = new JButton("Nouveau");
        btnActualiser = new JButton("Actualiser");
        btnRechercher = new JButton("Rechercher");
        btnRetour = new JButton("Retour");

        String[] colonnes = {"Date", "Statut", "Places", "Voyage", "Bus", "Destinations", "Passager"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableReservations = new JTable(tableModel);
        tableReservations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAjouter.addActionListener(e -> ajouterReservation());
        btnModifier.addActionListener(e -> modifierReservation());
        btnSupprimer.addActionListener(e -> supprimerReservation());
        btnNouveau.addActionListener(e -> nouveauReservation());
        btnActualiser.addActionListener(e -> chargerDonnees());
        btnRechercher.addActionListener(e -> rechercherReservations());
        btnRetour.addActionListener(e -> retourner());

        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    rechercherReservations();
                }
            }
        });

        cmbVoyage. addActionListener(e -> afficherInfosVoyage());

        tableReservations.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectionnerReservation();
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
        panelRecherche.setBorder(BorderFactory. createTitledBorder("Recherche"));
        panelRecherche.add(new JLabel("Rechercher: "));
        panelRecherche.add(txtRecherche);
        panelRecherche.add(btnRechercher);

        JPanel panelFormulaire = new JPanel(new GridBagLayout());
        panelFormulaire. setBorder(BorderFactory.createTitledBorder("Informations de la Réservation"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulaire.add(new JLabel("Date Réservation:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelFormulaire. add(txtDateReservation, gbc);

        gbc.gridx = 0;
        gbc. gridy = 1;
        gbc.weightx = 0;
        panelFormulaire.add(new JLabel("Voyage:"), gbc);
        gbc. gridx = 1;
        gbc.weightx = 1.0;
        panelFormulaire.add(cmbVoyage, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc. weightx = 0;
        panelFormulaire.add(new JLabel("Infos Bus:"), gbc);
        gbc. gridx = 1;
        gbc.weightx = 1.0;
        JPanel panelBusPlaces = new JPanel(new GridLayout(2, 1, 5, 2));
        panelBusPlaces. add(lblBusInfo);
        panelBusPlaces.add(lblPlacesInfo);
        panelFormulaire.add(panelBusPlaces, gbc);

        gbc. gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panelFormulaire.add(new JLabel("Destinations:"), gbc);
        gbc. gridx = 1;
        gbc.weightx = 1.0;
        JScrollPane scrollDestinations = new JScrollPane(txtDestinations);
        scrollDestinations.setPreferredSize(new Dimension(200, 45));
        panelFormulaire.add(scrollDestinations, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc. weightx = 0;
        panelFormulaire.add(new JLabel("Passager:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelFormulaire. add(cmbPassager, gbc);

        gbc. gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        panelFormulaire.add(new JLabel("Nombre de places:"), gbc);
        gbc. gridx = 1;
        gbc.weightx = 1.0;
        JPanel panelNbPlaces = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        spnNbPlaces.setPreferredSize(new Dimension(80, 25));
        panelNbPlaces. add(spnNbPlaces);
        panelFormulaire.add(panelNbPlaces, gbc);

        gbc.gridx = 0;
        gbc. gridy = 6;
        gbc.weightx = 0;
        panelFormulaire. add(new JLabel("Statut: "), gbc);
        gbc.gridx = 1;
        gbc. weightx = 1.0;
        panelFormulaire.add(cmbStatut, gbc);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoutons.add(btnNouveau);
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons. add(btnSupprimer);
        panelBoutons. add(btnActualiser);
        panelBoutons.add(btnRetour);

        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.add(panelRecherche, BorderLayout.NORTH);
        panelHaut.add(panelFormulaire, BorderLayout.CENTER);
        panelHaut.add(panelBoutons, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(tableReservations);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Réservations"));

        add(panelHaut, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void chargerDonnees() {
        chargerVoyages();
        chargerPassagers();
        chargerReservations();
        nouveauReservation();
    }

    private void chargerVoyages() {
        cmbVoyage. removeAllItems();
        voyageMap.clear();
        listeVoyages = VoyageDAO.getAllVoyage();

        if (listeVoyages == null || listeVoyages.isEmpty()) {
            cmbVoyage. addItem("Aucun voyage disponible");
            cmbVoyage. setEnabled(false);
            btnAjouter.setEnabled(false);
            txtDestinations.setText("");
            lblBusInfo.setText("Bus: -");
            lblPlacesInfo.setText("");
            return;
        }

        cmbVoyage. setEnabled(true);
        btnAjouter.setEnabled(true);

        for (Voyage v : listeVoyages) {
            String code = v.getCode() != null ? v.getCode() : ("VYG#" + v.getIdVoyage());
            LocalDate d = v.getDateDepart();
            String dateStr = d != null ?  d.toString() : "Date inconnue";
            String display = code + " - " + dateStr;
            cmbVoyage.addItem(display);
            voyageMap.put(display, v. getIdVoyage());
        }

        afficherInfosVoyage();
    }

    private void chargerPassagers() {
        cmbPassager.removeAllItems();
        passagerMap.clear();
        listePassagers = PassagerDAO. getAllPassagers();

        if (listePassagers == null || listePassagers.isEmpty()) {
            cmbPassager.addItem("Aucun passager");
            cmbPassager.setEnabled(false);
            btnAjouter.setEnabled(false);
            return;
        }

        cmbPassager.setEnabled(true);

        for (Passager p : listePassagers) {
            String display = p.getNom() + " " + p.getPrenom();
            cmbPassager.addItem(display);
            passagerMap.put(display, p.getIdPassager());
        }
    }

    private void chargerReservations() {
        listeReservations = ReservationDAO. getAllReservations();
        afficherReservationsDansTable();
    }

    private void rechercherReservations() {
        String critere = txtRecherche.getText().trim();
        if (critere.isEmpty()) {
            chargerReservations();
        } else {
            listeReservations = ReservationDAO. rechercher(critere);
            afficherReservationsDansTable();
        }
        nouveauReservation();
    }

    private void afficherReservationsDansTable() {
        tableModel.setRowCount(0);

        if (listeReservations == null) {
            listeReservations = new ArrayList<>();
        }

        for (Reservation r : listeReservations) {
            String voyageInfo = getVoyageCodeById(r.getIdVoyage());
            String busInfo = getBusInfoByVoyageId(r.getIdVoyage());
            String destinationsInfo = getDestinationsByVoyageId(r.getIdVoyage());
            String passagerInfo = getPassagerDisplayById(r.getIdPassager());
            String dateStr = r.getDateReservation() != null ? r. getDateReservation().format(dateFormatter) : "";
            tableModel.addRow(new Object[]{
                dateStr,
                r.getStatut(),
                r.getNbPlaces(),
                voyageInfo,
                busInfo,
                destinationsInfo,
                passagerInfo
            });
        }
    }

    private void afficherInfosVoyage() {
        String voyageDisplay = (String) cmbVoyage.getSelectedItem();
        if (voyageDisplay == null || "Aucun voyage disponible".equals(voyageDisplay)) {
            txtDestinations.setText("");
            lblBusInfo.setText("Bus: -");
            lblPlacesInfo.setText("");
            return;
        }

        Integer idVoyage = voyageMap.get(voyageDisplay);
        if (idVoyage == null) {
            txtDestinations.setText("");
            lblBusInfo.setText("Bus: -");
            lblPlacesInfo. setText("");
            return;
        }

        String destinations = getDestinationsByVoyageId(idVoyage);
        txtDestinations. setText(destinations. isEmpty() ? "Aucune destination" : destinations);

        String busInfo = getBusInfoByVoyageId(idVoyage);
        lblBusInfo.setText("Bus: " + busInfo);

        int capacite = ReservationDAO.getCapaciteBus(idVoyage);
        int placesDisponibles = ReservationDAO.getPlacesDisponibles(idVoyage);
        int placesEnAttente = ReservationDAO.getPlacesEnAttente(idVoyage);
        int placesConfirmees = ReservationDAO.getPlacesConfirmees(idVoyage);

        String placesText = String.format("Capacité: %d | Disponibles: %d | En attente: %d | Confirmées: %d",
                capacite, placesDisponibles, placesEnAttente, placesConfirmees);
        lblPlacesInfo. setText(placesText);

        if (placesDisponibles <= 0) {
            lblPlacesInfo.setForeground(Color.RED);
        } else if (placesDisponibles <= 5) {
            lblPlacesInfo.setForeground(new Color(255, 152, 0));
        } else {
            lblPlacesInfo.setForeground(new Color(46, 125, 50));
        }

        SpinnerNumberModel model = (SpinnerNumberModel) spnNbPlaces.getModel();
        model.setMaximum(Math.max(placesDisponibles, 1));
        if ((Integer) spnNbPlaces.getValue() > placesDisponibles) {
            spnNbPlaces. setValue(Math.max(placesDisponibles, 1));
        }
    }

    private String getDestinationsByVoyageId(int idVoyage) {
        ArrayList<String> destinations = VoyageDestinationDAO.getDestinationsByVoyage(idVoyage);
        if (destinations == null || destinations.isEmpty()) {
            return "Aucune destination";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < destinations.size(); i++) {
            sb.append(destinations. get(i));
            if (i < destinations.size() - 1) {
                sb.append(" → ");
            }
        }
        return sb.toString();
    }

    private String getBusInfoByVoyageId(int idVoyage) {
        if (listeVoyages != null) {
            for (Voyage v : listeVoyages) {
                if (v.getIdVoyage() == idVoyage) {
                    String busImmat = v.getBusImmatriculation();
                    int nbPlaces = v. getBusNbPlaces();
                    if (busImmat != null && ! busImmat.isEmpty()) {
                        return busImmat + " (" + nbPlaces + " places)";
                    }
                    return "Non assigné";
                }
            }
        }
        return "Non assigné";
    }

    private String getVoyageCodeById(int idVoyage) {
        if (listeVoyages != null) {
            for (Voyage v : listeVoyages) {
                if (v.getIdVoyage() == idVoyage) {
                    String code = v.getCode() != null ? v. getCode() : ("VYG#" + v.getIdVoyage());
                    LocalDate d = v. getDateDepart();
                    String dateStr = d != null ? d.toString() : "";
                    return code + " - " + dateStr;
                }
            }
        }
        return "Voyage #" + idVoyage;
    }

    private String getPassagerDisplayById(int idPassager) {
        if (listePassagers != null) {
            for (Passager p : listePassagers) {
                if (p. getIdPassager() == idPassager) {
                    return p.getNom() + " " + p.getPrenom();
                }
            }
        }
        return "Passager #" + idPassager;
    }

    private void ajouterReservation() {
        if (! validerChamps()) {
            return;
        }

        String voyageDisplay = (String) cmbVoyage. getSelectedItem();
        String passagerDisplay = (String) cmbPassager.getSelectedItem();
        Integer idVoyage = voyageMap.get(voyageDisplay);
        Integer idPassager = passagerMap.get(passagerDisplay);
        String statut = (String) cmbStatut. getSelectedItem();
        String dateText = txtDateReservation.getText().trim();
        int nbPlaces = (Integer) spnNbPlaces.getValue();

        if (idVoyage == null || idPassager == null) {
            JOptionPane.showMessageDialog(this, "Sélection invalide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int placesDisponibles = ReservationDAO.getPlacesDisponibles(idVoyage);
        if (!"Annulée".equals(statut) && placesDisponibles < nbPlaces) {
            JOptionPane.showMessageDialog(this,
                    "Nombre de places insuffisant!\nPlaces disponibles: " + placesDisponibles + "\nPlaces demandées: " + nbPlaces,
                    "Places insuffisantes", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ReservationDAO.existe(idVoyage, idPassager)) {
            JOptionPane.showMessageDialog(this, "Une réservation active existe déjà pour ce passager sur ce voyage",
                    "Information", JOptionPane. WARNING_MESSAGE);
            return;
        }

        LocalDate dateReservation;
        try {
            dateReservation = LocalDate.parse(dateText, dateFormatter);
        } catch (DateTimeParseException e) {
            dateReservation = LocalDate.now();
        }

        Reservation reservation = new Reservation();
        reservation.setIdVoyage(idVoyage);
        reservation.setIdPassager(idPassager);
        reservation. setDateReservation(dateReservation);
        reservation. setStatut(statut);
        reservation.setNbPlaces(nbPlaces);
        reservation. setIdReceptionniste(idReceptionnisteConnecte);

        if (ReservationDAO.ajouter(reservation)) {
            int newPlacesDisponibles = ReservationDAO.getPlacesDisponibles(idVoyage);
            JOptionPane.showMessageDialog(this,
                    "Réservation ajoutée avec succès!\n" +
                    "Nombre de places:  " + nbPlaces + "\n" +
                    "Statut: " + statut + "\n" +
                    "Places restantes: " + newPlacesDisponibles,
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerReservations();
            nouveauReservation();
            afficherInfosVoyage();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la réservation\nVérifiez les places disponibles.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réservation à modifier",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validerChamps()) {
            return;
        }

        Reservation reservationOriginale = listeReservations. get(selectedRow);
        int idReservation = reservationOriginale.getIdReservation();

        String voyageDisplay = (String) cmbVoyage. getSelectedItem();
        String passagerDisplay = (String) cmbPassager.getSelectedItem();
        Integer idVoyage = voyageMap. get(voyageDisplay);
        Integer idPassager = passagerMap.get(passagerDisplay);
        String statut = (String) cmbStatut.getSelectedItem();
        String dateText = txtDateReservation.getText().trim();
        int nbPlaces = (Integer) spnNbPlaces.getValue();

        String ancienStatut = reservationOriginale.getStatut();
        int ancienNbPlaces = reservationOriginale.getNbPlaces();
        boolean reactivation = "Annulée". equals(ancienStatut) && !"Annulée".equals(statut);
        boolean changementVoyage = reservationOriginale. getIdVoyage() != idVoyage;
        boolean augmentationPlaces = nbPlaces > ancienNbPlaces;

        if (!"Annulée". equals(statut)) {
            int placesDisponibles = ReservationDAO.getPlacesDisponibles(idVoyage);
            
            if (changementVoyage) {
                if (placesDisponibles < nbPlaces) {
                    JOptionPane.showMessageDialog(this,
                            "Nombre de places insuffisant sur le nouveau voyage!\nPlaces disponibles: " + placesDisponibles,
                            "Places insuffisantes", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (reactivation) {
                if (placesDisponibles < nbPlaces) {
                    JOptionPane.showMessageDialog(this,
                            "Impossible de réactiver:  places insuffisantes!\nPlaces disponibles: " + placesDisponibles,
                            "Places insuffisantes", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (augmentationPlaces && !"Annulée".equals(ancienStatut)) {
                int placesSupplementaires = nbPlaces - ancienNbPlaces;
                if (placesDisponibles < placesSupplementaires) {
                    JOptionPane.showMessageDialog(this,
                            "Impossible d'augmenter le nombre de places!\nPlaces supplémentaires demandées: " + placesSupplementaires + "\nPlaces disponibles: " + placesDisponibles,
                            "Places insuffisantes", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        LocalDate dateReservation;
        try {
            dateReservation = LocalDate. parse(dateText, dateFormatter);
        } catch (DateTimeParseException e) {
            dateReservation = reservationOriginale. getDateReservation();
        }

        Reservation reservation = new Reservation();
        reservation.setIdReservation(idReservation);
        reservation.setIdVoyage(idVoyage);
        reservation.setIdPassager(idPassager);
        reservation. setDateReservation(dateReservation);
        reservation. setStatut(statut);
        reservation.setNbPlaces(nbPlaces);

        if (ReservationDAO.modifier(reservation)) {
            String message = "Réservation modifiée avec succès! ";
            if ("Annulée". equals(statut) && !"Annulée".equals(ancienStatut)) {
                message += "\n" + ancienNbPlaces + " place(s) libérée(s).";
            } else if (reactivation) {
                message += "\nLa réservation a été réactivée.";
            }
            JOptionPane.showMessageDialog(this, message,
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerReservations();
            nouveauReservation();
            afficherInfosVoyage();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerReservation() {
        int selectedRow = tableReservations. getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réservation à supprimer",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reservation reservation = listeReservations.get(selectedRow);
        String message = "Êtes-vous sûr de vouloir supprimer définitivement cette réservation?";
        if (!"Annulée". equals(reservation.getStatut())) {
            message += "\nCette action libérera " + reservation.getNbPlaces() + " place(s).";
        }

        int confirmation = JOptionPane. showConfirmDialog(this, message,
                "Confirmation", JOptionPane. YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            if (ReservationDAO.supprimer(reservation. getIdReservation())) {
                JOptionPane.showMessageDialog(this, "Réservation supprimée avec succès! ",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                chargerReservations();
                nouveauReservation();
                afficherInfosVoyage();
            } else {
                JOptionPane. showMessageDialog(this, "Erreur lors de la suppression",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectionnerReservation() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow != -1 && selectedRow < listeReservations.size()) {
            Reservation reservation = listeReservations.get(selectedRow);

            if (reservation.getDateReservation() != null) {
                txtDateReservation.setText(reservation. getDateReservation().format(dateFormatter));
            }
            cmbStatut.setSelectedItem(reservation.getStatut());
            spnNbPlaces.setValue(reservation. getNbPlaces());

            String voyageDisplay = getVoyageCodeById(reservation.getIdVoyage());
            String passagerDisplay = getPassagerDisplayById(reservation.getIdPassager());

            selectComboByText(cmbVoyage, voyageDisplay);
            selectComboByText(cmbPassager, passagerDisplay);

            afficherInfosVoyage();

            SpinnerNumberModel model = (SpinnerNumberModel) spnNbPlaces. getModel();
            int placesDisponibles = ReservationDAO. getPlacesDisponibles(reservation. getIdVoyage());
            if (!"Annulée". equals(reservation.getStatut())) {
                model.setMaximum(placesDisponibles + reservation.getNbPlaces());
            } else {
                model. setMaximum(Math.max(placesDisponibles, 1));
            }
            spnNbPlaces.setValue(reservation.getNbPlaces());

            btnAjouter.setEnabled(false);
            btnModifier.setEnabled(true);
            btnSupprimer.setEnabled(true);
        }
    }

    private void selectComboByText(JComboBox<String> combo, String text) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(text)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void nouveauReservation() {
        txtDateReservation. setText(LocalDate.now().format(dateFormatter));
        cmbStatut.setSelectedIndex(0);
        spnNbPlaces.setValue(1);
        if (cmbVoyage. getItemCount() > 0) {
            cmbVoyage. setSelectedIndex(0);
        }
        if (cmbPassager.getItemCount() > 0) {
            cmbPassager.setSelectedIndex(0);
        }

        afficherInfosVoyage();

        tableReservations.clearSelection();
        btnAjouter.setEnabled(cmbVoyage. isEnabled() && cmbPassager.isEnabled());
        btnModifier. setEnabled(false);
        btnSupprimer.setEnabled(false);
    }

    private boolean validerChamps() {
        String dateText = txtDateReservation.getText().trim();
        if (dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La date de réservation est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtDateReservation. requestFocus();
            return false;
        }

        try {
            LocalDate dateReservation = LocalDate.parse(dateText, dateFormatter);
            if (dateReservation. isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "La date de réservation ne peut pas être antérieure à aujourd'hui",
                        "Erreur de validation", JOptionPane. ERROR_MESSAGE);
                txtDateReservation. requestFocus();
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide.  Utilisez AAAA-MM-JJ",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            txtDateReservation.requestFocus();
            return false;
        }

        if (cmbVoyage.getSelectedIndex() == -1 || ! cmbVoyage.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un voyage",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (cmbPassager.getSelectedIndex() == -1 || !cmbPassager.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un passager",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String voyageDisplay = (String) cmbVoyage.getSelectedItem();
        if ("Aucun voyage disponible".equals(voyageDisplay)) {
            JOptionPane.showMessageDialog(this, "Aucun voyage disponible",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String passagerDisplay = (String) cmbPassager. getSelectedItem();
        if ("Aucun passager". equals(passagerDisplay)) {
            JOptionPane.showMessageDialog(this, "Aucun passager disponible",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int nbPlaces = (Integer) spnNbPlaces.getValue();
        if (nbPlaces < 1) {
            JOptionPane.showMessageDialog(this, "Le nombre de places doit être au moins 1",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            spnNbPlaces.requestFocus();
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