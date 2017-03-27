package ru.puredelight.gui;

import ru.puredelight.handlers.Config;
import ru.puredelight.handlers.InjectHandler;
import ru.puredelight.utils.ImageFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import static ru.puredelight.gui.Utilities.setConstraints;

/**
 * Класс панели, которая формирует ГПИ для внедрения сообщения в изображение.
 *
 * @author Azamat Abidokov
 */
public class InjectPanel extends JPanel {
    private JLabel emptyImgLbl;
    private JLabel secretLbl;
    private JLabel fillImgLbl;
    private JButton loadImgBtn;
    private JButton saveImgBtn;
    private JButton injectBtn;
    private JButton estimateBtn;
    private JLabel sliderLbl;
    private JSlider slider;
    private JLabel maxSizeLbl;
    private JFileChooser fileChooser;

    private BufferedImage defaultImage;
    private BufferedImage emptyImage;
    private BufferedImage fillImage;
    private File secretFile;

    private String extension;
    private boolean secretIsImage = true;

    public InjectPanel() {
        emptyImgLbl = new JLabel("Пустой контейнер");
        secretLbl = new JLabel("Секрет");
        fillImgLbl = new JLabel("Заполненный контейнер");
        sliderLbl = new JLabel("Кол-во заменяемых бит");
        maxSizeLbl = new JLabel();

        loadImgBtn = new JButton("Выбрать контейнер");
        injectBtn = new JButton("Выбрать сообщение");
        saveImgBtn = new JButton("Сохранить");
        estimateBtn = new JButton("Оценка качества встраивания");

        slider = new JSlider(JSlider.HORIZONTAL);

        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        fileChooser = new JFileChooser();

        setProperties();
        addComponents();
        eventHandlers();

        defaultImage = Utilities.getImage(InjectPanel.class.getClassLoader()
                .getResourceAsStream("images/defaultImg.jpg"));

        emptyImage = defaultImage;
        fillImage = defaultImage;
        secretFile = null;
    }

    private void setProperties() {
        Font font = new Font("Arial", Font.PLAIN, 16);
        loadImgBtn.setFont(font);

        saveImgBtn.setFont(font);
        saveImgBtn.setEnabled(false);

        injectBtn.setFont(font);
        injectBtn.setEnabled(false);

        estimateBtn.setFont(font);
        estimateBtn.setEnabled(false);

        emptyImgLbl.setHorizontalTextPosition(JLabel.CENTER);
        emptyImgLbl.setVerticalTextPosition(JLabel.TOP);
        emptyImgLbl.setHorizontalAlignment(JLabel.CENTER);
        emptyImgLbl.setVerticalAlignment(JLabel.CENTER);

        fillImgLbl.setHorizontalTextPosition(JLabel.CENTER);
        fillImgLbl.setVerticalTextPosition(JLabel.TOP);
        fillImgLbl.setVerticalAlignment(JLabel.CENTER);
        fillImgLbl.setHorizontalAlignment(JLabel.CENTER);

        secretLbl.setHorizontalTextPosition(JLabel.CENTER);
        secretLbl.setVerticalTextPosition(JLabel.TOP);
        secretLbl.setVerticalAlignment(JLabel.CENTER);
        secretLbl.setHorizontalAlignment(JLabel.CENTER);

        slider.setMinimum(1);
        slider.setMaximum(7);
        slider.setValue(1);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(1);
        slider.setPaintLabels(true);

        sliderLbl.setHorizontalAlignment(JLabel.CENTER);

        maxSizeLbl.setVisible(false);

        fileChooser.setMultiSelectionEnabled(false);
    }

