package recsys.pb.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class sort_map {
	
    /**
     * Sorting
     * @param map
     * @param <Integer>
     * @param <Double>
     * @return
     */
    @SuppressWarnings("hiding")
	public static<Integer, Double extends Comparable<Double>> Map<Integer, Double> sortMapByValue(Map<Integer, Double> map) {
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(
                map.entrySet());
        Collections.sort(list,
                new Comparator<Map.Entry<Integer, Double>>() {
                    public int compare(Map.Entry<Integer, Double> o1,
                                       Map.Entry<Integer, Double> o2) {
                        return (o2.getValue().compareTo(o1.getValue()));
                    }
                });

        Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
        for (Iterator<Map.Entry<Integer, Double>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Integer, Double> entry = it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;

    }

}
