package edu.brown.cs.student.main.Server.Handlers;

/** Criteria */
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.Server;
import edu.brown.cs.student.main.csv.DataSource;
import edu.brown.cs.student.main.csv.Parser.Parser;
import edu.brown.cs.student.main.csv.Search;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Endpoint which sends back rows matching the given search criteria. */
public class SearchHandler implements Route {

  private DataSource source;
  private Parser parser;
  private String filepath = new Server().filepath;
  private String identifier;

  public SearchHandler() {}

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String searchTerm = request.queryParams("searchTerm");
    String header = request.queryParams("header");

    Moshi moshi = new Builder().build();

    Type stringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

    JsonAdapter<Map<String, Object>> jsonAdapter = moshi.adapter(stringObject);

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    Search searchObject = new Search(this.source.getCSVData(), searchTerm, this.identifier);

    try {
      this.source.loadCSV(this.filepath);

      if (this.source.isLoaded) {
        List<List<String>> matchingRows = searchObject.search();

        if (matchingRows.isEmpty()) {
          responseMap.put("errorType", "no search results");
        }

        responseMap.put("type", "success");
        responseMap.put("searchterm", searchTerm);
        responseMap.put("matching rows: ", matchingRows);

        // What is this used for
        String searchcsv = this.sendRequest(searchTerm);
      }
      // Sends a request to the API and receives JSON back
      // Adds results to the responseMap
    } catch (Exception e) {
      responseMap.put("result", "error");
      responseMap.put("searchTerm", searchTerm);
      // put the rows that match the search term in the responseMap

      return jsonAdapter.toJson(responseMap);
    }
    // return the adapted version of the responseMap
    return jsonAdapter.toJson(responseMap);
  }

  private String sendRequest(String searchTerm)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildApiRequest =
        HttpRequest.newBuilder().uri(new URI("http://localhost:3232" + searchTerm)).GET().build();

    HttpResponse<String> sentApiResponse =
        HttpClient.newBuilder().build().send(buildApiRequest, HttpResponse.BodyHandlers.ofString());

    System.out.println(sentApiResponse);
    System.out.println(sentApiResponse.body());

    return sentApiResponse.body();
  }
}
