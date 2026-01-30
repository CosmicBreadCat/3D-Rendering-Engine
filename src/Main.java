void main(){
    Mesh mesh = OBJParser.parseFile("test_files/suzanne.obj");

    Vertex location = new Vertex(0,0,4,1);
    Vertex look = new Vertex(0,0,0,1);
    Vertex up = new Vertex(0,1,0,1);
    Camera camera = new Camera(location, look, up, 75, 0.1, 1000);

    Canvas canvas = new Canvas(mesh, camera);

    Container container = new Container(canvas);
}