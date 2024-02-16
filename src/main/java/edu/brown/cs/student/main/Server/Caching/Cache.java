package edu.brown.cs.student.main.Server.Caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.List;

import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.csv.Records.LocationData;

public class Cache {

    private LoadingCache<LocationData,List<String>> cache;

    CacheConfigurator config;

    public Cache(CacheConfigurator config, BroadbandDataSource broadbandDataSource) {
        this.config = config;

       this.cache = CacheBuilder.newBuilder()
                .maximumSize(this.config.getMaximumSize())
                .expireAfterWrite(this.config.getExpireAfterWrite(), this.config.getTimeUnit())
                .recordStats()
                .build(
                        new CacheLoader<LocationData, List<String>>() {
                            public List<String> load(LocationData locationData) throws DatasourceException {
                                return broadbandDataSource.getInternetAccess(locationData.state(), locationData.county());
                            }
                        });

    }
}


