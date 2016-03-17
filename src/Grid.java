import java.io.IOException;

public class Grid {

	private int width, height;
	private Cell[][] cells;
	private double[][] popGrid, terrGrid, cropGrid;
	public double[][] functionMatrix, logFunctionMatrix;

	public Grid(String popGridS, String tGridS, String cGridS, double[][] functionMatrix, double[][] logMatrix) throws IOException {
		RasterReader rt = new RasterReader();

		popGrid = rt.readRaster(popGridS).getData();
		terrGrid = rt.readRaster(tGridS).getData();
		cropGrid = rt.readRaster(cGridS).getData();
		
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
				stateVector[19] = terrGrid[i][j];
				if (stateVector[19] == 0) {
					stateVector[20] = 0;
				} else {
					stateVector[20] = 1;
				}
				stateVector[21] = cropGrid[i][j];
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

	
	
	
	
	
	
	
	
	public Raster makePopRaster() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				popGrid[i][j] = Math.ceil(cells[i][j].getPop());
			}
		}
		return makeRaster(popGrid);
	}
	
	public Raster makeRaster(double[][] data) {
		Raster r = new Raster(data, 0.0833333, -180, -90);
		return r;
	}
}
