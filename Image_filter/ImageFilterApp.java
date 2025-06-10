import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageFilterApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage filteredImage;
    private JLabel imageLabel;

    public ImageFilterApp() {
        super("Image Filter Application");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());

        // Image display label
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();

        JButton loadButton = new JButton("Load Image");
        JButton grayscaleButton = new JButton("Grayscale");
        JButton sepiaButton = new JButton("Sepia");
        JButton invertButton = new JButton("Invert");
        JButton blurButton = new JButton("Blur");
        JButton resetButton = new JButton("Reset");
        JButton saveButton = new JButton("Save Image");

        buttonPanel.add(loadButton);
        buttonPanel.add(grayscaleButton);
        buttonPanel.add(sepiaButton);
        buttonPanel.add(invertButton);
        buttonPanel.add(blurButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        loadButton.addActionListener(e -> loadImage());
        grayscaleButton.addActionListener(e -> applyGrayscale());
        sepiaButton.addActionListener(e -> applySepia());
        invertButton.addActionListener(e -> applyInvert());
        blurButton.addActionListener(e -> applyBlur());
        resetButton.addActionListener(e -> resetImage());
        saveButton.addActionListener(e -> saveImage());

        setVisible(true);
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if(option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                originalImage = ImageIO.read(file);
                filteredImage = deepCopy(originalImage);
                imageLabel.setIcon(new ImageIcon(filteredImage));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading image.");
            }
        }
    }

    private void applyGrayscale() {
        if (filteredImage == null) return;
        for (int y = 0; y < filteredImage.getHeight(); y++) {
            for (int x = 0; x < filteredImage.getWidth(); x++) {
                int rgb = filteredImage.getRGB(x, y);
                Color color = new Color(rgb);
                int gray = (int)(color.getRed()*0.3 + color.getGreen()*0.59 + color.getBlue()*0.11);
                Color grayColor = new Color(gray, gray, gray);
                filteredImage.setRGB(x, y, grayColor.getRGB());
            }
        }
        imageLabel.setIcon(new ImageIcon(filteredImage));
    }

    private void applySepia() {
        if (filteredImage == null) return;
        for (int y = 0; y < filteredImage.getHeight(); y++) {
            for (int x = 0; x < filteredImage.getWidth(); x++) {
                int rgb = filteredImage.getRGB(x, y);
                Color color = new Color(rgb);

                int tr = (int)(0.393 * color.getRed() + 0.769 * color.getGreen() + 0.189 * color.getBlue());
                int tg = (int)(0.349 * color.getRed() + 0.686 * color.getGreen() + 0.168 * color.getBlue());
                int tb = (int)(0.272 * color.getRed() + 0.534 * color.getGreen() + 0.131 * color.getBlue());

                tr = Math.min(255, tr);
                tg = Math.min(255, tg);
                tb = Math.min(255, tb);

                Color sepia = new Color(tr, tg, tb);
                filteredImage.setRGB(x, y, sepia.getRGB());
            }
        }
        imageLabel.setIcon(new ImageIcon(filteredImage));
    }

    private void applyInvert() {
        if (filteredImage == null) return;
        for (int y = 0; y < filteredImage.getHeight(); y++) {
            for (int x = 0; x < filteredImage.getWidth(); x++) {
                int rgb = filteredImage.getRGB(x, y);
                Color color = new Color(rgb);

                Color inverted = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                filteredImage.setRGB(x, y, inverted.getRGB());
            }
        }
        imageLabel.setIcon(new ImageIcon(filteredImage));
    }

    private void applyBlur() {
        if (filteredImage == null) return;

        // Simple Box Blur Kernel (3x3)
        float[] matrix = {
            1f/9f, 1f/9f, 1f/9f,
            1f/9f, 1f/9f, 1f/9f,
            1f/9f, 1f/9f, 1f/9f,
        };
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix));
        filteredImage = op.filter(filteredImage, null);

        imageLabel.setIcon(new ImageIcon(filteredImage));
    }

    private void resetImage() {
        if (originalImage == null) return;
        filteredImage = deepCopy(originalImage);
        imageLabel.setIcon(new ImageIcon(filteredImage));
    }

    private void saveImage() {
        if (filteredImage == null) return;
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if(option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String fileName = file.getName();
                String format = "png";
                // Infer format from file extension if possible
                int dotIndex = fileName.lastIndexOf('.');
                if(dotIndex > 0 && dotIndex < fileName.length() - 1) {
                    format = fileName.substring(dotIndex + 1).toLowerCase();
                }
                ImageIO.write(filteredImage, format, file);
                JOptionPane.showMessageDialog(this, "Image saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image.");
            }
        }
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageFilterApp());
    }
}
