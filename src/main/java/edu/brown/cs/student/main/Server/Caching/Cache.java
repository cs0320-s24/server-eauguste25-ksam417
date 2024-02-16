package edu.brown.cs.student.main.Server.Caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.csv.Records.LocationData;

public class Cache {

    private LoadingCache<String,List<String>> cache;

    CacheConfigurator config;

    public Cache(CacheConfigurator config, BroadbandDataSource broadbandDataSource) {
        this.config = config;

        return CacheBuilder.newBuilder()
                .maximumSize(this.config.getMaximumSize())
                .expireAfterWrite(this.config.getExpireAfterWriteMinutes(), TimeUnit.MINUTES)
                .recordStats()
                .build(
                        new CacheLoader<>() {
                            @Override
                            public Object load(Object o) throws Exception {
                                return null;
                            }

                            public List<String> load(LocationData locationData) throws DatasourceException {
                                return broadbandDataSource.getBandWidth(locationData.state(), locationData.county());
                            }
                        });

    }
}


