void main() {
    Dashboard dashboard = new Dashboard();
    Canvas canvas = new Canvas();
    canvas.addPoint(new Point(100, 100, 100));
    canvas.addPoint(new Point(-100, 100, 100));
    canvas.addPoint(new Point(100, -100, 100));
    canvas.addPoint(new Point(-100, -100, 100));
    dashboard.reDrawCanvas(canvas);
}