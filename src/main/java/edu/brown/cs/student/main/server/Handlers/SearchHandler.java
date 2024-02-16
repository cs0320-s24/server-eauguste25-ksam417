package edu.brown.cs.student.main.server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.DataSource.CSVDataSource;
import edu.brown.cs.student.main.CSV.Search;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {

  private CSVDataSource source;
  private LoadHandler loadHandler;
  private Search search;
  private String searchterm;
  private String colHeader;
  private String colIndex;

  /**
   * A constructor for the SearchHandler class which takes in a LoadHandler and a CSVDataSource in
   * order to conduct a search on a loaded CSV file
   *
   * @param loadHandler
   * @param source
   */
  public SearchHandler(LoadHandler loadHandler, CSVDataSource source) {
    this.source = source;
    this.loadHandler = loadHandler;
  }

  /**
   * Handles the search functionality after a CSV file is loaded in
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Moshi moshi = new Builder().build();
    Type stringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> jsonAdapter = moshi.adapter(stringObject);

    this.searchterm = request.queryParams("searchterm");
    this.colHeader = request.queryParams("header");
    this.colIndex = request.queryParams("column_index");

    // Check if searchTerm is null
    if (this.searchterm == null) {
      // Handle null searchTerm
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("result", "error");
      responseMap.put("message", "Search term is required.");
      return jsonAdapter.toJson(responseMap);
    }

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    this.source = this.loadHandler.getSource();
    String filePath = this.loadHandler.getFilePath();
    boolean isLoaded = this.source.loadCSV(filePath);

    if (!isLoaded) {
      responseMap.put("result", "error");
      responseMap.put("message", "Failed to load CSV file.");
      return jsonAdapter.toJson(responseMap);
    }

    try {
      List matchingRows;
      if (this.colHeader != null) {
        this.search = new Search(this.source.getCSVData(), this.searchterm, this.colHeader);
      } else if (this.colIndex != null) {
        this.search = new Search(this.source.getCSVData(), this.searchterm, this.colIndex);
      } else {
        this.search = new Search(this.source.getCSVData(), this.searchterm);
      }
      matchingRows = this.search.search();

      responseMap.put("type", "success");
      responseMap.put("searchterm", this.searchterm);
      if (this.colHeader != null) {
        responseMap.put("column_header", this.colHeader);
      } else if (this.colIndex != null) {
        responseMap.put("column_index", this.colIndex);
      }
      responseMap.put("matching_rows", matchingRows);
    } catch (Exception e) {
      responseMap.put("result", "error");
      responseMap.put("message", e.getMessage()); // Provide error message
    }

    return jsonAdapter.toJson(responseMap);
  }

  /**
   * Getter method for the search term
   *
   * @return the input search term
   */
  public String getSearchTerm() {
    return this.searchterm;
  }

  public String colHeader() {
    return this.colHeader;
  }

  public int colIndex() {
    return Integer.parseInt(this.colIndex);
  }

  public CSVDataSource getSource() {
    return this.source;
  }
}
