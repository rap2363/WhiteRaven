package screen;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;
import javax.swing.JPanel;
import nes.PPU;

/**
 * Class to encompass drawing/refreshing the main main.java.screen.
 */
final public class MainScreen {
    private static final int SCREEN_WIDTH = PPU.SCREEN_WIDTH;
    private static final int SCREEN_HEIGHT = PPU.SCREEN_HEIGHT;

    private final WhiteRavenPanel panel;
    private final JFrame frame;
    private int[] imageData;

    public MainScreen() {
        panel = new MainScreen.WhiteRavenPanel();
        frame = new JFrame("WhiteRaven");
        frame.getContentPane().add(BorderLayout.CENTER, panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setVisible(true);
        imageData = new int[SCREEN_WIDTH * SCREEN_HEIGHT];
    }

    /**
     * Set the new image
     *
     * @param image
     */
    public synchronized void push(final int[] image) {
        if (image != null) {
            imageData = image;
        }
    }

    /**
     * Paint the image
     */
    public synchronized void redraw() {
        this.panel.processNewImage(imageData);
        this.panel.repaint();
    }

    private static class WhiteRavenPanel extends JPanel {
        private final BufferedImage screenImage;
        private final int[] imgData;

        public WhiteRavenPanel() {
            super();
            screenImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
            imgData = ((DataBufferInt) screenImage.getRaster().getDataBuffer()).getData();
        }

        public void paintComponent(Graphics g) {
            g.drawImage(this.screenImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }

        public void processNewImage(final int[] image) {
            System.arraycopy(image, 0, imgData, 0, image.length);
        }
    }
}
