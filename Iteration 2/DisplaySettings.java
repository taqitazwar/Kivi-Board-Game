import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class DisplaySettings extends JFrame implements ActionListener {
    private JComboBox<String> themeBox, pieceStyleBox;
    private JCheckBox gridVisibilityCheck;
    private JButton confirmButton, cancelButton, bgColorButton;
    private Color selectedBgColor = Color.BLACK;

    public DisplaySettings() {
        setTitle("Display Settings");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Theme Selection
        panel.add(new JLabel("Theme:"), gbc);
        gbc.gridx = 1;
        String[] themes = {"Light Mode", "Dark Mode"};
        themeBox = new JComboBox<>(themes);
        panel.add(themeBox, gbc);

        // Piece Style Selection
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Piece Style:"), gbc);
        gbc.gridx = 1;
        String[] styles = {"Classic", "Modern", "Wooden"};
        pieceStyleBox = new JComboBox<>(styles);
        panel.add(pieceStyleBox, gbc);

        // Grid Visibility Toggle
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Grid Visibility:"), gbc);
        gbc.gridx = 1;
        gridVisibilityCheck = new JCheckBox("Show Grid", true);
        panel.add(gridVisibilityCheck, gbc);

        // Background Color Selection
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Background Color:"), gbc);
        gbc.gridx = 1;
        bgColorButton = new JButton("Choose Color");
        bgColorButton.addActionListener(e -> chooseBackgroundColor());
        panel.add(bgColorButton, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chooseBackgroundColor() {
        Color color = JColorChooser.showDialog(this, "Choose Background Color", selectedBgColor);
        if (color != null) {
            selectedBgColor = color;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmButton) {
            applySettings();
            JOptionPane.showMessageDialog(this, "Settings Applied!");
        } else if (e.getSource() == cancelButton) {
            dispose(); // Close without saving
        }
    }

    private void applySettings() {
        String selectedTheme = (String) themeBox.getSelectedItem();
        String selectedPieceStyle = (String) pieceStyleBox.getSelectedItem();
        boolean gridVisible = gridVisibilityCheck.isSelected();

        // Apply settings (for now, just printing them)
        System.out.println("Applying settings:");
        System.out.println("Theme: " + selectedTheme);
        System.out.println("Piece Style: " + selectedPieceStyle);
        System.out.println("Grid Visibility: " + gridVisible);
        System.out.println("Background Color: " + selectedBgColor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisplaySettings::new);
    }
}


