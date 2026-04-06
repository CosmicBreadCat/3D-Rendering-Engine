import io.OBJParser;
import lighting.DirectionalLight;
import math.Vector4;
import scene.Camera;
import scene.Mesh;
import ui.Canvas;
import ui.Container;

void main(){
    Mesh mesh = OBJParser.parseFile("test_files/suzanne.obj");

    Vector4 locationC = new Vector4(0,0,4,1);
    Vector4 lookC = new Vector4(0,0,0,1);
    Vector4 upC = new Vector4(0,1,0,1);
    Camera camera = new Camera(locationC, lookC, upC, 75, 0.1, 1000);

    Vector4 locationL = new Vector4(0,1,0,1);
    Vector4 lookL = new Vector4(0,0,0,1);
    Vector4 upL = new Vector4(1,0,0,1);
    DirectionalLight light = new DirectionalLight(locationL, lookL, upL, 0.1, 50, 5);

    Canvas canvas = new Canvas(mesh, camera, light);

    Container container = new Container(canvas);
}