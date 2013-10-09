package recsys.pb.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class csv_writer {
	
	public static void write(String outputResult, String fileOutput) {
		try {
			@SuppressWarnings("resource")
			PrintStream output = new PrintStream(new FileOutputStream(fileOutput));
			output.print(outputResult);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Scrittura: " + fileOutput);
	}

}
