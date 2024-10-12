package parallelAbelianSandPile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

//Inner class that handles the parallel computations
class GridWorker extends RecursiveTask<Boolean> {
	int rowStart, rowEnd, colStart, colEnd;
    	int[][] grid, updateGrid;
    	static final int CUTOFF;
      
        static {
        // Determine the optimal sequential cutoff based on the number of available cores
        int numCores = Runtime.getRuntime().availableProcessors();
        CUTOFF = 65 * numCores / 4; // Adjust cutoff based on core count
    }

	GridWorker(int rowStart, int rowEnd, int colStart, int colEnd, int[][] grid, int[][] updateGrid) {
          	this.rowStart = rowStart;
        	this.rowEnd = rowEnd;
        	this.colStart = colStart;
        	this.colEnd = colEnd;
        	this.grid = grid;
        	this.updateGrid = updateGrid;
    	}

	@Override
	protected Boolean compute() {
		
      // If the task is small enough, process it sequentially
		if ((rowEnd - rowStart) < CUTOFF && (colEnd - colStart) < CUTOFF) {
		    boolean change = false;
        	    for (int i = rowStart; i < rowEnd; i++) {
                	for (int j = colStart; j < colEnd; j++) {
                        // Update grid based on the Abelian Sandpile rules
                    		updateGrid[i][j] = (grid[i][j] % 4) +
                            	(grid[i - 1][j] / 4) +
                            	(grid[i + 1][j] / 4) +
                            	(grid[i][j - 1] / 4) +
                            	(grid[i][j + 1] / 4);
                    	if (grid[i][j] != updateGrid[i][j]) {
                                change = true;
                    	}
             	        }
            	    }
           	 return change;}

      else  {
            // Split the task into two subtasks
		int mid;
		GridWorker left, right;
            	
      		if ((rowEnd - rowStart) > (colEnd - colStart)) {
      			mid = (rowEnd + rowStart) / 2;
      			left = new GridWorker(rowStart, mid, colStart, colEnd, grid, updateGrid);
      			right = new GridWorker(mid, rowEnd, colStart, colEnd, grid, updateGrid);
      		}
            
		else {
			mid = (colEnd + colStart) / 2;
			left = new GridWorker(rowStart, rowEnd, colStart, mid, grid, updateGrid);
			right = new GridWorker(rowStart, rowEnd, mid, colEnd, grid, updateGrid);
		}
      
      // Fork the left task and compute the right task in the current thread
		left.fork();
		boolean rightAns = right.compute();
		boolean leftAns = left.join();

		return rightAns || leftAns;


    }
}
}


//This class is for the grid for the Abelian Sandpile cellular automaton
public class Grid {
	private int rows, columns;
	private int [][] grid;                      //Grid 
	private int [][] updateGrid;               //Grid for next time step
    	private final ForkJoinPool pool = ForkJoinPool.commonPool();


	public Grid(int w, int h) {
		rows = w+2;              //for the "sink" border
		columns = h+2;          //for the "sink" border
		grid = new int[this.rows][this.columns];
		updateGrid=new int[this.rows][this.columns];

		//grid  initialization
		for(int i=0; i<this.rows; i++ ) {
			for( int j=0; j<this.columns; j++ ) {
				grid[i][j]=0;
				updateGrid[i][j]=0;
			}
		}
	}

	public Grid(int[][] newGrid) {
		this(newGrid.length,newGrid[0].length);   //call constructor above
      
		//don't copy over sink border
		for(int i=1; i<rows-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.grid[i][j]=newGrid[i-1][j-1];
			}
		}
		
	}
	public Grid(Grid copyGrid) {
		this(copyGrid.rows,copyGrid.columns); //call constructor above
      
		// grid  initialization
		for(int i=0; i<rows; i++ ) {
			for( int j=0; j<columns; j++ ) {
				this.grid[i][j]=copyGrid.get(i,j);
			}
		}
	}
	
	public int getRows() {
		return rows-2;             // Without the sink border
	}

	public int getColumns() {
		return columns-2;       // Without the sink border
	}


	int get(int i, int j) {
		return this.grid[i][j];
	}

	void setAll(int value) {
		// Set all cells to the given value, excluding borders
		for( int i = 1; i<rows-1; i++ ) {
			for( int j = 1; j<columns-1; j++ ) 			
				grid[i][j]=value;
			}
	}
	

	//for the next timestep - copy updateGrid into grid
	public void nextTimeStep() {
		for(int i=1; i<rows-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.grid[i][j]=updateGrid[i][j];
			}
		}
	}


	boolean update() {
      // Invoke the parallel computation and update the grid
		boolean change = pool.invoke(new GridWorker(1, rows -1,1, columns -1, grid, updateGrid));

		if (change) {
			nextTimeStep();
		}
		return change;
	}
	
	
	//display the grid in text format
	void printGrid( ) {
		int i,j;
      
		//not border is not printed
		System.out.printf("Grid:\n");
		System.out.printf("+");
      
		for( j=1; j<columns-1; j++ ) System.out.printf("  --");
		System.out.printf("+\n");
      
		for( i=1; i<rows-1; i++ ) {
			System.out.printf("|");
         
			for( j=1; j<columns-1; j++ ) {
         
				if ( grid[i][j] > 0) 
					System.out.printf("%4d", grid[i][j] );
				else
					System.out.printf("    ");
			}
         
			System.out.printf("|\n");
		}
      
		System.out.printf("+");
		for( j=1; j<columns-1; j++ ) System.out.printf("  --");
		System.out.printf("+\n\n");
	}
	
	//write grid out as an image
	void gridToImage(String fileName) throws IOException {
        BufferedImage dstImage =
                new BufferedImage(rows, columns, BufferedImage.TYPE_INT_ARGB);
        //integer values from 0 to 255.
        int a=0;
        int g=0;        //green
        int b=0;       //blue
        int r=0;      //red

		for( int i=0; i<rows; i++ ) {
			for( int j=0; j<columns; j++ ) {
			     g=0;         //green
			     b=0;        //blue
			     r=0;       //red

			
		     	    switch (grid[i][j]) {
			    case 0:
		                break;
		            case 1:
		            	g=255;
		                break;
		            case 2:
		                b=255;
		                break;
		            case 3:
		                r = 255;
		                break;
		            default:
		                break;
				
				}
		                // Set destination pixel to mean
		                // Re-assemble destination pixel.
		              int dpixel = (0xff000000)
		                		| (a << 24)
		                        | (r << 16)
		                        | (g<< 8)
		                        | b; 
		              dstImage.setRGB(i, j, dpixel); //write it out

			
			}}
		
        File dstFile = new File(fileName);
        ImageIO.write(dstImage, "png", dstFile);
	}
	
	


}
