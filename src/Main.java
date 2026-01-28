import java.awt.*;

void main(){
    Mesh mesh = OBJParser.parseFile("test_files/suzanne.obj");

    Canvas canvas = new Canvas(mesh);

    Container container = new Container(canvas);
}