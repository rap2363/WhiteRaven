package web.transport;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * Encapsulates a png image we send over the wire from the WhiteRavenServer to each client. This is reconstructed
 */
public class ImageMessage implements ByteSerializable<BufferedImage> {
    private static final int SCREEN_WIDTH = nes.PPU.SCREEN_WIDTH;
    private static final int SCREEN_HEIGHT = nes.PPU.SCREEN_HEIGHT;
    private static final int INT_SIZE = 4;
    private byte[] imageMessageSize;
    private byte[] imageMessage;

    public ImageMessage(final int[] imageData) {
        final BufferedImage image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        final int[] a = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(imageData, 0, a, 0, imageData.length);

        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);
            imageMessageSize = ByteBuffer.allocate(INT_SIZE).putInt(byteArrayOutputStream.size()).array();
            imageMessage = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Could not serialize image: " + e.getStackTrace());
        }
    }

    /**
     * Serialize the image into an array of bytes. The protocol we follow is to compress the image using png, then
     * sending the length of the message in an int, then sending the compressed image data over as bytes.
     *
     * @return
     */
    @Override
    public byte[] serialize() {
        final byte[] arr = new byte[imageMessageSize.length + imageMessage.length];
        int i = 0;
        for (byte b : imageMessageSize) {
            arr[i++] = b;
        }
        for (byte b : imageMessage) {
            arr[i++] = b;
        }
        return arr;
    }

    /**
     * Deserialize a message over the socket into full blown image data.
     *
     * @param inputStream
     * @return
     */
    public static int[] deserialize(final InputStream inputStream) throws IOException {
        final byte[] sizeAr = new byte[INT_SIZE];
        inputStream.read(sizeAr);
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
        final byte[] imageAr = new byte[size];

        // Wait until we've received all bytes required for an image
        while (inputStream.available() < size);

        inputStream.read(imageAr);
        final BufferedImage im = ImageIO.read(new ByteArrayInputStream(imageAr));
        final BufferedImage image = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(im, 0, 0, null);
        g.dispose();

        return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }
}
