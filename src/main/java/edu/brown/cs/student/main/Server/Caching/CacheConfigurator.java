package edu.brown.cs.student.main.Server.Caching;

import java.util.concurrent.TimeUnit;

/**
 * serves as a data holder for various cache settings that a developer can customize. It
 * encapsulates configuration parameters such as maximum size, expiration time after write.
 */
public class CacheConfigurator {
  private long maximumSize = 100; // Default maximum size
  private long expireAfterWriteMinutes = 60; // Default expiration time (in minutes)
  private TimeUnit timeUnit = TimeUnit.MINUTES; // Default time unit

  public CacheConfigurator() {}

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

  // Getter and setter for expireAfterWriteMinutes
  public long getExpireAfterWrite() {
    return this.expireAfterWriteMinutes;
  }

  public void setExpireAfterWriteMinutes(long expireAfterWriteMinutes) {
    this.expireAfterWriteMinutes = expireAfterWriteMinutes;
  }

  // Getter for timeUnit
  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  // Setter for timeUnit
  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }
}
