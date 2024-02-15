package edu.brown.cs.student.main.Server.Handlers;

/** Criteria */
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.CSVDataSource;
import edu.brown.cs.student.main.csv.Search;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Endpoint which sends back rows matching the given search criteria. */
public class SearchHandler implements Route {

  private CSVDataSource source;
  private LoadHandler loadHandler;
  private Search search;
  private String searchterm;
  private String colHeader;
  private String colIndex;

  public SearchHandler(LoadHandler loadHandler, CSVDataSource source) {
    this.source = source;
    this.loadHandler = loadHandler;
    //    this.colIdentifier = colIdentifier;
    //    this.colIndex = colIndex;
  }

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

    try {
      if (isLoaded) {
        List matchingRows;
        if (this.colHeader != null) {
          this.search = new Search(this.source.getCSVData(), this.searchterm, this.colHeader);
          matchingRows = this.search.search();
          responseMap.put("type", "success");
          responseMap.put("searchterm", this.searchterm);
          responseMap.put("column_header:", this.colHeader);
          responseMap.put("matching rows: ", matchingRows);
        }
        if (this.colIndex != null) {
          this.search = new Search(this.source.getCSVData(), this.searchterm, this.colIndex);
          matchingRows = this.search.search();
          responseMap.put("type", "success");
          responseMap.put("searchterm", this.searchterm);
          responseMap.put("column_index:", this.colIndex);
          responseMap.put("matching rows: ", matchingRows);
        } else {
          this.search = new Search(this.source.getCSVData(), this.searchterm);
          matchingRows = this.search.search();
          if (!matchingRows.isEmpty()) {
            responseMap.put("type", "success");
            responseMap.put("searchterm", this.searchterm);
            responseMap.put("matching rows: ", matchingRows);
          } else {
            responseMap.put("type", "success");
            responseMap.put("result", "No matching rows were found. Please try again.");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "error");
      // put the rows that match the search term in the responseMap
      return jsonAdapter.toJson(responseMap);
    }

    // return the adapted version of the responseMap
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
