package edu.brown.cs.student.main.server;

/** Criteria */
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** viewcsv**, which sends back the entire CSV file's contents as a Json 2-dimensional array. */

/** Endpoint which sends back the entire CSV file's contents as a Json 2-dimensional array. */
public class ViewHandler implements Route {

  private DataSource source;

  private List<List<String>> jsonCSVdata;
  private String filepath = new Server().filepath;

  public ViewHandler(DataSource source) {
    this.source = source;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    Moshi moshi = new Moshi.Builder().build();

    JsonAdapter<List<List<String>>> jsonAdapter =
        moshi.adapter(Types.newParameterizedType(List.class, List.class, String.class));
    // Sends a request to the API and receives JSON back

    // requests the filepath
    this.filepath = request.queryParams("filepath");
    try {
      if (this.source.isLoaded) {
        List<List<String>> csvData = this.source.getCSVData();
        // Convert the csv data to json data
        String jsonCSVData = jsonAdapter.toJson(csvData);
        // Add the two-dimensional json data to the responseMap
        responseMap.put("data", jsonCSVData);
        return moshi
            .adapter(Types.newParameterizedType(List.class, List.class, String.class))
            .toJson(responseMap);
      } else {
        // Handle the case where CSV is not loaded successfully
        responseMap.put("type", "error");
        responseMap.put("message", "Cannot view CSV file that is not loaded.");
      }
    } catch (Exception e) {

      // Error handling messages
      responseMap.put("type", "error");
      responseMap.put(
          "message", "An error occurred while attempting to view the CSV file: " + e.getMessage());
    }
    return moshi
        .adapter(Types.newParameterizedType(List.class, List.class, String.class))
        .toJson(responseMap);
  }
}
