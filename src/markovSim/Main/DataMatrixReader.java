package markovSim.Main;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataMatrixReader {

	public double[][] readFromFile(String file) {
		double[][] matrix = new double[110][110];
		
		try {
			String line = null;
			int row = 0;
			

			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null) {
				String[] tokens = line.split("\\s+");

				for (int col = 0; col < tokens.length; col++) {
					matrix[row][col] = Double.parseDouble(tokens[col]);
				}
				
				row++;
			}   

			bufferedReader.close();         
		}
		catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + file + "'");                
		}
		catch(IOException ex) {
			System.out.println("Error reading file '" + file + "'");                  
		}


		return matrix;
	}
}
