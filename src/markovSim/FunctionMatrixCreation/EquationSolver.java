package markovSim.FunctionMatrixCreation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


/**
 * Split equation into String[] of tokens
 * Keep track of which variable is supposed to be 1 and which is 0
 * Parse through tokens and replace all String entries with 0 or 1 (can tell the operators and variables apart using symbols HashMap)
 * @author 2024351h
 *
 */
public class EquationSolver {
	private HashSet<String> ops;
	private FunctionMatrixCreator fmc;

	public EquationSolver(FunctionMatrixCreator fmc) {
		ops = new HashSet<String>();
		ops.add("+");
		ops.add("-");
		ops.add("/");
		ops.add("*");

		this.fmc = fmc;
	}


	/**
	 * 
	 * @param rowNum Which row of the matrix is to be calculated
	 * @return An array of 2 arrays. The 2 arrays being the rows for functionMatrix and logMatrix, respectively.
	 */
	public double[][] getMatrixRow(int rowNum) {
		// for each entry in fmc.funcEqs, evaluate that expression and put into double[0][i] or double[1][i]
		double[][] matrixRows = new double[2][fmc.funcEqs.length];

		for (int i = 0; i < fmc.funcEqs.length; i++) {
			matrixRows[0][i] = evaluateEquation(i, fmc.funcEqs[rowNum]);
			matrixRows[1][i] = evaluateEquation(i, fmc.logEqs[rowNum]);
		}


		return matrixRows;
	}

	/**
	 * Parses an equation in RPN format, replacing the variables which aren't the symbol with index rowNum with 0.
	 * @param rowNum which row of the matrix is to be calculated
	 * @param equation An equation String in RPN notation that is to be parsed
	 * @return
	 */
	private double evaluateEquation(int rowNum, String equation) {
		String beingEvaluated = getSymbolFromIndex(rowNum);
		Stack<Double> stack = new Stack<Double>();

		for (String token: equation.split("\\s")) {
			Double tokenNum = null;
			try {
				tokenNum = Double.parseDouble(token);
			} catch(NumberFormatException e){}
			
			
			if(tokenNum != null){
				stack.push(Double.parseDouble(token + ""));
			} else if (token.equals(beingEvaluated)) { // Ran into the variable whos' row we are calculating, replace it's value with 1
				stack.push(1.0);
			} else if (fmc.symbols.containsKey(token)) { // Ran into some other variable, replace with 0
				stack.push(0.0); 
			} else if (token.equals("*")) {  // Evaluating operands
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(firstOperand * secondOperand);
			} else if (token.equals("/")) {
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(firstOperand / secondOperand);
			} else if (token.equals("-")) {
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(firstOperand - secondOperand);
			} else if (token.equals("+")) {
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(firstOperand + secondOperand);
			} else{
				System.out.println("\nerror: " + token);
			}
		}
		return stack.pop();
	}

	/**
	 * Utility function to get a symbol String from it's index
	 * @param index
	 * @return
	 */
	private String getSymbolFromIndex(int index)  {

		for (String s : fmc.symbols.keySet()) {
			if (fmc.symbols.get(s) == index) return s;
		}

		try {
			throw new Exception("Couldn't find a symbol with index " + index);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} 
		return null;
	}


}
