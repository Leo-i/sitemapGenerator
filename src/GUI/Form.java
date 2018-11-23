package GUI;


import downloader.Data;
import downloader.Save;
import downloader.State;

import javax.swing.*;
import java.awt.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class Form extends JFrame {


    private JPanel rootPanel;
    private JButton chooseButton;
    private JButton stopButton;
    private JButton startOrPauseButton;
    private JTextField fileName;
    private JLabel textForSitesDone;
    private JTextField sourceFile;
    private JLabel textForTime;
    private JLabel sitesFound;
    private JLabel sitesInProcess;
    private JLabel sitesDone;
    private JLabel time;
    private JLabel textForPaused;
    private JLabel textForFoundSites;
    private JLabel textForSitesInProcess;
    private JLabel pausedLabel;
    private JButton saveButton;


    private AtomicBoolean paused = new AtomicBoolean(true);

    private Data data;
    private String originalSite;


    public Form() {

        setMinimumSize(new Dimension(520, 280));
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JFileChooser fileChooser = new JFileChooser();
        timer();

        chooseButton.addActionListener(e -> {
            fileChooser.setDialogTitle("Выбор директории");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = fileChooser.showOpenDialog(Form.this);
            String file;
            try {
                file = fileChooser.getSelectedFile().getPath();
            } catch (NullPointerException ex) {
                file = "";
            }
            if (result == JFileChooser.APPROVE_OPTION)
                fileName.setText(file);
        });


        startOrPauseButton.addActionListener(e -> {

//            if (data != null && data.isDone())
//                data = null;

            if (data == null || data.isDone()) {
                String textFromSite = getTextFromSourceSite();
                if (!textFromSite.equals("")) {

                    data = new Data(2 * Runtime.getRuntime().availableProcessors(), textFromSite);
                    originalSite = textFromSite;

                    paused.set(false);
                    startOrPauseButton.setText("Пауза");
                    stopButton.setEnabled(true);

                } else {
                    JOptionPane.showMessageDialog(null, "Введите url");
                }

            } else if (paused.get()) {
                resume();
            } else {
                pause();
            }

        });


        stopButton.addActionListener(e -> {
            stop();
            reset();
            data = null;
        });


        saveButton.addActionListener(e -> {
            if (data == null)
                return;

            pause();
            boolean save = false;
            if (!data.isDone()) {
                String options[] = {"да", "нет"};
                int option = JOptionPane.showOptionDialog(null, "поиск не завершен, желаете завершить" +
                                " поиск и сохранить имеющиеся файлы?", "Внимание",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, null);

                if (option == JOptionPane.YES_OPTION) {
                    save = true;
                }

            } else {
                save = true;
            }


            if (save) {

                stop();
                reset();
                new Save(data.getUrlMap(), getTextFromFileName(), originalSite);
                data = null;
            } else
                resume();

        });
    }

    private void stop() {

        data.close();
        paused.set(true);
        startOrPauseButton.setText("Старт");
        stopButton.setEnabled(false);
    }


    private void pause() {
        data.setPaused(true);
        paused.set(true);
        startOrPauseButton.setText("Старт");
        stopButton.setEnabled(true);
    }

    private void resume() {
        data.setPaused(false);
        paused.set(false);
        startOrPauseButton.setText("Пауза");
        stopButton.setEnabled(true);
    }


    private String getTextFromSourceSite() {
        return sourceFile.getText().trim();
    }

    private String getTextFromFileName() {
        return fileName.getText().trim();
    }

    private void timer() {
        new Timer(10, e -> {
            if (paused.get())
                return;

            if (data != null) {
                State state = data.getState();
                textForFoundSites.setText(String.valueOf(state.getLinksDetectedCount()));
                textForSitesInProcess.setText(String.valueOf(state.getLinksInProcessCount()));
                textForSitesDone.setText(String.valueOf(state.getLinksDetectedCount() - state.getLinksInProcessCount()));
                textForPaused.setText((state.isPaused() ? "да" : "нет"));
                textForTime.setText(state.getTimeElapsed() / 1000.0 + " сек.");

                if (state.getLinksInProcessCount() == 0) {
                    pause();
                    stop();
                }

            } else {
                reset();
            }
        }).start();
    }

    private void reset() {
        textForFoundSites.setText("0");
        textForSitesInProcess.setText("0");
        textForSitesDone.setText("0");
        textForPaused.setText("0");
        textForTime.setText("0");
    }


}
