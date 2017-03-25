package ru.puredelight.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Класс формы, с него начинается выполнение приложения
 *
 * @author Azamat Abidokov
 */
public class MainWindow extends JFrame {

    private MainWindow() {
        super("Курсовая по стеганографии");

        JTabbedPane jtp = new JTabbedPane();

        InjectPanel injectPanel = new InjectPanel();
        jtp.addTab("Внедрение", injectPanel);
        jtp.addTab("Извлечение", new EjectPanel());
        add(jtp);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        setSize(new Dimension(900, 500));
        setLocationByPlatform(true);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
