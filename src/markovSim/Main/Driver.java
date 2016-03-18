package markovSim.Main;
import java.io.IOException;

import markovSim.FunctionMatrixCreation.FunctionMatrixCreator;
import markovSim.FunctionMatrixCreation.SpreadSheetExtender;

public class Driver {
	private static final String funcSpreadsheet = "Project-Functions";
	private static final String funcMatrix = "functionMatrix.txt";
	private static final String logMatrix = "logFunctionMatrix.txt";
	private static final String outputPath = "test_output/";
	
	private static final int runningTime = 5000;

	public static void main(String[] args) throws IOException {
		new SpreadSheetExtender(funcSpreadsheet);
		new FunctionMatrixCreator(funcSpreadsheet + "_EXTENDED.xlsx");

		DataMatrixReader mr = new DataMatrixReader();
		final double[][] functionMatrix = mr.readFromFile(funcMatrix);
		final double[][] logFunctionMatrix = mr.readFromFile(logMatrix);

		String filePath = "TestInputs/" + args[0] + "/";

		Grid world = new Grid(filePath + "testPopulation.asc", filePath + "testTerrain.asc", filePath + "testCropland.asc", functionMatrix, logFunctionMatrix);


		RasterWriter rw = new RasterWriter();

		rw.writeRaster(outputPath + "output_start.asc", world.makeRaster(0));

		System.out.println("People at start:\t" + world.countPeople() );

		for (int i = 0; i < runningTime; i++) {
			//			System.out.println(world.getCell(2, 6).getEntry(3));
			//			for ( int j = 0; j < world.functionMatrix.length/5; j++) {
			//				System.out.println(Math.ceil(world.getCell(2, 6).getEntry(j)) + "\t\t" + Math.ceil(world.getCell(2, 7).getEntry(j)));
			//			}
			//			
			//			System.out.println("FIOHPISFA");

			//world.getCell(2, 6).printNBH();
			//System.out.println("People at step:\t" + world.countPeople() );
			
//			System.out.println(world.getCell(2, 6).getEntry(1));
//			System.out.println(world.getCell(2, 6).getEntry(6));
			//System.out.println(world.getCell(2,  6).getEntry(0));
			
			world.step();
			if (i%100 == 0 && i != 0) 
				rw.writeRaster(outputPath + "output" + i + ".asc", world.makeRaster(0));
		}
		
		rw.writeRaster(outputPath + "waterOutput.asc", world.makeRaster(3));

		System.out.println("People at end:\t" + world.countPeople() );
	}
}
