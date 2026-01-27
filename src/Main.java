import java.awt.*;

void main(){
    ArrayList<Triangle> tris = new ArrayList<>();
    // ---------- FRONT FACE (z = +100) ----------
    tris.add(new Triangle(
            new Vertex(-100, -100, 100, 1),
            new Vertex(100, -100, 100, 1),
            new Vertex(100, 100, 100, 1),
            Color.RED));

    tris.add(new Triangle(
            new Vertex(-100, -100, 100, 1),
            new Vertex(100, 100, 100, 1),
            new Vertex(-100, 100, 100, 1),
            Color.RED));


// ---------- BACK FACE (z = -100) ----------
    tris.add(new Triangle(
            new Vertex(100, -100, -100, 1),
            new Vertex(-100, -100, -100, 1),
            new Vertex(-100, 100, -100, 1),
            Color.GREEN));

    tris.add(new Triangle(
            new Vertex(100, -100, -100, 1),
            new Vertex(-100, 100, -100, 1),
            new Vertex(100, 100, -100, 1),
            Color.GREEN));


// ---------- LEFT FACE (x = -100) ----------
    tris.add(new Triangle(
            new Vertex(-100, -100, -100, 1),
            new Vertex(-100, -100, 100, 1),
            new Vertex(-100, 100, 100, 1),
            Color.BLUE));

    tris.add(new Triangle(
            new Vertex(-100, -100, -100, 1),
            new Vertex(-100, 100, 100, 1),
            new Vertex(-100, 100, -100, 1),
            Color.BLUE));


// ---------- RIGHT FACE (x = +100) ----------
    tris.add(new Triangle(
            new Vertex(100, -100, 100, 1),
            new Vertex(100, -100, -100, 1),
            new Vertex(100, 100, -100, 1),
            Color.YELLOW));

    tris.add(new Triangle(
            new Vertex(100, -100, 100, 1),
            new Vertex(100, 100, -100, 1),
            new Vertex(100, 100, 100, 1),
            Color.YELLOW));


// ---------- TOP FACE (y = +100) ----------
    tris.add(new Triangle(
            new Vertex(-100, 100, 100, 1),
            new Vertex(100, 100, 100, 1),
            new Vertex(100, 100, -100, 1),
            Color.WHITE));

    tris.add(new Triangle(
            new Vertex(-100, 100, 100, 1),
            new Vertex(100, 100, -100, 1),
            new Vertex(-100, 100, -100, 1),
            Color.WHITE));


// ---------- BOTTOM FACE (y = -100) ----------
    tris.add(new Triangle(
            new Vertex(-100, -100, -100, 1),
            new Vertex(100, -100, -100, 1),
            new Vertex(100, -100, 100, 1),
            Color.PINK));

    tris.add(new Triangle(
            new Vertex(-100, -100, -100, 1),
            new Vertex(100, -100, 100, 1),
            new Vertex(-100, -100, 100, 1),
            Color.PINK));

    Mesh mesh = new Mesh(tris);

    Canvas canvas = new Canvas(mesh);

    Container container = new Container(canvas);
}