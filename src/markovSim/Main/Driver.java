package markovSim.Main;
import java.io.IOException;

import markovSim.FunctionMatrixCreation.FunctionMatrixCreator;
import markovSim.FunctionMatrixCreation.SpreadSheetExtender;

public class Driver {
	private static final String funcSpreadsheet = "Project-Functions";
	private static final String funcMatrix = "functionMatrix.txt";
	private static final String logMatrix = "logFunctionMatrix.txt";
	private static final String outputPath = "proto_test_result_1/";

	private static final int runningTime = 5;

	public static void main(String[] args) throws IOException {
		new SpreadSheetExtender(funcSpreadsheet);
		new FunctionMatrixCreator(funcSpreadsheet + "_EXTENDED.xlsx");

		DataMatrixReader mr = new DataMatrixReader();
		final double[][] functionMatrix = mr.readFromFile(funcMatrix);
		final double[][] logFunctionMatrix = mr.readFromFile(logMatrix);

		String filePath = "TestInputs/" + args[0] + "/";

		long startTime = System.currentTimeMillis();

		Grid world = new Grid(filePath + "testPopulation.asc", filePath + "testTerrain.asc", filePath + "testCropland.asc", functionMatrix, logFunctionMatrix);


		RasterWriter rw = new RasterWriter();

		rw.writeRaster(outputPath + "output_start.asc", world.makeRaster(0));

		System.out.println("People at start:\t" + world.countPeople() );

		long endTime = System.currentTimeMillis();
		long setUpTime = endTime - startTime;
		System.out.println("Setup time: " + setUpTime/1000.0);

		startTime = System.currentTimeMillis();
		
		int outputRes = 1;
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
			System.out.println("Step " + i + " complete");
			
			if (i%outputRes == 0 && i != 0) {
				rw.writeRaster(outputPath + "POOPoutput" + i + ".asc", world.makeRaster(0));
				System.out.println("Percent Completed: " + (i * 100 / runningTime) + "%");
			}
		}

		rw.writeRaster(outputPath + "outputEND.asc", world.makeRaster(0));
		rw.writeRaster(outputPath + "water.asc", world.makeRaster(2));

		System.out.println("People at end:\t" + world.countPeople() );

		endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;

		System.out.println("Runtime in seconds: " + endTime/1000.0);
		
	}
}