import java.awt.*;

void main(){
    Container container = new Container();

    ArrayList<Triangle> tris = new ArrayList<>();
    tris.add(new Triangle(new Vertex(100, 100, 100, 1),
            new Vertex(-100, -100, 100, 1),
            new Vertex(-100, 100, -100, 1),
            Color.WHITE));
    tris.add(new Triangle(new Vertex(100, 100, 100, 1),
            new Vertex(-100, -100, 100, 1),
            new Vertex(100, -100, -100, 1),
            Color.RED));
    tris.add(new Triangle(new Vertex(-100, 100, -100, 1),
            new Vertex(100, -100, -100, 1),
            new Vertex(100, 100, 100, 1),
            Color.GREEN));
    tris.add(new Triangle(new Vertex(-100, 100, -100, 1),
            new Vertex(100, -100, -100, 1),
            new Vertex(-100, -100, 100, 1),
            Color.BLUE));

    Mesh mesh = new Mesh(tris);

    Canvas canvas = new Canvas(mesh);

    container.drawCanvas(canvas);
}