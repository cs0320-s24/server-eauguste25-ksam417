package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import edu.brown.cs.student.main.CSV.DataSource.CSVDataSource;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;
import spark.Spark;

/**
 * A command-line entry point is intended to start the server (i.e. clicking the green play button),
 * displaying minimal output, such as “Server started” and an instructional line. In addition to the
 * API features above, a command-line entry point should be integrated in your project. This will be
 * achieved through the use of a run script.
 */
public class Server {
  private String[] args;
  // private static BroadbandDataSource broadbandDataSource;

  public Server(String[] args) {
    this.args = args;
  }

  public Server() {}

  public static void main(String[] args) {
    int port = 3333;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    CSVDataSource source = new CSVDataSource();

    LoadHandler testLoadHandler =
        new LoadHandler(
            source,
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/RI City & Town Income from American Community Survey 5-Year Estimates Source_ US Census Bureau, 2017-2021 American Community Survey 5-Year Estimates 2017-2021 - Sheet1.csv");

    try {
      /** Endpoint which loads a CSV file */
      Spark.get("loadcsv", testLoadHandler);
      /** Endpoint which prints the entirety of a loaded CSV file */
      Spark.get("viewcsv", new ViewHandler(testLoadHandler, testLoadHandler.getSource()));
      /** Endpoint which sends back rows matching the given search criteria. */
      Spark.get("searchcsv", new SearchHandler(testLoadHandler, testLoadHandler.getSource()));
      // Spark.get("broadband", new BroadbandHandler(broadbandDataSource));
      Spark.init();
      Spark.awaitInitialization();
    } catch (Exception e) {

    }

    System.out.println("Server started at http://localhost:" + port);
  }

  public void run() {}
}
