package edu.brown.cs.student.main.server;

import static spark.Spark.after;
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

  public Server(String[] args) {
    this.args = args;
  }

  public Server() {}

  public static void main(String[] args) {
    Server server = new Server(args);
    server.run();
  }

  private void run() {
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /activity endpoints
    //    Spark.get("loadcsv", new LoadHandler());
    //    Spark.init();
    //    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
