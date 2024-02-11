package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.csv.DataSource;
import edu.brown.cs.student.main.csv.Search;
import java.io.FileNotFoundException;
import java.io.IOException;
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
  public String filepath;
  private String searchTerm;
  private String columnIdentifier;
  private int columnIndex;
  private List<List<String>> parsedData;

  public Server(String[] args) {
    this.args = args;
  }

  public Server() {}

  public static void main(String[] args) {
    Server server = new Server(args);
    server.run();
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /activity endpoints

    DataSource source = new DataSource();
    //    Spark.get("searchcsv", new SearchHandler(source));
    Spark.get("viewcsv", new ViewHandler(source));
    Spark.get("loadcsv", new LoadHandler());
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  public void run() {
    // possible root path?

    this.scanner = new Scanner(System.in);
    System.out.println("What is the path of the file you are searching?");
    String csvPath = scanner.nextLine();
    try {
      DataSource source = new DataSource();
      Boolean isLoaded = source.loadCSV(csvPath);
      if (isLoaded) {
        List<List<String>> data = source.getCSVData();
      } else {
        System.out.println("The CSV file was unable to be loaded, please try again.");
      }
      System.out.println("What is the phrase that you are searching for in this file?");
      // This set of methods refines the input
      this.searchTerm = scanner.nextLine().toLowerCase().strip().replaceAll("[\"']", "");
      this.handleColumn();

    } catch (FileNotFoundException e) {
      System.err.println("File Not Found");
      System.exit(1);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (FactoryFailureException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
