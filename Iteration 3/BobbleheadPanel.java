import java.awt.*;
import javax.swing.*;

public class BobbleheadPanel extends JPanel implements PlayerSettings.BobbleheadPanel {
    private Color color;
    private int playerId;
    private boolean isCpu;
    private double bobbleAngle = 0;
    private double bobbleSpeed = 0.1;
    private double bobbleMagnitude = 5;

    public BobbleheadPanel(int playerId, Color color) {
        this.playerId = playerId;
        this.color = color;
        this.setOpaque(false);
    }
    
    public void setCpu(boolean isCpu) {
        this.isCpu = isCpu;
        repaint();
    }
    
    public Color getColor() {
        return color;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
        repaint();
    }
    
    public void updateAnimation() {
        bobbleAngle += bobbleSpeed;
        if (bobbleAngle > 2 * Math.PI) {
            bobbleAngle -= 2 * Math.PI;
        }
    }
    
    public void setBobbleSpeed(double speed) {
        this.bobbleSpeed = speed;
    }
    
    public void setBobbleMagnitude(double magnitude) {
        this.bobbleMagnitude = magnitude;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        double offsetX = Math.sin(bobbleAngle) * bobbleMagnitude;
        double offsetY = Math.cos(bobbleAngle) * (bobbleMagnitude / 2);
        
        int bodyWidth = width / 3;
        int bodyHeight = height / 3;
        int bodyX = (width - bodyWidth) / 2;
        int bodyY = height - bodyHeight;
        
        // Body color slightly darker than head
        Color bodyColor = color.darker();
        g2d.setColor(DisplaySettings.ColorBlindnessFilter.transformColor(bodyColor));
        
        if (isCpu) {
            g2d.fillRect(bodyX, bodyY, bodyWidth, bodyHeight);
            g2d.setColor(Color.GRAY);
            g2d.drawLine(bodyX + 5, bodyY + 5, bodyX + bodyWidth - 5, bodyY + 5);
            g2d.drawLine(bodyX + 5, bodyY + bodyHeight/2, bodyX + bodyWidth - 5, bodyY + bodyHeight/2);
            g2d.drawRect(bodyX + bodyWidth/3, bodyY + 8, bodyWidth/3, bodyHeight/3);
        } else {
            g2d.fillRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.fillArc(bodyX, bodyY, bodyWidth, bodyHeight/3, 0, 180);
        }
        
        int neckWidth = width / 6;
        int neckHeight = height / 10;
        int neckX = (width - neckWidth) / 2;
        int neckY = bodyY - neckHeight;
        
        g2d.setColor(DisplaySettings.ColorBlindnessFilter.transformColor(new Color(255, 222, 173)));
        g2d.fillRect(neckX, neckY, neckWidth, neckHeight);
        
        int headDiameter = width / 2 + 10;
        int headX = (width - headDiameter) / 2 + (int)offsetX;
        int headY = neckY - headDiameter + 5 + (int)offsetY;
        
        g2d.setColor(DisplaySettings.ColorBlindnessFilter.transformColor(color));
        g2d.fillOval(headX, headY, headDiameter, headDiameter);
        
        if (isCpu) {
            g2d.setColor(Color.BLACK);
            int faceSize = headDiameter * 2/3;
            int faceX = headX + (headDiameter - faceSize) / 2;
            int faceY = headY + (headDiameter - faceSize) / 2;
            g2d.fillRect(faceX, faceY, faceSize, faceSize);
            g2d.setColor(Color.GREEN);
            g2d.fillRect(faceX + 2, faceY + 2, faceSize - 4, faceSize - 4);
            g2d.setColor(Color.RED);
            g2d.fillRect(faceX + faceSize/4 - 2, faceY + faceSize/3, 4, 4);
            g2d.fillRect(faceX + faceSize*3/4 - 2, faceY + faceSize/3, 4, 4);
            g2d.drawLine(faceX + faceSize/4, faceY + faceSize*2/3, 
                         faceX + faceSize*3/4, faceY + faceSize*2/3);
        } else {
            g2d.setColor(Color.WHITE);
            int eyeSize = headDiameter / 6;
            g2d.fillOval(headX + headDiameter/3 - eyeSize/2, headY + headDiameter/3, eyeSize, eyeSize);
            g2d.fillOval(headX + 2*headDiameter/3 - eyeSize/2, headY + headDiameter/3, eyeSize, eyeSize);
            g2d.setColor(Color.BLACK);
            int pupilSize = eyeSize / 2;
            g2d.fillOval(headX + headDiameter/3 - pupilSize/2, headY + headDiameter/3 + eyeSize/4, pupilSize, pupilSize);
            g2d.fillOval(headX + 2*headDiameter/3 - pupilSize/2, headY + headDiameter/3 + eyeSize/4, pupilSize, pupilSize);
            int mouthWidth = headDiameter / 3;
            int mouthHeight = headDiameter / 10;
            g2d.setColor(Color.RED);
            g2d.fillArc(headX + (headDiameter - mouthWidth)/2, 
                       headY + 2*headDiameter/3, 
                       mouthWidth, mouthHeight, 0, 180);
        }
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String playerText = "P" + (playerId + 1);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(playerText);
        g2d.drawString(playerText, 
                      (width - textWidth) / 2, 
                      height - 5);
    }
}
