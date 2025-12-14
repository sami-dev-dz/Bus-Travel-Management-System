package ui;

import javax.swing.*;
import java.awt.*;
import model.Receptionniste;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Receptionniste currentUser;

    private BusPanel busPanel;
    private DestinationPanel destinationPanel;
    private VoyagePanel voyagePanel;
    private VoyageDestinationPanel voyageDestinationPanel;
    private ReceptionnistePanel receptionnistePanel;
    private PassagerPanel passagerPanel;
    private ReservationPanel reservationPanel;
    private JPanel menuPanel;

    private final Color COLOR_PRINCIPAL = new Color(22, 92, 70);
    private final Color COLOR_DECONNEXION = new Color(192, 57, 43);
    private final Color BG_COLOR = new Color(236, 240, 241);

    public MainFrame(Receptionniste user) {
        this.currentUser = user;

        setTitle("Système de Gestion de Transport - " + user.getNomComplet() + " (" + user.getRole() + ")");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initPanels();

        add(mainPanel);

        cardLayout.show(mainPanel, "MENU");
    }

    private void initPanels() {
        menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, "MENU");

        if (currentUser.isAdmin()) {
            busPanel = new BusPanel();
            destinationPanel = new DestinationPanel();
            voyagePanel = new VoyagePanel();
            voyageDestinationPanel = new VoyageDestinationPanel();
            receptionnistePanel = new ReceptionnistePanel();

            mainPanel.add(busPanel, "BUS");
            mainPanel.add(destinationPanel, "DESTINATION");
            mainPanel.add(voyagePanel, "VOYAGE");
            mainPanel.add(voyageDestinationPanel, "VOYAGE_DESTINATION");
            mainPanel. add(receptionnistePanel, "RECEPTIONNISTE");
        } else if (currentUser.isReceptionniste()) {
            passagerPanel = new PassagerPanel();
            reservationPanel = new ReservationPanel();

            mainPanel.add(passagerPanel, "PASSAGER");
            mainPanel. add(reservationPanel, "RESERVATION");
        }
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel. setBackground(COLOR_PRINCIPAL);
        titlePanel.setPreferredSize(new Dimension(1200, 100));

        JLabel lblTitle = new JLabel("GESTION DE TRANSPORT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(Color. WHITE);

        JLabel lblUser = new JLabel("Connecté:  " + currentUser. getNomComplet() + " | " + currentUser.getRole() + "  ", SwingConstants.RIGHT);
        lblUser.setFont(new Font("Segoe UI", Font. PLAIN, 14));
        lblUser.setForeground(Color.WHITE);
        lblUser.setBorder(BorderFactory. createEmptyBorder(0, 0, 0, 20));

        titlePanel.add(lblTitle, BorderLayout. CENTER);
        titlePanel.add(lblUser, BorderLayout. SOUTH);

        JPanel centerPanel = new JPanel();

        if (currentUser.isAdmin()) {
            centerPanel.setLayout(new GridLayout(3, 2, 20, 20));
            centerPanel. setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
            centerPanel.setBackground(BG_COLOR);

            JButton btnVoyage = createMenuButton("Gestion des Voyages", COLOR_PRINCIPAL);
            JButton btnBus = createMenuButton("Gestion des Bus", COLOR_PRINCIPAL);
            JButton btnDestination = createMenuButton("Gestion des Destinations", COLOR_PRINCIPAL);
            JButton btnVoyageDest = createMenuButton("Voyage-Destinations", COLOR_PRINCIPAL);
            JButton btnReceptionniste = createMenuButton("Gestion des Réceptionnistes", COLOR_PRINCIPAL);
            JButton btnDeconnexion = createMenuButton("Déconnexion", COLOR_DECONNEXION);

            btnVoyage.addActionListener(e -> cardLayout.show(mainPanel, "VOYAGE"));
            btnBus.addActionListener(e -> cardLayout.show(mainPanel, "BUS"));
            btnDestination.addActionListener(e -> cardLayout.show(mainPanel, "DESTINATION"));
            btnVoyageDest.addActionListener(e -> cardLayout.show(mainPanel, "VOYAGE_DESTINATION"));
            btnReceptionniste.addActionListener(e -> cardLayout.show(mainPanel, "RECEPTIONNISTE"));
            btnDeconnexion.addActionListener(e -> deconnecter());

            centerPanel.add(btnVoyage);
            centerPanel.add(btnBus);
            centerPanel. add(btnDestination);
            centerPanel.add(btnVoyageDest);
            centerPanel. add(btnReceptionniste);
            centerPanel.add(btnDeconnexion);

        } else if (currentUser.isReceptionniste()) {
            centerPanel. setLayout(new GridLayout(2, 2, 20, 20));
            centerPanel.setBorder(BorderFactory.createEmptyBorder(100, 200, 100, 200));
            centerPanel. setBackground(BG_COLOR);

            JButton btnPassager = createMenuButton("Gestion des Passagers", COLOR_PRINCIPAL);
            JButton btnReservation = createMenuButton("Gestion des Réservations", COLOR_PRINCIPAL);
            JButton btnDeconnexion = createMenuButton("Déconnexion", COLOR_DECONNEXION);

            btnPassager.addActionListener(e -> cardLayout.show(mainPanel, "PASSAGER"));
            btnReservation. addActionListener(e -> cardLayout.show(mainPanel, "RESERVATION"));
            btnDeconnexion.addActionListener(e -> deconnecter());

            centerPanel.add(btnPassager);
            centerPanel.add(btnReservation);
            centerPanel. add(new JLabel());
            centerPanel.add(btnDeconnexion);
        }

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void deconnecter() {
        int confirmation = JOptionPane. showConfirmDialog(
                this,
                "Voulez-vous vraiment vous déconnecter? ",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmation == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginPanel().setVisible(true));
        }
    }

    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(400, 80));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button. setBackground(color. brighter());
            }
            public void mouseExited(java.awt.event. MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    public Receptionniste getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities. invokeLater(() -> new LoginPanel().setVisible(true));
    }
}