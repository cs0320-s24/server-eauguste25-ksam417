package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.CSV.DataSource.CSVDataSource;
import edu.brown.cs.student.main.CSV.Search;
import edu.brown.cs.student.main.Server.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import spark.Spark;

/**
 * A command-line entry point is intended to start the server (i.e. clicking the green play button),
 * displaying minimal output, such as “Server started” and an instructional line. In addition to the
 * API features above, a command-line entry point should be integrated in your project. This will be
 * achieved through the use of a run script.
 */
public class Server {
  private String[] args;
  private Scanner scanner;
  private String searchTerm;
  private String columnIdentifier;
  private int columnIndex;
  private List<List<String>> parsedData;
  private static BroadbandDataSource broadbandDataSource;

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
      Spark.get("broadband", new BroadbandHandler(broadbandDataSource));
      Spark.init();
      Spark.awaitInitialization();
    } catch (Exception e) {
    }
    System.out.println("Server started at http://localhost:" + port);
  }

  public void run() {

  }

  /**
   * A helper method which includes the logic for handling different user input in the terminal.
   * Depends on whether there is a column identifier and whether a search term is found in the given
   * file
   *
   * @throws FileNotFoundException
   */
  private void handleColumn() throws FileNotFoundException {
    String hasColumnIdentifier = "";
    while (!hasColumnIdentifier.equals("yes") && !hasColumnIdentifier.equals("no")) {
      System.out.println("Do you know the header of the phrase you are looking for? (yes/no)");
      hasColumnIdentifier = this.scanner.nextLine().toLowerCase().strip();
    }
    if (hasColumnIdentifier.equals("yes")) {
      System.out.println("What is the column identifier?");
      // makes sure that the column identifier accounts for discrepancies in the user input and the
      // csv file
      // TODO: add functionality for columnindex
      this.columnIdentifier = this.scanner.nextLine().toLowerCase().strip();
      Search search = new Search(this.parsedData, this.searchTerm, this.columnIdentifier);
      List<List<String>> row = search.search();
      // If the phrase doesn't exist or our parser didn't detect we have a different message
      if (!row.isEmpty()) {
        System.out.println("The matching rows you are looking for in the data are: " + row);
      } else {
        System.out.println("The word hasn't been found in the file.");
      }
    } else {
      Search search = new Search(this.parsedData, this.searchTerm);
      List<List<String>> row = search.search();
      if (!row.isEmpty()) {
        System.out.println("The rows of the data you are looking for are: " + row);
      } else {
        System.out.println("The word hasn't been found in the file :(");
      }
    }
  }
}
