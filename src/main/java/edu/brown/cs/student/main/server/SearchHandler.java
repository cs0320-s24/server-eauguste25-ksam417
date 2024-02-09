package edu.brown.cs.student.main.server;

/** Criteria */
import spark.Request;
import spark.Response;
import spark.Route;

/** Endpoint which sends back rows matching the given search criteria. */

// Instantiate the search object, and then pass reader into the server file path

// SearchCSV searchCSV = new SearchCSV(new Reader());
public class SearchHandler implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    return null;
  }
}
