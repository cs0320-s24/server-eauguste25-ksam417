package edu.brown.cs.student.main.server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.DataSource.CSVDataSource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  private CSVDataSource source;
  private String filePath;
  private List data;
  private boolean loadStatus;

  /**
   * Constructor for LoadHandler that takes in a CSVDataSource and a String filepath to load and set
   * up for other endpoints
   *
   * @param source
   * @param filePath
   */
  public LoadHandler(CSVDataSource source, String filePath) {
    this.filePath = filePath;
    this.source = source;
  }


  /**
   * Handles the loading of a CSV file, making sure that it is able to be parsed
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.filePath = request.queryParams("filePath");
    Moshi moshi = new Builder().build();
    Type stringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

    JsonAdapter<Map<String, Object>> jsonAdapter = moshi.adapter(stringObject);

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    this.source.loadCSV(this.filePath);
    this.loadStatus = this.source.getLoadStatus();
    this.data = this.source.getCSVData();

    try {
      // if the csv file is loaded
      if (this.loadStatus) {
        responseMap.put("type", "success");
        responseMap.put("filepath", this.filePath);
        // if the csv file is not loaded
      } else {
        responseMap.put("type", "error");
        responseMap.put("details", "Failed to load CSV file");
      }
      // if an error is caught:
    } catch (Exception e) {
      responseMap.put("type", "error");
      responseMap.put("details", e.getMessage());
      responseMap.put("filepath", this.filePath);
      return jsonAdapter.toJson(responseMap);
    }
    return jsonAdapter.toJson(responseMap);
  }

  /**
   * Getter method for the file path field
   *
   * @return a String representing the filePath
   */
  public String getFilePath() {
    return this.filePath;
  }

  /**
   * Getter method for the CSVDataSource
   *
   * @return a CSVDataSource
   */
  public CSVDataSource getSource() {
    return this.source;
  }

  /**
   * Getter method for the CSV data
   *
   * @return the data of a CSV file
   */
  public List getData() {
    return this.data;
  }
}
