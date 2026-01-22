import javax.swing.*;
import java.awt.*;

public class Painter extends JFrame{
    private JPanel painterPanel;
    private JPanel contentPanel;


    public Painter() {
        setSize(800, 600);
        setContentPane(painterPanel);
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
