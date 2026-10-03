import java.awt.*;
import java.awt.image.*;
import java.io.File;
import javax.imageio.ImageIO;

public class EmojiPixelTest {
    public static void main(String[] args) throws Exception {
        Font font = new Font("Noto Color Emoji", Font.PLAIN, 40);
        if (!font.canDisplay(0x1F600)) throw new AssertionError("Emoji font unavailable");
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
        System.out.println("Runtime: " + System.getProperty("java.runtime.version"));
        System.out.println("Configuration: " + gc.getClass().getName());
        boolean vulkan = Boolean.getBoolean("sun.java2d.vulkan");
        if (vulkan && !gc.getClass().getName().contains("WLVK")) {
            throw new AssertionError("Vulkan was requested but is not active");
        }
        VolatileImage image = gc.createCompatibleVolatileImage(900, 120);
        BufferedImage snapshot = null;
        for (int attempt = 0; attempt < 10; attempt++) {
            image.validate(gc);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 900, 120);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.BLACK);
            g.setFont(font);
            g.drawString("😀 🚀 🐱 🔥", 15, 65);
            // Switch back to the grayscale text path after drawing color glyphs.
            g.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
            g.drawString("After emoji: Hello 123", 600, 65);
            g.dispose();
            snapshot = image.getSnapshot();
            if (!image.contentsLost()) break;
            snapshot = null;
        }
        if (snapshot == null) throw new AssertionError("Surface repeatedly lost");
        int colorPixels = 0, textPixels = 0;
        for (int y = 0; y < 120; y++) {
            for (int x = 0; x < 900; x++) {
                int rgb = snapshot.getRGB(x, y);
                int r = (rgb >>> 16) & 255, green = (rgb >>> 8) & 255, b = rgb & 255;
                if (x < 500 && Math.max(r, Math.max(green, b)) - Math.min(r, Math.min(green, b)) > 30)
                    colorPixels++;
                if (x >= 600 && r < 180 && green < 180 && b < 180) textPixels++;
            }
        }
        System.out.println("Accelerated: " + image.getCapabilities().isAccelerated());
        System.out.println("Color pixels: " + colorPixels + "; text pixels: " + textPixels);
        if (args.length > 0) ImageIO.write(snapshot, "png", new File(args[0]));
        image.flush();
        if (colorPixels < 100) throw new AssertionError("Color emoji were not rendered");
        if (textPixels < 50) throw new AssertionError("Text after emoji was not rendered");
        System.out.println("PASS");
        System.exit(0);
    }
}
