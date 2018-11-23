import GUI.Form;
import javax.swing.*;

/**
 * Created by HerrSergio on 31.07.2016.
 */
public class Loader {

    public static void main(String[] args) throws Exception {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        SwingUtilities.invokeLater(() -> {
            JFrame frame = new Form();
            frame.setVisible(true);
        });

    }

}
