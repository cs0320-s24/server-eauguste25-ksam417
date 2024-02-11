package edu.brown.cs.student.main.server;

/** Criteria */
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import edu.brown.cs.student.main.csv.CSVAPIUtilities;
import edu.brown.cs.student.main.csv.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Endpoint which loads a CSV file if one is located at the specified path.
 *
 * <p>No more than one CSV file should be loaded at a time, but it should be possible to change from
 * one CSV file to another by making additional calls to loadcsv.
 */
public class LoadHandler implements Route {

  private String filepath = new Server().filepath;
  private DataSource source;

  // get the name of the filepath you are searching for
  // update variable in search class
  // the only thing that changes for view would be dependent on the filepath
  // load would update and view and search would use the information from that variable

  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    Moshi moshi = new Builder().build();
    // Sends a request to the API and receives JSON back
    // Deserializes JSON into a loadcsv
    Set<String> params = request.queryParams();
    // requests the filepath
    this.filepath = request.queryParams("filepath");
    Reader fileReader = new FileReader(this.filepath);
    try {
      Boolean isLoaded = this.source.loadCSV(this.filepath);
      if (isLoaded) {
        String filepathJson = this.sendRequest(Integer.parseInt(this.filepath));
        this.source = CSVAPIUtilities.deserializeActivity(filepathJson);

        responseMap.put("type", "success");
        responseMap.put("filepath", filepath);
        return responseMap;
      }
      // Sends a request to the API and receives JSON back
      // Adds results to the responseMap
    } catch (Exception e) {
      e.printStackTrace();
      // Adds results to the responseMap
      responseMap.put("result", "success");
      return responseMap;
    }
    return responseMap;
  }

  private String sendRequest(int i) throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildApiRequest =
        HttpRequest.newBuilder().uri(new URI("http://localhost:3232")).GET().build();

    HttpResponse<String> sentApiResponse =
        HttpClient.newBuilder().build().send(buildApiRequest, HttpResponse.BodyHandlers.ofString());

    System.out.println(sentApiResponse);
    System.out.println(sentApiResponse.body());

    return sentApiResponse.body();
  }
}
