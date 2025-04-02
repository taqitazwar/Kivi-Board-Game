import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DisplaySettings extends JFrame implements ActionListener {
    private JComboBox<String> modeBox;
    private JButton applyButton, cancelButton;
    private JPanel previewPanel;

    public DisplaySettings() {
        // Use Nimbus Look and Feel if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) { }

        setTitle("Display Settings");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Display Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center panel for mode selection and preview
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel modeLabel = new JLabel("Select Mode:");
        modeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String[] modes = {"Mono Magic", "Vibrant Vibes", "Rainbow Surprise"};
        modeBox = new JComboBox<>(modes);
        modeBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        modeBox.addActionListener(e -> updatePreview());
        selectionPanel.add(modeLabel);
        selectionPanel.add(modeBox);
        centerPanel.add(selectionPanel, BorderLayout.NORTH);

        previewPanel = new JPanel();
        previewPanel.setPreferredSize(new Dimension(300, 150));
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        updatePreview();
        centerPanel.add(previewPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        applyButton = new JButton("Apply");
        applyButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        applyButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        cancelButton.addActionListener(this);
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void updatePreview() {
        String selectedMode = (String) modeBox.getSelectedItem();
        previewPanel.removeAll();
        previewPanel.setLayout(new GridBagLayout());
        JLabel previewLabel = new JLabel();
        previewLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        if ("Mono Magic".equals(selectedMode)) {
            Color sample = new Color(30, 144, 255);
            Color transformed = ColorBlindnessFilter.applyMonochromacy(sample);
            previewPanel.setBackground(transformed);
            previewLabel.setText("Mono Magic Preview");
        } else if ("Vibrant Vibes".equals(selectedMode)) {
            Color sample = new Color(220, 20, 60);
            Color transformed = ColorBlindnessFilter.applyDichromacy(sample);
            previewPanel.setBackground(transformed);
            previewLabel.setText("Vibrant Vibes Preview");
        } else if ("Rainbow Surprise".equals(selectedMode)) {
            Color sample = new Color(50, 205, 50);
            Color transformed = ColorBlindnessFilter.applyAnomalousTrichromacy(sample);
            previewPanel.setBackground(transformed);
            previewLabel.setText("Rainbow Surprise Preview");
        }
        previewLabel.setForeground(Color.BLACK);
        previewPanel.add(previewLabel, new GridBagConstraints());
        previewPanel.revalidate();
        previewPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            String selectedMode = (String) modeBox.getSelectedItem();
            if ("Mono Magic".equals(selectedMode)) {
                ColorBlindnessFilter.setMode(ColorBlindnessFilter.Mode.MONOCHROMACY);
            } else if ("Vibrant Vibes".equals(selectedMode)) {
                ColorBlindnessFilter.setMode(ColorBlindnessFilter.Mode.DICHROMACY);
            } else if ("Rainbow Surprise".equals(selectedMode)) {
                ColorBlindnessFilter.setMode(ColorBlindnessFilter.Mode.ANOMALOUS_TRICHROMACY);
            }
            JOptionPane.showMessageDialog(this, "Display settings applied: " + selectedMode,
                "Settings Applied", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    // Inner static class for simulating color blindness effects
    public static class ColorBlindnessFilter {
        public enum Mode {
            NORMAL,
            MONOCHROMACY,
            DICHROMACY,
            ANOMALOUS_TRICHROMACY
        }
        private static Mode currentMode = Mode.NORMAL;
        public static void setMode(Mode mode) {
            currentMode = mode;
        }
        public static Mode getMode() {
            return currentMode;
        }
        // Mono Magic: Convert to grayscale
        public static Color applyMonochromacy(Color color) {
            int gray = (int)(color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
            return new Color(gray, gray, gray);
        }
        // Vibrant Vibes: Simple dichromacy simulation
        public static Color applyDichromacy(Color color) {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            int r2 = clamp((int)(0.625 * r + 0.375 * g));
            int g2 = clamp((int)(0.7 * r + 0.3 * g));
            int b2 = b;
            return new Color(r2, g2, b2);
        }
        // Rainbow Surprise: Blend normal with dichromacy simulation
        public static Color applyAnomalousTrichromacy(Color color) {
            Color dichro = applyDichromacy(color);
            int r = (int)(0.5 * color.getRed() + 0.5 * dichro.getRed());
            int g = (int)(0.5 * color.getGreen() + 0.5 * dichro.getGreen());
            int b = (int)(0.5 * color.getBlue() + 0.5 * dichro.getBlue());
            return new Color(clamp(r), clamp(g), clamp(b));
        }
        public static Color transformColor(Color color) {
            switch(currentMode) {
                case MONOCHROMACY:
                    return applyMonochromacy(color);
                case DICHROMACY:
                    return applyDichromacy(color);
                case ANOMALOUS_TRICHROMACY:
                    return applyAnomalousTrichromacy(color);
                default:
                    return color;
            }
        }
        private static int clamp(int value) {
            return Math.max(0, Math.min(255, value));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisplaySettings());
    }
}
