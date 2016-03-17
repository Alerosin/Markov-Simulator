import java.io.IOException;

public class Driver {
	

	public static void main(String[] args) throws IOException {
		matrixReader mr = new matrixReader();
		final double[][] functionMatrix = mr.readFromFile("functionMatrix.txt");
		final double[][] logFunctionMatrix = mr.readFromFile("logFunctionMatrix.txt");
		
		String filePath = "TestInputs\\" + args[0] + "\\";
		
		Grid world = new Grid(filePath + "testPopulation.asc", filePath + "testTerrain.asc", filePath + "testCropland.asc", functionMatrix, logFunctionMatrix);
		
		
		
		RasterWriter rw = new RasterWriter();
		
		for (int i = 0; i < 5; i++) {
			world.step();
			rw.writeRaster("output" + i + ".asc", world.makePopRaster());
		}
		
		
	}
}
