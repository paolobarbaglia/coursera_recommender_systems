package recsys.pb.main;

import java.util.ArrayList;

import recsys.pb.loader.csv_loader;
import recsys.pb.writer.csv_writer;

public class recsys_code_pa1 {
	
    //Top 5
    public static final int TOP_5 = 5;
    //Path of input file
    public static final String DOC_PATH = "input/recsys_data_ratings.csv";
    public static final String OUTPUT_SIMPLE = "output/simple.csv";
    public static final String OUTPUT_ADV = "output/advanced.csv";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
        String outputResultSimple = "";
        String outputResultAdv = "";

        //Dataset for associate - Inputs
        ArrayList<Integer> Input = new ArrayList<Integer>();
        Input.add(120);
        Input.add(10020);
        Input.add(194);

        //File path
        String strFilePath = DOC_PATH;
        System.out.println("Wait...");

        try {
            int ij =0;
            for (ij=0; ij<Input.size(); ij++) {
            	
            	outputResultSimple = outputResultSimple + "\n" + csv_loader.LoadDataAssociations(strFilePath,Input.get(ij),false);
            	outputResultAdv = outputResultAdv + "\n" + csv_loader.LoadDataAssociations(strFilePath,Input.get(ij),true);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getStackTrace());
        }
        
        System.out.println(outputResultSimple);
        System.out.println(outputResultAdv);
        
        csv_writer.write(outputResultSimple, OUTPUT_SIMPLE);
        csv_writer.write(outputResultAdv, OUTPUT_ADV);

	}

}
