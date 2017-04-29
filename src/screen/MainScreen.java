package screen;

import memory.CircularBuffer;
import sun.applet.Main;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class to encompass drawing/refreshing the main screen.
 */
final public class MainScreen
{
    private final WhiteRavenPanel panel;
    private final JFrame frame;
    private final CircularBuffer<int[]> imageBuffer;

    private static final int SCREEN_WIDTH = nes.PPU.SCREEN_WIDTH;
    private static final int SCREEN_HEIGHT = nes.PPU.SCREEN_HEIGHT;
    private static final int IMAGE_BUFFER_SIZE = 1;

    public MainScreen() {
        panel = new MainScreen.WhiteRavenPanel();
        frame = new JFrame("WhiteRaven");
        frame.getContentPane().add(BorderLayout.CENTER, panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setVisible(true);

        imageBuffer = new CircularBuffer<>(IMAGE_BUFFER_SIZE);
    }

    /**
     * Push a new image onto the buffer
     *
     * @param image
     */
    public synchronized void push(final int[] image) {
        imageBuffer.push(image);
    }

    /**
     * Pop an image off the buffer to paint
     */
    public synchronized void redraw() {
        if (imageBuffer.peek() != null) {
            this.panel.processNewImage(imageBuffer.get());
        }
        this.panel.repaint();
    }

    private static class WhiteRavenPanel extends JPanel
    {
        private final BufferedImage screenImage;
        private final int[] imgData;

        public WhiteRavenPanel() {
            super();
            screenImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
            imgData = ((DataBufferInt)screenImage.getRaster().getDataBuffer()).getData();
        }

        public void paintComponent(Graphics g)
        {
            g.drawImage(this.screenImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }

        public void processNewImage(final int[] image) {
            System.arraycopy(image, 0, imgData, 0, image.length);
        }
    }
}
