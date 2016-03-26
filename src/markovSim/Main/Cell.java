package markovSim.Main;

import java.util.Hashtable;
import java.util.Random;

public class Cell {
	private static double randVariableThreshold = 0.7;

	private double[] state, nextState, nbhState; // This cell's state vector, next vector and the combined neighbourhood state. The latter is obtained by concatenating this cell's state with it's neighbour's 
	private Cell[] nbh; // References to this cell's neighbourhood
	private double [][] functionMatrix, logFunctionMatrix;
	private boolean isBorder;
	private boolean[] isRandom;
	private boolean[] threshold;
	private Random rnd;

	private Hashtable<Integer, double[]> noLogT;  // Stores indices of all stateVector indices that shouldn't be log(). E.g water
	private final int[] noLog = {2};


	// Initialises the instance variables, and unless it is passed null copies the values in initState to state
	public Cell(double[] initState, double[][] fMatrix, double[][] lMatrix, boolean[] threshold, boolean[] isRandom) {
		state = new double[initState.length];
		nextState = new double[initState.length];
		nbhState = new double[initState.length * 5];
		nbh = new Cell[4];
		functionMatrix = fMatrix;
		logFunctionMatrix = lMatrix;
		isBorder = false;
		this.threshold = threshold;
		this.isRandom = isRandom;
		rnd = new Random();

		if (initState != null) {
			for (int i = 0; i < initState.length; i ++) {
				state[i] = initState[i];
				if (isRandom[i]) state[i] = rnd.nextDouble(); 
			}
		}

		noLogT = new Hashtable<Integer, double[]>(noLog.length);
		for (int i = 0; i < noLog.length; i++) {
			noLogT.put(noLog[i], new double[5]);
			noLogT.get(noLog[i])[0] = state[noLog[i]];
		}
	}

	// Constructor for empty cell, e.g outside the borders
	public Cell(int length, boolean useBorders) {
		state = new double[length];
		isBorder = true;
		if (useBorders) {
			state[2] = 0;
		} else {
			state[2] = 1;
		}
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
			if (noLogT.containsKey(i % state.length)) { 
				if (nbhState[i] == 0) {
					nbhState[i] = -Double.MAX_VALUE;
				} else {
					nbhState[i] = 0;
				}
			} else if (nbhState[i] == 0) {
				nbhState[i] = 0;
			} else {
				nbhState[i] = Math.log(nbhState[i]);
			}
		}
		
		multMatrix(nbhState, logFunctionMatrix);
	
		// Antilog step
		for (int i = 0; i < nbhState.length; i++) {
			if (noLogT.containsKey(i % state.length)) { 
				nbhState[i] = noLogT.get(i % state.length)[i /state.length];
			} else if (i < state.length && threshold[i]) {
				nbhState[i] = Math.exp(nbhState[i]);
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
		// Updates the stateVector
		for (int i = 0; i < state.length; i++) {
			state[i] = nextState[i];

			if (threshold[i]) {
				state[i] = threshold(i);
			} else if (isRandom[i]) {
				state[i] = rnd.nextDouble();
			} else if (state[i] < 0 || this.isBorder) {
				state[i] = 0; // Removes population from border tiles
			}
		}
	}

	private double threshold(int index) {
		if (state[index] < randVariableThreshold) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Creates the neighbourhood state vector by writing the values of the cell's state vectors into a larger array. 
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
		return (state[i]);
	}

	public void printNBH() {
		System.out.println(nbhState[0] + "\t" + nbhState[state.length] + "\t" + nbhState[state.length*2] + "\t" + nbhState[state.length*3] + "\t" + nbhState[state.length*4]);
	}

	public void printNeighEntry(int i) {
		System.out.print("\n" + state[i] + "   ");
		for (Cell c : nbh) {
			System.out.print(c.getEntry(i) + "   ");
		}
	}
}