    private void addComponents() {
        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gbag);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);

        add(loadImgBtn, setConstraints(gbc, 0, 0, 1, 1, 0, 0));
        add(injectBtn, setConstraints(gbc, 1, 0, 1, 1, 0, 0));
        add(saveImgBtn, setConstraints(gbc, 2, 0, 1, 1, 0, 0));
        add(sliderLbl, setConstraints(gbc, 2, 1, 1, 1, 0, 0));
        add(slider, setConstraints(gbc, 2, 2, 1, 1, 0, 0));
        add(maxSizeLbl, setConstraints(gbc, 0, 1, 1, 2, 0, 0));


        add(emptyImgLbl, setConstraints(gbc, 0, 3, 1, 1, 1, 1));
        add(secretLbl, setConstraints(gbc, 1, 3, 1, 1, 1, 1));
        add(fillImgLbl, setConstraints(gbc, 2, 3, 1, 1, 1, 1));
        add(estimateBtn, setConstraints(gbc, 0, 4, 3, 1, 0, 0.2));
    }

    private void eventHandlers() {
        loadImgBtn.addActionListener(e -> {
            fileChooser.addChoosableFileFilter(new ImageFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File image = fileChooser.getSelectedFile();
                emptyImage = Utilities.getImage(image);
                extension = Utilities.getExtension(image);
                System.out.println(extension);

                emptyImgLbl.setIcon(Utilities.getScaledImage(emptyImgLbl, emptyImage));

                injectBtn.setEnabled(true);
                maxSizeLbl.setText("<html><p>Максимальный размер встраиваемого файла: " + maxSize() / 1024 + " Кбайт</p></html>");
                maxSizeLbl.setVisible(true);


                if (secretFile != null) {
                    fillImage = new InjectHandler().inject(emptyImage, secretFile, slider.getValue());
                    fillImgLbl.setIcon(Utilities.getScaledImage(emptyImgLbl, fillImage));

                    saveImgBtn.setEnabled(false);
                    estimateBtn.setEnabled(false);
                }
            }
        });

        saveImgBtn.addActionListener(e -> {
            fileChooser.addChoosableFileFilter(new ImageFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setSelectedFile(new File("Secret"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath() + extension);
                try {
                    ImageIO.write(fillImage, extension.substring(1), file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        injectBtn.addActionListener(e -> {
            fileChooser.resetChoosableFileFilters();

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                if (file.length() >= maxSize()) {
                    JOptionPane.showMessageDialog(this,
                            "Слишком большой файл");
                    return;
                }

                secretFile = file;
                if (Utilities.supportImageType(file)) { //если секрет - изображение
                    secretIsImage = true;
                    secretLbl.setText("Секретное изображение");
                    secretLbl.setIcon(Utilities.getScaledImage(emptyImgLbl, Utilities.getImage(secretFile)));
                } else {
                    secretIsImage = false;
                    secretLbl.setIcon(null);
                    secretLbl.setText("<html>Секретный файл:<br><br><i>" + file.getName() + "</i></html>");
                }

                fillImage = new InjectHandler().inject(emptyImage, file, slider.getValue());
                fillImgLbl.setIcon(Utilities.getScaledImage(emptyImgLbl, fillImage));

                saveImgBtn.setEnabled(true);
                estimateBtn.setEnabled(true);
            }
        });

        //при изменении положения ползунка
        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                if (secretFile == null) {
                    return;
                } else if (secretFile.length() >= maxSize()) {
                    JOptionPane.showMessageDialog(this,
                            "В контейнере недостаточно места, для внедрения");
                    slider.setValue(slider.getValue() + 1);
                    return;
                }

                fillImage = new InjectHandler().inject(emptyImage, secretFile, slider.getValue());
                fillImgLbl.setIcon(Utilities.getScaledImage(emptyImgLbl, fillImage));
            }
        });

        estimateBtn.addActionListener(e -> {
            byte[] emptyPixels = ((DataBufferByte) emptyImage.getRaster().getDataBuffer()).getData();
            byte[] fillPixels = ((DataBufferByte) fillImage.getRaster().getDataBuffer()).getData();

            JOptionPane.showMessageDialog(this,
                    String.format("Среднеквадратическая погрешность: %.2f %%", MSE(emptyPixels, fillPixels))
                            + String.format("\nОтношение сигнал/шум: %.2f дБ", SNR(emptyPixels, fillPixels)));
        });

        emptyImgLbl.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                emptyImgLbl.setIcon(Utilities.getScaledImage(emptyImgLbl, emptyImage));
            }
        });

        fillImgLbl.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                fillImgLbl.setIcon(Utilities.getScaledImage(emptyImgLbl, fillImage));
            }
        });

        secretLbl.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                if (secretIsImage) {
                    secretLbl.setIcon(Utilities.getScaledImage(emptyImgLbl,
                            secretFile == null ? defaultImage : Utilities.getImage(secretFile)));
                }
            }
        });
    }

    public double MSE(byte[] emptyCont, byte[] fillCont) {
        double result = 0;
        int length = emptyCont.length;
        for (int i = 0; i < length; i++) {
            result += Math.pow(emptyCont[i] - fillCont[i], 2);
        }
        result = result / emptyCont.length;
        return result;
    }

    public double SNR(byte[] emptyCont, byte[] fillCont) {
        double divident = 0, divider = 0;
        int length = emptyCont.length;
        for (int i = 0; i < length; i++) {
            divident += emptyCont[i] * emptyCont[i];
            divider += Math.pow(emptyCont[i] - fillCont[i], 2);
        }
        return Math.log10(divident / divider); //Db
    }

    /**
     * Максимальный размер, который можно встроить в контейнер в байтах.
     */
    private long maxSize() {
        return (long) ((emptyImage.getWidth()
                * emptyImage.getHeight()
                * slider.getValue()
                * 3 - Config.HEADER_LENGTH * 8) / 8);
    }
}
