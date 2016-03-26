package markovSim.Main;
import java.io.IOException;

import markovSim.FunctionMatrixCreation.FunctionMatrixCreator;
import markovSim.FunctionMatrixCreation.SpreadSheetExtender;

public class Driver {
	private static final String funcSpreadsheet = "Project-Functions";
	private static final String funcMatrix = "functionMatrix.txt";
	private static final String logMatrix = "logFunctionMatrix.txt";
	private static final String outputPath = "proto_test_result_1/";

	private static final int runningTime = 200;
	private static final int outputRes = 10;

	public static void main(String[] args) throws IOException {
		new SpreadSheetExtender(funcSpreadsheet);
		FunctionMatrixCreator fmc = new FunctionMatrixCreator(funcSpreadsheet + "_EXTENDED.xlsx");

		
		DataMatrixReader mr = new DataMatrixReader();
		final double[][] functionMatrix = mr.readFromFile(funcMatrix);
		final double[][] logFunctionMatrix = mr.readFromFile(logMatrix);

		String filePath = "TestInputs/" + args[0] + "/";

		long startTime = System.currentTimeMillis();


		Grid world = new Grid(filePath + "testPopulation.asc", filePath + "testTerrain.asc", filePath + "testCropland.asc", functionMatrix, logFunctionMatrix, fmc.threshold, fmc.isRandom, false);


		RasterWriter rw = new RasterWriter();
		rw.writeRaster(outputPath + "output_start.asc", world.makeRaster(0));

		System.out.println("People at start:\t" + world.countPeople() );

		long endTime = System.currentTimeMillis();
		long setUpTime = endTime - startTime;
		System.out.printf("Set up time in seconds: %.2fs\n", setUpTime/1000.0);

		startTime = System.currentTimeMillis();

		for (int i = 0; i < runningTime; i++) {
			world.step();
			//			System.out.println("Step " + i + " complete");

			System.out.println(world.getCell(2, 6).getEntry(0));
			if (i%outputRes == 0 && i != 0) {
				rw.writeRaster(outputPath + "output" + i + ".asc", world.makeRaster(0));
				rw.writeRaster(outputPath + "t1" + i + ".asc", world.makeRaster(14));
				rw.writeRaster(outputPath + "t2" + i + ".asc", world.makeRaster(19));
				rw.writeRaster(outputPath + "Y" + i  + ".asc", world.makeRaster(13));
				System.out.println("Percent Completed:\t" + (i * 100.0 / runningTime) + "%");
			}
		}
		System.out.println("Percent Completed:\t100%\n");

		rw.writeRaster(outputPath + "outputEND.asc", world.makeRaster(0));
		rw.writeRaster(outputPath + "t1.asc", world.makeRaster(14));
		rw.writeRaster(outputPath + "t2.asc", world.makeRaster(19));
		rw.writeRaster(outputPath + "Y" + ".asc", world.makeRaster(13));
		rw.writeRaster(outputPath + "rand.asc", world.makeRaster(24));

		System.out.printf("People at end:\t%.2f\n", world.countPeople() );

		endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		System.out.printf("Runtime in seconds: %.2fs", runTime/1000.0);
	}
}
