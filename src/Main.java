private final int FPS = 60;

void main() throws InterruptedException {
    Painter painter = new Painter();
    Canvas canvas = new Canvas();

    float dt =  (float) 1 / FPS;
    float dz = 0;

    canvas.addPoint(new Point(100, 100, 100));
    canvas.addPoint(new Point(-100, 100, 100));
    canvas.addPoint(new Point(100, -100, 100));
    canvas.addPoint(new Point(-100, -100, 100));

    // main loop
    while (true){
        dz += 1*dt;
        for(Point point: canvas.getPoints()){
            point.setZ(point.getZ() + dz);
        }
        painter.drawCanvas(canvas);
        Thread.sleep(1000/FPS);
    }
}