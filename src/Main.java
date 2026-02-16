void main(){
    Mesh mesh = OBJParser.parseFile("test_files/suzanne.obj");

    Vector4 location = new Vector4(0,0,4,1);
    Vector4 look = new Vector4(0,0,0,1);
    Vector4 up = new Vector4(0,1,0,1);
    Camera camera = new Camera(location, look, up, 75, 0.1, 1000);

    Canvas canvas = new Canvas(mesh, camera);

    Container container = new Container(canvas);
}