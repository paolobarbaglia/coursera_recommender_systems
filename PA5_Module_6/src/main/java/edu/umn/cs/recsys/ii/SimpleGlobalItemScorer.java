package edu.umn.cs.recsys.ii;

import org.grouplens.lenskit.basic.AbstractGlobalItemScorer;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global item scorer to find similar items.
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleGlobalItemScorer extends AbstractGlobalItemScorer {
	private final SimpleItemItemModel model;

	@Inject
	public SimpleGlobalItemScorer(SimpleItemItemModel mod) {
		model = mod;
	}

	/**
	 * Score items with respect to a set of reference items.
	 * @param items The reference items.
	 * @param scores The score vector. Its domain is the items to be scored, and the scores should
	 *               be stored into this vector.
	 */
	@Override
	public void globalScore(@Nonnull Collection<Long> items, @Nonnull MutableSparseVector scores) {
		scores.fill(0);
		// score items in the domain of scores
		for (VectorEntry e: scores.fast(VectorEntry.State.EITHER)) {
			// each item's score is the sum of its similarity to each item in items, if they are
			// neighbors in the model.
			long itemId = e.getKey();
			
			// getting neighbors
			List<ScoredId> neighbors = model.getNeighbors(itemId);
			Map<Long, Double> neighMap = new HashMap<Long, Double>();
			for (ScoredId scoredId : neighbors) {
				neighMap.put(scoredId.getId(), scoredId.getScore());
			}
			
			// scoring similarity
			double score = 0.0;
			for(Long basketItem: items){
				Double similarity = 0.0;
				if(neighMap.containsKey(basketItem)) similarity = neighMap.get(basketItem);
				score += similarity;
			}
			
			// asserting score
			scores.set(e, score);
		}
	}
}
