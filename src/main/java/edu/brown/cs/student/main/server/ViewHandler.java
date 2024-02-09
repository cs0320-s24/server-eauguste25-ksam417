package edu.brown.cs.student.main.server;

/** Criteria */

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Endpoint which sends back the entire CSV file's contents as a Json 2-dimensional array.
 */
public class ViewHandler implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    return null;
  }
}
