package edu.brown.cs.student.main.server;

/** Criteria */

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Endpoint which loads a CSV file if one is located at the specified path.
 *
 * No more than one CSV file should be loaded at a time, but it should be possible to change from
 * one CSV file to another by making additional calls to loadcsv.
 */
public class LoadHandler implements Route {


  @Override
  public Object handle(Request request, Response response) throws Exception {
    return null;
  }
}
