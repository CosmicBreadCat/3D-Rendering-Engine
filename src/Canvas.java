import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel{
    private JPanel canvasPanel;

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.drawRect(50, 50, 100, 100);
        g.fillOval(200, 50, 80, 80);
    }
}
