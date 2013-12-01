package edu.umn.cs.recsys.ii;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.grouplens.lenskit.collections.LongUtils;
import org.grouplens.lenskit.core.Transient;
import org.grouplens.lenskit.cursors.Cursor;
import org.grouplens.lenskit.data.dao.ItemDAO;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Event;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.util.TopNScoredItemAccumulator;
import org.grouplens.lenskit.vectors.ImmutableSparseVector;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;
import org.grouplens.lenskit.vectors.similarity.CosineVectorSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleItemItemModelBuilder implements Provider<SimpleItemItemModel> {
	private final ItemDAO itemDao;
	private final UserEventDAO userEventDao;
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SimpleItemItemModelBuilder.class);;

	@Inject
	public SimpleItemItemModelBuilder(@Transient ItemDAO idao,
			@Transient UserEventDAO uedao) {
		itemDao = idao;
		userEventDao = uedao;
	}

	@Override
	public SimpleItemItemModel get() {
		// Get the transposed rating matrix
		// This gives us a map of item IDs to those items' rating vectors
		Map<Long, ImmutableSparseVector> itemVectors = getItemVectors();

		// Get all items - you might find this useful
		LongSortedSet items = LongUtils.packedSet(itemVectors.keySet());
		// Map items to vectors of item similarities
		@SuppressWarnings("unused")
		Map<Long,MutableSparseVector> itemSimilarities = new HashMap<Long, MutableSparseVector>();

		// Compute the similarities between each pair of items
		// It will need to be in a map of longs to lists of Scored IDs to store in the model
		Map<Long, List<ScoredId>> neighborhoods = new HashMap<Long, List<ScoredId>>();


		// Compute the similarities between each pair of items
		CosineVectorSimilarity cosine = new CosineVectorSimilarity();

		for(long item : items){

			// get this item ratings
			ImmutableSparseVector itemRatings = itemVectors.get(item);

			// create the accumulator for this item
			TopNScoredItemAccumulator accumulator = new TopNScoredItemAccumulator(items.size() - 1);

			for(long neighbor : items){

				// skip itself
				if(item == neighbor) continue;

				ImmutableSparseVector neighRatings = itemVectors.get(neighbor);

				// cosine similarity
				double similarity = cosine.similarity(itemRatings, neighRatings);

				//accumulate positive similarities
				if(similarity >= 0.0){
					accumulator.put(neighbor, similarity);
				}
			}

			//get the final list of sorted neighbors
			List<ScoredId> similarities = accumulator.finish();

			// update the map of similarity
			neighborhoods.put(item, similarities);
		}

		// It will need to be in a map of longs to lists of Scored IDs to store in the model
		return new SimpleItemItemModel(neighborhoods);
	}

	/**
	 * Load the data into memory, indexed by item.
	 * @return A map from item IDs to item rating vectors. Each vector contains users' ratings for
	 * the item, keyed by user ID.
	 */
	public Map<Long,ImmutableSparseVector> getItemVectors() {
		// set up storage for building each item's rating vector
		LongSet items = itemDao.getItemIds();
		// map items to maps from users to ratings
		Map<Long,Map<Long,Double>> itemData = new HashMap<Long, Map<Long, Double>>();
		for (long item: items) {
			itemData.put(item, new HashMap<Long, Double>());
		}
		// itemData should now contain a map to accumulate the ratings of each item

		// stream over all user events
		Cursor<UserHistory<Event>> stream = userEventDao.streamEventsByUser();
		try {
			for (UserHistory<Event> evt: stream) {
				MutableSparseVector vector = RatingVectorUserHistorySummarizer.makeRatingVector(evt).mutableCopy();
				// vector is now the user's rating vector
				// Normalize this vector
				vector.add(-vector.mean());
				// Store the ratings in the item data
				for (VectorEntry vectorEntry : vector.fast(VectorEntry.State.EITHER)) {
					long itemId = vectorEntry.getKey();
					double rating = vectorEntry.getValue();
					long userId = evt.getUserId();
					itemData.get(itemId).put(userId, rating);
				}
			}
		} finally {
			stream.close();
		}

		// This loop converts our temporary item storage to a map of item vectors
		Map<Long,ImmutableSparseVector> itemVectors = new HashMap<Long, ImmutableSparseVector>();
		for (Map.Entry<Long,Map<Long,Double>> entry: itemData.entrySet()) {
			MutableSparseVector vec = MutableSparseVector.create(entry.getValue());
			itemVectors.put(entry.getKey(), vec.immutable());
		}
		return itemVectors;
	}
}
