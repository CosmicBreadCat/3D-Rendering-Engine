import java.util.ArrayList;

public class Mesh {
    ArrayList<Triangle> tris = new ArrayList<>();

    public Mesh(ArrayList<Triangle> tris) {
        this.tris = tris;
    }

    public ArrayList<Triangle> getTris() {
        return tris;
    }

    public void addTri(Triangle tri){
        if (!tris.contains(tri)){
            tris.add(tri);
        }
    }

    public void removeTri(Triangle tri){
        tris.remove(tri);
    }

    public void setTris(ArrayList<Triangle> tris) {
        this.tris = tris;
    }
}
