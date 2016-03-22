package markovSim.Main;
import java.io.IOException;

public class Grid {

	private int width, height;
	private Cell[][] cells;
	private double[][] popGrid, terrGrid, cropGrid;
	public double[][] functionMatrix, logFunctionMatrix;
	
	private final double xll;
	private final double yll;
	

	public Grid(String popGridS, String tGridS, String cGridS, double[][] functionMatrix, double[][] logMatrix) throws IOException {
		RasterReader rt = new RasterReader();

		popGrid = rt.readRaster(popGridS).getData();
		terrGrid = rt.readRaster(tGridS).getData();
		cropGrid = rt.readRaster(cGridS).getData();
		
		xll = rt.readRaster(cGridS).getXll();
		yll = rt.readRaster(cGridS).getYll();
		
		height = popGrid.length;
		width = popGrid[0].length;
		this.functionMatrix = functionMatrix;
		this.logFunctionMatrix = logMatrix;
		this.initCells();
	}


	/**
	 * Initialises all cells in 2 steps:
	 * 	- First it initialises all the cells using data from the input rasters
	 * 	- Then it sets each cell's neighbours
	 */
	private void initCells() {
		this.cells = new Cell[height][width];
		double[] stateVector = new double[functionMatrix.length/5];

		// Initialise cells
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				stateVector[0] = popGrid[i][j];
				stateVector[2] = terrGrid[i][j];
				if (stateVector[2] == 0 || stateVector[2] == -9999) {
					stateVector[3] = 0;
				} else {
					stateVector[3] = 1;
				}
				stateVector[11] = cropGrid[i][j];
				cells[i][j] = new Cell(stateVector, functionMatrix, logFunctionMatrix);
			}
		}

		// Set each cell's neighbours (to a blank cell if they're outside 
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// North neighbour
				if (i == 0) {
					cells[i][j].setNeigh(new Cell(stateVector.length), 0);
				} else {
					cells[i][j].setNeigh(cells[i-1][j], 0);
				}
				// South Neighbour
				if (i == (height-1)) {
					cells[i][j].setNeigh(new Cell(stateVector.length), 1);
				} else {
					cells[i][j].setNeigh(cells[i+1][j], 1);
				}
				// East Neighbour
				if (j == (width -1)) {
					cells[i][j].setNeigh(new Cell(stateVector.length), 2);
				} else {
					cells[i][j].setNeigh(cells[i][j+1], 2);
				}
				// West Neighbour
				if (j == 0) {
					cells[i][j].setNeigh(new Cell(stateVector.length), 3);
				} else {
					cells[i][j].setNeigh(cells[i][j-1], 3);
				}
			}
		}
	}

	public void step() {
		for (Cell[] row : cells) {
			for (Cell c: row) {
				c.calcStep();
			}
		}
		for (Cell[] row : cells) {
			for (Cell c: row) {
				c.thresholdStep();
			}
		}
	}

	

	public Cell getCell(int x, int y) {
		return cells[x][y];
	}
	
	public Raster makeRaster(int x) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				popGrid[i][j] = (int)(cells[i][j].getEntry(x));
			}
		}
		return makeRaster(popGrid);
	}
	
	public Raster makeRaster(double[][] data) {
		Raster r = new Raster(data, 0.0833333, xll, yll);
		return r;
	}
	
	public int countPeople() {
		int sum = 0;
		for (Cell[] row : cells) {
			for (Cell c : row) {
				sum += c.getEntry(0);
			}
		}
		return sum;
	}
}
