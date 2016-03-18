package markovSim.Main;
import java.io.IOException;

import markovSim.FunctionMatrixCreation.FunctionMatrixCreator;
import markovSim.FunctionMatrixCreation.SpreadSheetExtender;

public class Driver {
	private static final String funcSpreadsheet = "Project-Functions";
	private static final String funcMatrix = "functionMatrix.txt";
	private static final String logMatrix = "logFunctionMatrix.txt";

	public static void main(String[] args) throws IOException {
		SpreadSheetExtender she = new SpreadSheetExtender(funcSpreadsheet);
		FunctionMatrixCreator fmc = new FunctionMatrixCreator(funcSpreadsheet + "_EXTENDED.xlsx");
		
		DataMatrixReader mr = new DataMatrixReader();
		final double[][] functionMatrix = mr.readFromFile(funcMatrix);
		final double[][] logFunctionMatrix = mr.readFromFile(logMatrix);
		
		String filePath = "TestInputs/" + args[0] + "/";
		
		Grid world = new Grid(filePath + "testPopulation.asc", filePath + "testTerrain.asc", filePath + "testCropland.asc", functionMatrix, logFunctionMatrix);
		
		
		
		RasterWriter rw = new RasterWriter();
		
		for (int i = 0; i < 5; i++) {
			world.step();
			rw.writeRaster("output" + i + ".asc", world.makePopRaster());
		}
		
		
	}
}
