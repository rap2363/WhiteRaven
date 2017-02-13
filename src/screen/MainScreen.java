package screen;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class to encompass drawing/refreshing the main screen.
 */
final public class MainScreen
{
    private final WhiteRavenPanel panel;
    private final JFrame frame;

    private static final int SCREEN_WIDTH = nes.PPU.SCREEN_WIDTH;
    private static final int SCREEN_HEIGHT = nes.PPU.SCREEN_HEIGHT;

    public MainScreen() {
        panel = new MainScreen.WhiteRavenPanel();
        frame = new JFrame("WhiteRaven");
        frame.getContentPane().add(BorderLayout.CENTER, panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setVisible(true);
    }

    public void redraw(final Image screenImage) {
        this.panel.setImage(screenImage);
        this.frame.repaint();
    }

    private static class WhiteRavenPanel extends JPanel
    {
        private static final long serialVersionUID = 1L;
        private Image screenImage;

        public void paintComponent(Graphics g)
        {
            if (screenImage != null) {
                g.drawImage(this.screenImage, 0, 0, this.getWidth(), this.getHeight(), null);
            }
        }

        public void setImage(final Image screenImage) {
            this.screenImage = screenImage;
        }
    }
}
