package recsys.pb.loader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import recsys.pb.calculator.calculate_ratings;
import recsys.pb.main.ratingsData;
import recsys.pb.main.recsys_code_pa1;
import recsys.pb.utils.sort_map;
import au.com.bytecode.opencsv.CSVReader;

public class csv_loader {
	/**
     * Data loader
     * @param strFilePath
     * @param AssocId
     * @param Simple
     * @return
     * @throws FileNotFoundException
     */
	public static String  LoadDataAssociations(String strFilePath, Integer AssocId, Boolean Simple) throws FileNotFoundException {
		
		Map<java.lang.Integer, java.lang.Double> UserCompare = new TreeMap<java.lang.Integer, java.lang.Double>();
        @SuppressWarnings("unused")
		Collection<Integer> advUserCompare = new ArrayList<Integer>();
        Collection<ratingsData> UserRatings = new ArrayList<ratingsData>();
        String output = AssocId.toString() ;
        String adVoutput = AssocId.toString() ;
        String result ="";
        Integer xbarValue = 0;

        try {
        	@SuppressWarnings("unused")
			int ii=0;
        	//Read the input file and convert into
            @SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new FileReader(strFilePath),',');
            String [] nextLine;

            while ((nextLine = reader.readNext()) != null) {
            	
            	ratingsData ratingsDataVal = new ratingsData();

                //Comparing MovieId with AssocId
                if(java.lang.Integer.parseInt(nextLine[1]) == AssocId) {
                    UserCompare.put(java.lang.Integer.parseInt(nextLine[0]), java.lang.Double.parseDouble(nextLine[2]));
                } else {
                    ratingsDataVal.setUserId(java.lang.Integer.parseInt(nextLine[0]));
                    ratingsDataVal.setMovieId(java.lang.Integer.parseInt(nextLine[1]));
                    ratingsDataVal.setRatingValue(java.lang.Double.parseDouble(nextLine[2]));
                    UserRatings.add(ratingsDataVal);
                }
            }
            
            Map<java.lang.Integer, java.lang.Integer> MovieRatings = new TreeMap<java.lang.Integer, java.lang.Integer>();
            
            //Y Values
            MovieRatings = calculate_ratings.findYvalue(UserRatings, UserCompare);
            
            //simple association
            Map<java.lang.Integer, java.lang.Double> MovieAssociations = new TreeMap<java.lang.Integer, java.lang.Double>();
            
            //simple association
            Map<java.lang.Integer, java.lang.Double> AdvMovieAssociations = new TreeMap<java.lang.Integer, java.lang.Double>();
            
            //Advanced Value
            Map<java.lang.Integer, java.lang.Integer> advMovieRatings = new TreeMap<java.lang.Integer, java.lang.Integer>();
            
            //findXBarvalue
            advMovieRatings = calculate_ratings.findXBarvalue(UserRatings, UserCompare);
            xbarValue = calculate_ratings.XBarvalue(UserRatings, UserCompare);
            
            for (Iterator<java.lang.Integer> mr = MovieRatings.keySet().iterator(); mr.hasNext();) {
            	//(x+y)/y
            	java.lang.Integer dmr = mr.next();
            	java.lang.Double Val = (double) (MovieRatings.get(dmr) + UserCompare.size())/UserCompare.size();
            	MovieAssociations.put(dmr,(Val -1));
            	Float advValue = (float) ((Val-1)/(((float)(advMovieRatings.get(dmr))/(xbarValue))));
            	AdvMovieAssociations.put(dmr,(double) advValue);
            }
            
            //Sorted - Simple
            Map<java.lang.Integer, java.lang.Double> SortedMovieAssociations = new TreeMap<java.lang.Integer, java.lang.Double>();
            SortedMovieAssociations = sort_map.sortMapByValue(MovieAssociations);
            
            int h = 0;
            for (Iterator<java.lang.Integer> smr = SortedMovieAssociations.keySet().iterator(); smr.hasNext();) {
            	h = h +1;
            	java.lang.Integer dsmr = smr.next();
            	
            	output = output + ","+ dsmr + "," + SortedMovieAssociations.get(dsmr).toString().substring(0,5);
            	
            	if (h >= recsys_code_pa1.TOP_5){
            		break;
            	}
            }
            //Sorted - Advanced
            Map<java.lang.Integer, java.lang.Double> SortedAdvMovieAssociations = new TreeMap<java.lang.Integer, java.lang.Double>();
            SortedAdvMovieAssociations = sort_map.sortMapByValue(AdvMovieAssociations);
            
            int adv = 0;
            
            for (Iterator<java.lang.Integer> asmr = SortedAdvMovieAssociations.keySet().iterator(); asmr.hasNext();) {
            	adv = adv +1;
            	java.lang.Integer adsmr = asmr.next();
            	
            	//output
            	adVoutput = adVoutput + ","+ adsmr + "," + SortedAdvMovieAssociations.get(adsmr).toString().substring(0,5);
            	
            	//Top 5
            	if (adv >= recsys_code_pa1.TOP_5){
            		break;
            	}
            }
            
            if (Simple == true) {
            	result  = adVoutput;
            } else { 
            	result = output;
            }    
        }
        
        catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        
        return result;
	}
}
