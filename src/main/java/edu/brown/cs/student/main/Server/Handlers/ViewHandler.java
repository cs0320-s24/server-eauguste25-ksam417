package edu.brown.cs.student.main.Server.Handlers;

/** Criteria */
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.DataSource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** viewcsv**, which sends back the entire CSV file's contents as a Json 2-dimensional array. */

/** Endpoint which sends back the entire CSV file's contents as a Json 2-dimensional array. */
public class ViewHandler implements Route {

  private DataSource source;

  private LoadHandler loadHandler;

  public ViewHandler() {}

  @Override
  public Object handle(Request request, Response response) throws Exception {

    // Creates a hashmap to store the results of the request

    Moshi moshi = new Moshi.Builder().build();

    Type mapObject = Types.newParameterizedType(Map.class, String.class, Object.class);

    JsonAdapter<Map<String, Object>> jsonAdapter = moshi.adapter(mapObject);

    Map<String, Object> responseMap = new HashMap<>();
    // Sends a request to the API and receives JSON back

    try {
      if (this.source.isLoaded) {
        responseMap.put("type", "success");

        // Add the two-dimensional json data to the responseMap
        responseMap.put("data", this.source.getCSVData());
        // Convert the csv data to json data
        return jsonAdapter.toJson(responseMap);
      } else {
        // Handle the case where CSV is not loaded successfully
        responseMap.put("type", "error");
        responseMap.put("details", "Cannot view CSV file that is not loaded.");
        return responseMap;
      }
    } catch (Exception e) {

      // Error handling messages
      responseMap.put("type", "error");
      responseMap.put(
          "details", "An error occurred while attempting to view the CSV file: " + e.getMessage());
      return responseMap;
    }
  }
}
