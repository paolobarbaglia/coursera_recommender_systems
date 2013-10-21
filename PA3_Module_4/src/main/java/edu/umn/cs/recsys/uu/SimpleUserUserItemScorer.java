package edu.umn.cs.recsys.uu;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.grouplens.lenskit.basic.AbstractItemScorer;
import org.grouplens.lenskit.data.dao.ItemEventDAO;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.history.History;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.similarity.CosineVectorSimilarity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

/**
 * User-user item scorer.
 * @author Paolo Barbaglia 
 */
public class SimpleUserUserItemScorer extends AbstractItemScorer {
    private final UserEventDAO userDao;
    private final ItemEventDAO itemDao;

    @Inject
    public SimpleUserUserItemScorer(UserEventDAO udao, ItemEventDAO idao) {
        userDao = udao;
        itemDao = idao;
    }
    
	/**
	 * Calculate the predicted scores of a set of items for the input user
	 */
    @Override
    public void score(long user, @Nonnull MutableSparseVector scores) {
        SparseVector userVector = getUserRatingVector(user);
        
        for (long itemToScore : scores.keyDomain()) {
        	
        	double predictedRating = 0.0;
        	double weight = 0.0;
        	LongSet Neighbors = getTopNeighbours(user, itemToScore);
        	
        	//For each neighbor increment the predicted score
        	for (Long neighbor : Neighbors) {
        		SparseVector neighbourVector = getUserRatingVector(neighbor);
				double neighbourMeanRating = neighbourVector.mean();
				double neighbourRating = neighbourVector.get(itemToScore);
				double offsetFromMean = neighbourRating - neighbourMeanRating;
				double similarity = similarityMap.get(neighbor);
				predictedRating = predictedRating + offsetFromMean*similarity;
				weight = weight + Math.abs(similarity);
			}
        	
        	//Divide by total weight
        	predictedRating = predictedRating/weight + userVector.mean();
        	scores.set(itemToScore, predictedRating);
		}
    }
    
    //Stores the similarity of this user with every other users
    HashMap<Long,Double> similarityMap;
	/**
	 * Find cosine similarity with this user with all other users and returns a set of top 30 neighbors
	 * @param user
	 * @param itemToScore
	 * @return topNeighbours
	 */
    private LongSet getTopNeighbours(long user, long itemToScore) {
    	
    	SparseVector userVector = getUserRatingVector(user);
    	LongSet users = itemDao.getUsersForItem(itemToScore);
    	LongSet topNeighbours = new LongOpenHashSet();
    	
    	similarityMap = new HashMap<Long,Double>();
    	ValueComparator similarityComparator = new ValueComparator(similarityMap);
    	
    	//Map for sorting scores
    	TreeMap<Long, Double> sortedSimilarityMap = new TreeMap<Long, Double>(similarityComparator);
    	
    	
    	for (Long potentialNeighbour : users) {
    		if(user == potentialNeighbour)
    			continue;
			double similarity = calculateUserSimilarity(userVector, getUserRatingVector(potentialNeighbour));
			similarityMap.put(potentialNeighbour, similarity);
		}
    	sortedSimilarityMap.putAll(similarityMap);
    	int i =0;
    	
    	//Add top 30 neighbors to resultSet
    	for (Long neighbor : sortedSimilarityMap.keySet()){
    		topNeighbours.add(neighbor);
    		if(i++ == 29)
    			break;
    	}
    	return topNeighbours;
	}

    /**
     * Calculate User similarity
     * @param userVector, otherUserVector
     * @return The similarity vector 
     */
	private double calculateUserSimilarity(SparseVector userVector, SparseVector otherUserVector) {
		
		MutableSparseVector userCopy = userVector.mutableCopy();
		MutableSparseVector otherUserCopy = otherUserVector.mutableCopy();
		
		userCopy.subtract(MutableSparseVector.create(userCopy.keyDomain(), userVector.mean()));
		otherUserCopy.subtract(MutableSparseVector.create(otherUserCopy.keyDomain(), otherUserCopy.mean()));
		
		CosineVectorSimilarity similarity = new CosineVectorSimilarity();
		
		return similarity.similarity(userCopy, otherUserCopy);
	}

	/**
     * Get a user's rating vector
     * @param user The user ID
     * @return The rating vector
     */
    private SparseVector getUserRatingVector(long user) {
        UserHistory<Rating> history = userDao.getEventsForUser(user, Rating.class);
        if (history == null) {
            history = History.forUser(user);
        }
        return RatingVectorUserHistorySummarizer.makeRatingVector(history);
    }
}

//Used for sorting similarity scores
class ValueComparator implements Comparator<Long> {
    Map<Long, Double> base;
    public ValueComparator(Map<Long, Double> base) {
        this.base = base;
    }
    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(Long a, Long b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } 
    }
}
