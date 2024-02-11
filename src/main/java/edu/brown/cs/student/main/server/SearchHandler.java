package edu.brown.cs.student.main.server;

/** Criteria */
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import edu.brown.cs.student.main.csv.CSVAPIUtilities;
import edu.brown.cs.student.main.csv.DataSource;
import edu.brown.cs.student.main.csv.Parser;
import edu.brown.cs.student.main.csv.Search;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

/** Endpoint which sends back rows matching the given search criteria. */

// Instantiate the search object, and then pass reader into the server file path

// SearchCSV searchCSV = new SearchCSV(new Reader());
public class SearchHandler implements Route {
  private DataSource source;
  private Parser parser;
  private String filepath = new Server().filepath;
  private String identifier;

  public SearchHandler(DataSource source) {
    this.source = source;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    Moshi moshi = new Builder().build();

    Set<String> params = request.queryParams();
    String searchTerm = request.queryParams("searchTerm");
    String header = request.queryParams("header");

    Reader fileReader = new FileReader(this.filepath);

    try {
      Boolean isLoaded = this.source.loadCSV(this.filepath);
      if (isLoaded) {
        String searchcsv = this.sendRequest(searchTerm);
        DataSource search = CSVAPIUtilities.deserializeActivity(searchTerm);
        List<List<String>> csvdata = search.getCSVData();
        Search searchObject = new Search(csvdata, searchTerm, this.identifier);
        List<List<String>> matchingRows = searchObject.search();

        responseMap.put("type", "success");
        responseMap.put("searchterm", searchTerm);
        responseMap.put("matching rows: ", matchingRows);

      }
      // Sends a request to the API and receives JSON back
      // Adds results to the responseMap
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "error");
      responseMap.put("searchTerm", searchTerm);
      // put the rows that match the search term in the responseMap

      return responseMap;
    }
    // return the adapted version of the responseMap
    return responseMap;
  }

  private String sendRequest(String searchTerm) throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildApiRequest =
        HttpRequest.newBuilder().uri(new URI("http://localhost:3232" + searchTerm))
            .GET()
            .build();

    HttpResponse<String> sentApiResponse =
        HttpClient.newBuilder().build().send(buildApiRequest, HttpResponse.BodyHandlers.ofString());

    System.out.println(sentApiResponse);
    System.out.println(sentApiResponse.body());

    return sentApiResponse.body();
  }
}
