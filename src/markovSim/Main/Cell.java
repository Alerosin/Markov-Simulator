package markovSim.Main;

import java.util.HashSet;

public class Cell {
	private double[] state, nextState, nbhState; // This cell's state vector, next vector and the combined neighbourhood state. The latter is obtained by concatenating this cell's state with it's neighbour's 
	private Cell[] nbh; // References to this cell's neighbourhood
	private double [][] functionMatrix, logFunctionMatrix;
	
	private HashSet<Integer> noLogS;
	private final int[] noLog = {2, 3};


	// Initialises the instance variables, and unless it is passed null copies the values in initState to state
	public Cell(double[] initState, double[][] fMatrix, double[][] lMatrix) {
		state = new double[initState.length];
		nextState = new double[initState.length];
		nbhState = new double[initState.length * 5];
		nbh = new Cell[4];
		functionMatrix = fMatrix;
		logFunctionMatrix = lMatrix;

		if (initState != null) {
			for (int i = 0; i < initState.length; i ++) {
				state[i] = initState[i];
			}
		}
		
		noLogS = new HashSet<Integer>(noLog.length);
		for (int i = 0; i < noLog.length; i++) {
			//for ( int j = 0; j < 5; j++) {
				noLogS.add(noLog[i]);
			//}
		}
	
	}
	
	public Cell(int length) {
		state = new double[length];
	}


	/*
	 * INITIALISATION FUNCTIONS                                                                     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */ 

	public double[] getStateV() {
		return state;
	}

	/**
	 * Sets this cells neighbour
	 * @param c 
	 * @param pos (0 = north, 1 = south, 2 = east, 3 = west)
	 */
	public void setNeigh(Cell c, int pos) {
		nbh[pos] = c;
	}

	
	
	/*
	 *  SIMULATION RUNNING FUNCTIONS                                                                 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	/**
	 * Calculates the next state of the cell by:
	 * 	- Creating the nbhState vector through concatenate()
	 * 	- Taking the log of the nbhState (if a variable is = 0, it sets the result to be 0)
	 * 	- Multiplying the result with the logFunctionMatrix
	 * 	- Taking the antilog of the result (If it takes the antilog of 0, it sets the result to 0)
	 * 	- Multiplying the result with the functionMatrix
	 * 	- Extracting the result from nbhState into nextState using extract()
	 * 
	 */
	public void calcStep() {
		concatenate();
		
		// Log step
		for (int i = 0; i < nbhState.length; i++) {
			if (noLogS.contains(i)) {
				if (nbhState[i] == 0) {
					nbhState[i] -= Double.MAX_VALUE;
				} else {
					nbhState[i] = 0.000001;
				}
			} else if (nbhState[i] <= 0) {
				nbhState[i] = 0;
			} else {
				nbhState[i] = Math.log(nbhState[i]);
			}
		}
		
		multMatrix(nbhState, logFunctionMatrix);
		
		// Antilog step
		for (int i = 0; i < nbhState.length; i++) {
			if (noLogS.contains(i)) {
				if (nbhState[i] == 0.000001) {
					nbhState[i] = 1;
				} else {
					continue;
				}
			} else if (nbhState[i] <= 0) {
				nbhState[i] = 0;
			} else {
				nbhState[i] = Math.exp(nbhState[i]);
			}
		}
		
		multMatrix(nbhState, functionMatrix);
		
		extract();
		
	}

	/**
	 * Additional step to update the state vectors and provide stochastic functionality. 
	 * Iterates through state and at variables which are considered stochastic it checks if their value is above a threshold. If so, the outcome occurs (eg. a technology is discovered/spread to this tile)
	 */
	public void thresholdStep() {
		for (int i = 0; i < state.length; i++) {
			state[i] = nextState[i];
		}
		
		threshold();
	}
	
	private void threshold() {
		for (int i = 0; i < state.length; i++) {
			if (state[i] < 0 || Double.isNaN(state[i])) state[i] = 0;
		}
	}
	
	/**
	 * Creates the neighbourhoods state vector by writing the values of the cell's state vectors into a larger array. 
	 * ie. [[vector1][vector2][vector3]...[vectorN]]
	 */
	private void concatenate() {
		for (int i = 0; i < state.length; i++) {
			nbhState[i] = state[i];
			nbhState[i + (state.length)] = nbh[0].getStateV()[i];
			nbhState[i + (state.length * 2)] = nbh[1].getStateV()[i];
			nbhState[i + (state.length * 3)] = nbh[2].getStateV()[i];
			nbhState[i + (state.length * 4)] = nbh[3].getStateV()[i];
		}
		//System.out.println(state.length);
		
		//System.out.println(nbhState[0] + "\t" + nbhState[state.length] + "\t" + nbhState[state.length*2] + "\t" + nbhState[state.length*3] + "\t" + nbhState[state.length*4]);
	}
	
	/**
	 * Extracts the first state vector from the nbhState, and puts it into nextState
	 */
	private void extract() {
		for (int i = 0; i < state.length; i++) {
			nextState[i] = nbhState[i];
		}
	}
	
	private void multMatrix(double[] vector, double[][] matrix) {
		int rows = matrix.length;
	    int columns = matrix[0].length;

	    double[] result = new double[rows];

	    for (int row = 0; row < rows; row++) {
	        double sum = 0;
	        for (int column = 0; column < columns; column++) {
	            sum += matrix[row][column] * vector[column];
	        }
	        result[row] = sum;
	    }
	    
	    for (int i = 0; i < result.length; i++) 
	    	nbhState[i] = result[i];
	}
	
	
	/*
	 *  ERROR CHECKING/ OUTPUT FUNCTIONS                                                                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

		
	public double getEntry(int i) {
		return Math.round(state[i]);
	}
	
	public void printNBH() {
		System.out.println(nbhState[0] + "\t" + nbhState[state.length] + "\t" + nbhState[state.length*2] + "\t" + nbhState[state.length*3] + "\t" + nbhState[state.length*4]);
	}
}