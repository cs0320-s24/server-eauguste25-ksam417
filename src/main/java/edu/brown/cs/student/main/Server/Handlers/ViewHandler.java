package edu.brown.cs.student.main.Server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVDataSource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Endpoint which sends back the entire CSV file's contents as a Json 2-dimensional array. */
public class ViewHandler implements Route {

  private CSVDataSource source;
  private LoadHandler loadHandler;

  /**
   * A constructor for the ViewHandler class which takes in a LoadHandler object and a CSVDataSource
   * in order to view the entirety of a CSV file as a JSON 2-dimensional array
   * @param loadHandler
   * @param source
   */
  public ViewHandler(LoadHandler loadHandler, CSVDataSource source) {
    this.source = source;
    this.loadHandler = loadHandler;
  }

  /**
   * Handles the functionality for viewing a CSV file in the browser
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Moshi moshi = new Moshi.Builder().build();
    Type mapObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> jsonAdapter = moshi.adapter(mapObject);
    Map<String, Object> responseMap = new HashMap<>();

    this.source = this.loadHandler.getSource();
    String filePath = this.loadHandler.getFilePath();
    boolean isLoaded = this.source.loadCSV(filePath);

    try {
      if (isLoaded) {
        responseMap.put("type", "success");
        // Add the two-dimensional json data to the responseMap
        responseMap.put("data", this.source.getCSVData());
      } else {
        // Handle the case where CSV is not loaded successfully
        responseMap.put("type", "error");
        responseMap.put("details", "Cannot view CSV file that is not loaded.");
      }
    } catch (Exception e) {
      // Error handling messages
      responseMap.put("type", "error");
      responseMap.put(
          "details", "An error occurred while attempting to view the CSV file: " + e.getMessage());
      return jsonAdapter.toJson(responseMap);
    }
    return jsonAdapter.toJson(responseMap);
  }
}
