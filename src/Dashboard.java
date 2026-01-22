import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame{
    private JPanel dashPanel;
    private JPanel contentPanel;


    public Dashboard() {
        setSize(800, 600);
        setContentPane(dashPanel);
        setVisible(true);

        setContentPane(new Canvas());

        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void changeContentPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
