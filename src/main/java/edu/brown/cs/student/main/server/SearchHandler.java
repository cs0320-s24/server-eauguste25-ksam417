package edu.brown.cs.student.main.server;

/** Criteria */
import edu.brown.cs.student.main.csv.DataSource;
import edu.brown.cs.student.main.csv.Parser;
import spark.Request;
import spark.Response;
import spark.Route;

/** Endpoint which sends back rows matching the given search criteria. */

// Instantiate the search object, and then pass reader into the server file path

// SearchCSV
public class SearchHandler implements Route {
  private DataSource source;
  private Parser parser;
  private String filepath = new Server().filepath;

  public SearchHandler(DataSource source) {
    this.source = source;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    return null;
  }
}
