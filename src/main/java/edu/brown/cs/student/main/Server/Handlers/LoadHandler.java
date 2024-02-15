package edu.brown.cs.student.main.Server.Handlers;

/** Criteria */
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.DataSource;
import java.lang.reflect.Type;
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

  private DataSource source;
  private String filePath;

  /** Constructs a LoadHandler with the specified file path and CSVDataSource. */
  public LoadHandler() {}

  public LoadHandler(String filePath) {}

  // get the name of the filepath you are searching for
  // update variable in search class
  // the only thing that changes for view would be dependent on the filepath
  // load would update and view and search would use the information from that variable

  @Override
  public Object handle(Request request, Response response) throws Exception {

    String filePath = request.queryParams("filePath");

    Moshi moshi = new Builder().build();

    Type stringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

    JsonAdapter<Map<String, Object>> jsonAdapter = moshi.adapter(stringObject);

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    this.source = new DataSource(filePath);
    this.source.loadCSV(filePath);
    this.filePath = filePath;

    Set<String> params = request.queryParams();

    try {

      if (this.source.isLoaded) {
        responseMap.put("type", "success");
        responseMap.put("filepath", filePath);

      } else {
        responseMap.put("type", "error");
        responseMap.put("details", "Failed to load CSV file");
      }
    } catch (Exception e) {
      responseMap.put("type", "error");
      responseMap.put("details", e.getMessage());
      responseMap.put("filepath", filePath);
      return jsonAdapter.toJson(responseMap);
    }
    return jsonAdapter.toJson(responseMap);
  }

  public String getFilePath() {
    return this.filePath;
  }

  public DataSource getSource() {
    return this.source;
  }
}
