package edu.brown.cs.student.main.server.Caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.CSV.Records.LocationData;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import java.util.List;

public class Cache {
  private LoadingCache<LocationData, List<String>> cache;

  BroadbandDataSource broadbandDataSource;

  CacheConfigurator config;

  // Constructor for the Cache class.
  // It initializes the cache with configurations and a data source for broadband data.
  public Cache(CacheConfigurator config, BroadbandDataSource broadbandDataSource) {
    // Assigns the provided broadband data source to a class variable for later use.
    this.broadbandDataSource = broadbandDataSource;
    // Assigns the provided cache configuration object to a class variable.
    this.config = config;
    // Initializes the cache with specific configurations using the CacheBuilder from Guava (or a
    // similar library).
    this.cache =
        CacheBuilder.newBuilder()
            // Sets the maximum number of entries the cache can hold, as specified by the cache
            // configuration.
            .maximumSize(this.config.getMaximumSize())
            // Sets the expiration time for each entry after its creation, using values from the
            // cache configuration.
            .expireAfterWrite(this.config.getExpireAfterWrite(), this.config.getTimeUnit())
            // Enables the recording of cache statistics, such as hit rate, average load penalty,
            // etc.
            .recordStats()
            // Builds the cache with a custom CacheLoader defined to load new values.
            .build(
                new CacheLoader<LocationData, List<String>>() {
                  // Defines how to load data into the cache when it's not present.
                  // This method is called automatically by the cache when a requested entry is
                  // missing.
                  @Override
                  public List<String> load(LocationData locationData) throws DatasourceException {
                    // Fetches internet access information for a given location from the broadband
                    // data source
                    // and returns it for caching. This method is invoked by the cache when an entry
                    // is not found.
                    return broadbandDataSource.getInternetAccess(
                        locationData.state(), locationData.county());
                  }
                });
  }

  // Define a function that takes a LocationData object as a parameter and returns a list of
  // strings.
  // It can throw a DatasourceException if any issues arise during its execution.
  public List<String> inCache(LocationData locationData) throws DatasourceException {

    try {
      // Attempt to retrieve a result from a cache using the provided locationData as the key.
      List<String> result = this.cache.get(locationData);
      // If successful, return the result from the cache.
      return result;
    } catch (Exception e) {
      // If there's an exception (e.g., cache miss, or any other issue), print the error message to
      // standard error.
      System.err.println(e.getMessage());
    }

    // If the data was not found in the cache (or if an exception was caught),
    // attempt to retrieve the internet access information from a broadbandDataSource
    // using the county and state information from the provided locationData.
    return this.broadbandDataSource.getInternetAccess(locationData.county(), locationData.state());
  }
}
