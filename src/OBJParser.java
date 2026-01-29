import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class OBJParser {
    static Random rand = new Random();

    public static Mesh parseFile(String filename){
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Triangle> triangles = new ArrayList<>();

        try (Scanner reader = new Scanner(new File(filename))) {
            while (reader.hasNextLine()) {
                String[] split = reader.nextLine().split(" ");
                switch (split[0]){
                    case "v":
                        double x = Double.parseDouble(split[1]);
                        double y = Double.parseDouble(split[2]);
                        double z = Double.parseDouble(split[3]);
                        vertices.add(new Vertex(x,y,z,1));
                        break;
                    case "f":
                        Vertex v1 = vertices.get(Integer.parseInt(split[1].split("/")[0]) - 1);
                        Vertex v2 = vertices.get(Integer.parseInt(split[2].split("/")[0]) - 1);
                        Vertex v3 = vertices.get(Integer.parseInt(split[3].split("/")[0]) - 1);
                        Color randomColor = new Color(rand.nextFloat(0.2f,1.0f), rand.nextFloat(0.2f,1.0f), rand.nextFloat(0.2f,1.0f));
                        triangles.add(new Triangle(v1,v2,v3, randomColor));
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error file not found: "+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }

        return new Mesh(triangles);
    }
}
