package edu.brown.cs.student.main.datasource.broadband;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import edu.brown.cs.student.main.datasource.DatasourceException;
import java.util.HashMap;

/** Proxy wrapper for a BroadBandDatasource that caches its response for efficiency. */
public class CachingBroadBandDataSource implements BroadBandDatasource {
  /** The original datasource to wrap and invoke if needed. */
  private final BroadBandDatasource original;
  /** The cache, which maps inputted locations to the resulting BroadBandData. */
  private final Cache<Location, BroadBandData> cache;

  /**
   * Constructor for the CachingBroadBandDataSource object.
   *
   * @param original the datasource to act as a caching proxy for
   * @param maxSize the maximum size of the cache
   */
  public CachingBroadBandDataSource(BroadBandDatasource original, int maxSize) {
    this.original = original;
    this.cache = CacheBuilder.newBuilder().maximumSize(maxSize).build();
  }

  /**
   * Method to get the broadband for a state and county.
   *
   * @param location record object containing names of the state and county to retrieve data for
   * @return the broadband data retrieved, as a record object
   * @throws DatasourceException if there is an issue retrieving data for this state and county
   */
  @Override
  public BroadBandData getBroadBand(Location location) throws DatasourceException {
    // Initialize result object to return
    BroadBandData result;

    // Get the location from the cache data
    BroadBandData cachedData = cache.getIfPresent(location);

    // If it was there (cached) set it to the result, otherwise get it manually
    if (cachedData != null) {
      result = cachedData;
    } else {
      result = original.getBroadBand(location);
      cache.put(location, result);
    }
    return result;
  }

  /**
   * Method which peeks at the current cache.
   *
   * @return a HashMap representation of the current cache.
   */
  public HashMap<Location, BroadBandData> peekCache() {
    return new HashMap<>(this.cache.asMap());
  }
}
