package edu.brown.cs.student.main.Server;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

// Responsible for holding the data

// Loading method(takes in the loadhandler result) fill the map with the data

// Get data method

import edu.brown.cs.student.main.Server.Handlers.LoadHandler;

import java.util.List;


public class Proxy {
    //private final LoadHandler loadHandler;

    private final LoadingCache<String, List<String>> cache;

    public Proxy(CacheLoader<String, List<String>> loader, CacheConfiguration config) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(config.getMaximumSize())
                .expireAfterWrite(config.getExpireAfterWriteSeconds(), TimeUnit.SECONDS)
                .build(loader);
    }






}
