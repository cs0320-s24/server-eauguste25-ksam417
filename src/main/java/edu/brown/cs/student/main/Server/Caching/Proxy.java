package edu.brown.cs.student.main.Server.Caching;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import java.util.List;

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
