package recsys.pb.calculator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import recsys.pb.main.ratingsData;

public class calculate_ratings {
	
    /**
    *
    * @param UserRatings
    * @param UserCompare
    * @return
    */
   public static Map<Integer,Integer> findYvalue(Collection<ratingsData> UserRatings, Map<Integer,Double> UserCompare) {

       //Map of MovieID and Rating
       Map<Integer,Integer> MovieRatings = new TreeMap<Integer, Integer>();

       try {
    	   for (Iterator<ratingsData> ii=UserRatings.iterator();ii.hasNext();)
    	   {
    		   ratingsData rd = ii.next();
    		   
    		   if (UserCompare.containsKey(rd.getUserId()))
    		   {
    			   if( MovieRatings.containsKey(rd.getMovieId()))
    			   {
    				   MovieRatings.put(rd.getMovieId(), MovieRatings.get(rd.getMovieId())+1);
    				   } 
    			   else {
    				   //First time
    				   MovieRatings.put(rd.getMovieId(),1);
    				   }
    			   }
    		   }
    	   }
       catch (Exception e)
       {
    	   System.out.println(e.getMessage());
       }
       return MovieRatings;
       
   }
   
   /**
   *
   * @param UserRatings
   * @param UserCompare
   * @return
   */
   public static Map<Integer,Integer> findXBarvalue(Collection<ratingsData> UserRatings, Map<Integer,Double> UserCompare) {
	   Integer XbarCount = 0;
	   Map<Integer,Integer> advMovieRatings = new TreeMap<Integer, Integer>();
	   Map<Integer,Integer> XbarArray = new TreeMap<Integer, Integer>();
	   
	   try {
		   for (Iterator<ratingsData> ii=UserRatings.iterator();ii.hasNext();)
		   {
			   ratingsData rd = ii.next();
			   if (!UserCompare.containsKey(rd.getUserId())) {
				   
				   if (XbarArray.containsKey(rd.getUserId()))
				   {
					   XbarCount = XbarCount + 1;
					   XbarArray.put(rd.getUserId(),XbarCount);
				   } else {
					   XbarArray.put(rd.getUserId(),XbarCount);
				   }
				   
				   if( advMovieRatings.containsKey(rd.getMovieId()))
				   {
					   advMovieRatings.put(rd.getMovieId(), advMovieRatings.get(rd.getMovieId())+1);
				   } else {
					   //First time
					   advMovieRatings.put(rd.getMovieId(),1);   
				   }
			   }
		   }
	   }
	   catch (Exception e) {
		   System.out.println(e.getMessage());   
	   }
	   
	   //Print array dimension
	   //System.out.println(XbarArray.size());
	   return advMovieRatings;
   }
   
   public static Integer XBarvalue(Collection<ratingsData> UserRatings, Map<Integer,Double> UserCompare) {
	   Integer XbarCount = 0;
	   Map<Integer,Integer> XbarArray = new TreeMap<Integer, Integer>();
	   
	   try {
		   for (Iterator<ratingsData> ii=UserRatings.iterator(); ii.hasNext();)
		   {
			   ratingsData rd = ii.next();
			   if (!UserCompare.containsKey(rd.getUserId()))
			   {
				   if (XbarArray.containsKey(rd.getUserId()))
				   {
					   XbarCount = XbarCount + 1;
					   XbarArray.put(rd.getUserId(),XbarCount);
				   } else {
					   XbarArray.put(rd.getUserId(),XbarCount);
				   }
				   
			   }
			   
		   }
		   
	   }
	   catch (Exception e) {
		   System.out.println(e.getMessage());
	   }
	   return XbarArray.size();	   
   }
}
