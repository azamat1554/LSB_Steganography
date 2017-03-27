package ru.puredelight.gui;

import ru.puredelight.handlers.EjectHandler;
import ru.puredelight.utils.ImageFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static ru.puredelight.gui.Utilities.setConstraints;

/**
 * Класс панели, которая формирует ГПИ для обработки текста.
 *
 * @author Azamat Abidokov
 */
public class EjectPanel extends JPanel {
    private JLabel secretLbl;
    private JButton loadImgBtn;
    private JButton saveImgBtn;
    private JFileChooser fileChooser;

    private BufferedImage fillImage;
    private BufferedImage secretImage;

    private byte[] fileData;
    private String extension;
    private boolean secretIsImage;

    public EjectPanel() {
        secretLbl = new JLabel("Секретное сообщение");
        loadImgBtn = new JButton("Выбрать контейнер");
        saveImgBtn = new JButton("Сохранить");
        fileChooser = new JFileChooser();

        setProperties();
        addComponents();
        eventHandlers();
    }

    private void setProperties() {
        Font font = new Font("Arial", Font.PLAIN, 16);
        loadImgBtn.setFont(font);
        saveImgBtn.setFont(font);
        saveImgBtn.setEnabled(false);

        secretImage = Utilities.getImage(InjectPanel.class.getClassLoader()
                .getResourceAsStream("images/defaultImg.jpg"));

        secretLbl.setHorizontalTextPosition(JLabel.CENTER);
        secretLbl.setVerticalTextPosition(JLabel.TOP);
        secretLbl.setVerticalAlignment(JLabel.CENTER);
        secretLbl.setHorizontalAlignment(JLabel.CENTER);

        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    private void addComponents() {
        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gbag);

        gbc.fill = GridBagConstraints.BOTH;

        JPanel loadSavePnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadSavePnl.add(loadImgBtn);
        loadSavePnl.add(saveImgBtn);

        //add components on EjectPanel
        gbc.insets = new Insets(2, 2, 2, 2);
        add(loadSavePnl, setConstraints(gbc, 0, 0, 1, 1, 0, 0));
        add(secretLbl, setConstraints(gbc, 0, 1, 1, 1, 1, 1));
    }

    private void eventHandlers() {
        loadImgBtn.addActionListener(e -> {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fillImage = Utilities.getImage(fileChooser.getSelectedFile());

                fileData = new EjectHandler().eject(fillImage);
                if (fileData == null) {
                    JOptionPane.showMessageDialog(this,
                            "В этом файле, нет секретного содержимого.");
                    return;
                }

                extension = Utilities.getExtension(fileData);
                if (Utilities.supportImageType(fileData)) {
                    secretIsImage = true;
                    secretImage = Utilities.getImage(fileData); //new ImageIcon(imageData).getImage();
                    secretLbl.setIcon(Utilities.getScaledImage(secretLbl, secretImage));
                } else {
                    secretIsImage = false;
                    secretLbl.setIcon(null);
                    secretLbl.setText("В контейнере содиржится файл с расширением: " + extension.substring(1));
                }

                saveImgBtn.setEnabled(true);
            }
        });

        saveImgBtn.addActionListener(e -> {
            fileChooser.setSelectedFile(new File("Secret" + extension));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileOutputStream fout = new FileOutputStream(fileChooser.getSelectedFile())) {
                    fout.write(fileData);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        secretLbl.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                if (secretIsImage) {
                    secretLbl.setIcon(Utilities.getScaledImage(secretLbl, secretImage));
                }
            }
        });
    }
}

