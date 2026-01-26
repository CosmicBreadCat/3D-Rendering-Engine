import javax.swing.*;
import java.awt.*;

public class Container extends JFrame{
    private JPanel containerPanel;
    private JPanel contentPanel;


    public Container() {
        setSize(600, 600);
        setContentPane(containerPanel);
        setVisible(true);

        setTitle("Graphics Test");

        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void drawCanvas(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
