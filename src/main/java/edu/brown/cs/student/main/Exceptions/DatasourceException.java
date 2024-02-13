package edu.brown.cs.student.main.Exceptions;

/**
 * This exception communicates that something went wrong with a requested datasource. It _wraps_ the
 * original cause as a field, which helps with debugging, but also allows the caller to handle the
 * issue uniformly if they wish, without looking inside.
 */
public class DatasourceException extends Exception {
  // The root cause of this datasource problem
  private final Throwable cause;

  public DatasourceException(String message) {
    super(message); // Exception message
    this.cause = null;
  }

  public DatasourceException(String message, Throwable cause) {
    super(message); // Exception message
    this.cause = cause;
  }
}
