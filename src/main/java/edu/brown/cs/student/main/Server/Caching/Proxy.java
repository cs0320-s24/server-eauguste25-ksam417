package edu.brown.cs.student.main.Server.Caching;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.security.Key;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

// Responsible for holding the data

// Loading method(takes in the loadhandler result) fill the map with the data

// Get data method

import com.sun.jdi.Value;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.csv.ACSData;
import edu.brown.cs.student.main.csv.LocationData;

import javax.xml.stream.Location;
import java.util.List;

import static org.eclipse.jetty.websocket.common.events.annotated.InvalidSignatureException.build;


public class Proxy {
    //private final LoadHandler loadHandler;

    private final LoadingCache<String, List<String>> cache;

    private LoadHandler loadHandler;

    public Proxy(CacheLoader<String, ACSResponse> loader, CacheConfigurator config) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(config.getMaximumSize())
                .expireAfterWrite(config.getExpireAfterWriteMinutes(), TimeUnit.MINUTES)
                .build(loader);
    }

    public void clearCache() {
        this.cache.invalidateAll();
    }
}
