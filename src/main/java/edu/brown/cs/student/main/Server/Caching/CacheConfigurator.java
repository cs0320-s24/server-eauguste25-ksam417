package edu.brown.cs.student.main.Server.Caching;

/**
 serves as a data holder for various cache settings that a developer can customize.
 It encapsulates configuration parameters such as maximum size, expiration time after write.
 **/
public class CacheConfigurator {
    private long maximumSize = 100; // Default maximum size
    private long expireAfterWriteMinutes = 60; // Default expiration time (in minutes)

    public CacheConfigurator(){

    }

    // Getter and setter for maximumSize
    public long getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(long maximumSize) {
        this.maximumSize = maximumSize;
    }

    // Getter and setter for expireAfterWriteSeconds
    public long getExpireAfterWriteMinutes() {
        return expireAfterWriteMinutes;
    }

    public void setExpireAfterWriteMinutes(long expireAfterWriteMinutes) {
        this.expireAfterWriteMinutes = expireAfterWriteMinutes;
    }
}
