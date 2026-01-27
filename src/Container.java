import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class Container extends JFrame{
    private JPanel containerPanel;
    private JPanel contentPanel;
    private JPanel scrollPanel;
    private JScrollPane scrollPane;
    private final Canvas canvas;

    public Container(Canvas canvas) {
        this.canvas = canvas;
        setSize(850, 600);
        setContentPane(containerPanel);
        setVisible(true);

        setTitle("Graphics Test");

        generateScrollComponents();

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(canvas, BorderLayout.CENTER);
        contentPanel.repaint();
        contentPanel.revalidate();


        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void generateScrollComponents(){
        scrollPanel.setLayout(new GridLayout(10,1));

        JPanel rotXPanel = new JPanel(new GridLayout(2,1));
        rotXPanel.add(new JLabel("X Rotation"));
        JSlider rotXSlider = new JSlider(0, 360, 0);
        rotXSlider.setMajorTickSpacing(90);
        rotXSlider.setPaintTicks(true);
        rotXSlider.setPaintLabels(true);
        rotXSlider.addChangeListener(e -> {
            canvas.setRotX(rotXSlider.getValue());
            canvas.repaint();
            canvas.revalidate();
        });
        rotXPanel.add(rotXSlider);
        scrollPanel.add(rotXPanel);

        JPanel rotYPanel = new JPanel(new GridLayout(2,1));
        rotYPanel.add(new JLabel("Y Rotation"));
        JSlider rotYSlider = new JSlider(0, 360, 0);
        rotYSlider.setMajorTickSpacing(90);
        rotYSlider.setPaintTicks(true);
        rotYSlider.setPaintLabels(true);
        rotYSlider.addChangeListener(e -> {
            canvas.setRotY(rotYSlider.getValue());
            canvas.repaint();
            canvas.revalidate();
        });
        rotYPanel.add(rotYSlider);
        scrollPanel.add(rotYPanel);

        JPanel rotZPanel = new JPanel(new GridLayout(2,1));
        rotZPanel.add(new JLabel("Z Rotation"));
        JSlider rotZSlider = new JSlider(0, 360, 0);
        rotZSlider.setMajorTickSpacing(90);
        rotZSlider.setPaintTicks(true);
        rotZSlider.setPaintLabels(true);
        rotZSlider.addChangeListener(e -> {
            canvas.setRotZ(rotZSlider.getValue());
            canvas.repaint();
            canvas.revalidate();
        });
        rotZPanel.add(rotZSlider);
        scrollPanel.add(rotZPanel);

        JPanel wireFramePanel = new JPanel(new GridBagLayout());
        JButton wireFrameButton = new JButton("Enable WireFrame");
        wireFrameButton.addActionListener(e -> {
            canvas.setShowWireFrame(!canvas.isShowWireFrame());
            canvas.repaint();
            canvas.revalidate();
        });
        wireFramePanel.add(wireFrameButton);
        scrollPanel.add(wireFramePanel);
    }
}
