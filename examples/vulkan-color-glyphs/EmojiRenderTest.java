import java.awt.*;
import javax.swing.*;

public class EmojiRenderTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String mode = System.getProperty("sun.java2d.vulkan", "false");
            System.out.println("Java: " + System.getProperty("java.runtime.version"));
            System.out.println("Toolkit: " + Toolkit.getDefaultToolkit().getClass().getName());
            System.out.println("Vulkan requested: " + mode);
            JFrame frame = new JFrame("Standalone JBR test — Vulkan " + mode);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel panel = new JPanel() {
                @Override protected void paintComponent(Graphics graphics) {
                    super.paintComponent(graphics);
                    Graphics2D g = (Graphics2D) graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.setColor(Color.BLACK);
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
                    g.drawString("Vulkan = " + mode + " | " + System.getProperty("java.runtime.version"), 20, 35);
                    g.drawString("Plain text: Hello, world! 123", 20, 75);
                    g.drawString("Logical font (Dialog):", 20, 115);
                    g.setFont(new Font(Font.DIALOG, Font.PLAIN, 32));
                    g.drawString("A [😀] [🚀] [🐱] [🔥] Z", 20, 160);
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
                    g.drawString("Explicit font: Noto Color Emoji", 20, 205);
                    g.setFont(new Font("Noto Color Emoji", Font.PLAIN, 32));
                    g.drawString("😀 🚀 🐱 🔥", 20, 255);
                    g.dispose();
                }
            };
            panel.setBackground(Color.WHITE);
            panel.setPreferredSize(new Dimension(650, 290));
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
            System.out.println("Graphics configuration: " + panel.getGraphicsConfiguration().getClass().getName());
            Font emoji = new Font("Noto Color Emoji", Font.PLAIN, 32);
            System.out.println("Emoji font: " + emoji.getFontName()
                    + "; supports U+1F600: " + emoji.canDisplay(0x1F600));
        });
    }
}
