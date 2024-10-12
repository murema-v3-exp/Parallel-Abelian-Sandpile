import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GridMaker {

    public static void main(String[] args) {
        int[] gridSizes = {50, 100, 200, 250, 300, 350, 400, 600, 800, 1000, 1200, 2000}; 
        
        String directory = "";

        for (int size : gridSizes) {
            makeGrid(size, size, directory + "grid_" + size + "x" + size + ".csv");
        }

        System.out.println("CSV files created successfully.");
    }

    public static void makeGrid(int width, int height, String filePath) {
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the dimensions as the first line
            writer.write(width + "," + height);
            writer.newLine();

	    int gridValue = 8;

            for (int i = 0; i < height; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < width; j++) {
                    line.append(gridValue); // Random integer between 0 and 9
                    if (j < width - 1) {
                        line.append(",");
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

