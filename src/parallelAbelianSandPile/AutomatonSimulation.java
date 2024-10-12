//Copyright M.M.Kuttel 2024 CSC2002S, UCT
package parallelAbelianSandPile;

import java.io.*;

class AutomatonSimulation {
	static final boolean DEBUG = false;// for debugging output, off

	static long startTime = 0; // Start time for timing the simulation
	static long endTime = 0; // End time for timing the simulation

	// timers - note milliseconds
	private static void tick() { // start timing
		startTime = System.currentTimeMillis();
	}

	private static void tock() { // end timing
		endTime = System.currentTimeMillis();
	}

	// input is via a CSV file
	public static int[][] readArrayFromCSV(String filePath) {
		int[][] array = null;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line = br.readLine();
			if (line != null) {
				String[] dimensions = line.split(",");
				int width = Integer.parseInt(dimensions[0]);
				int height = Integer.parseInt(dimensions[1]);
				System.out.printf("Rows: %d, Columns: %d\n", width, height);

				array = new int[height][width];
				int rowIndex = 0;

				while ((line = br.readLine()) != null && rowIndex < height) {
					String[] values = line.split(",");
					for (int colIndex = 0; colIndex < width; colIndex++) {
						array[rowIndex][colIndex] = Integer.parseInt(values[colIndex]);
					}
					rowIndex++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return array;
	}

	public static void main(String[] args) throws IOException {

		Grid simulationGrid; // the cellular automaton grid

		if (args.length != 2) { // input is the name of the input and output files
			System.out.println("Incorrect number of command line arguments provided.");
			System.exit(0);
		}

		// Read command line arguments for input and output file names
		String inputFileName = args[0]; // input file name
		String outputFileName = args[1]; // output file name

		// Read from input .csv file
		simulationGrid = new Grid(readArrayFromCSV(inputFileName));

		int counter = 0;
		tick(); // start timer

		// Print the initial grid configuration if debugging is enabled
		if (DEBUG) {
			System.out.printf("starting config: %d \n", counter);
			simulationGrid.printGrid();
		}

		// Run the simulation until the grid reaches a stable state
		while (simulationGrid.update()) {// run until no change
			if (DEBUG)
				simulationGrid.printGrid(); // Print grid at each step if debugging
			counter++; // Increment step counter
		}
		tock(); // end timer

		// Output results
		System.out.println("Simulation complete, writing image...");
		simulationGrid.gridToImage(outputFileName); // write grid as an image

		// Print the number of steps and the total computation time
		System.out.printf("Number of steps to stable state: %d \n", counter);
		System.out.printf("Time: %d ms\n", endTime - startTime); /* Total computation time */
	}
}
