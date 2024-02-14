package edu.brown.cs.student.main.Server.Handlers;

/** Criteria */
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.DataSource;
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

  private DataSource source;
  private LoadHandler loadHandler;
  private Search search;

  public SearchHandler(LoadHandler loadHandler, DataSource source) {
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

    String searchterm = request.queryParams("searchterm");
    String colHeader = request.queryParams("header");
    String colIndex = request.queryParams("column_index");

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    this.source = this.loadHandler.getSource();
    String filePath = this.loadHandler.getFilePath();
    boolean isLoaded = this.source.loadCSV(filePath);

    try {
      if (isLoaded) {
        List matchingRows;
        if (colHeader != null) {
          this.search = new Search(this.source.getCSVData(), searchterm, colHeader);
          matchingRows = this.search.search();
          responseMap.put("type", "success");
          responseMap.put("searchterm", searchterm);
          responseMap.put("matching rows: ", matchingRows);
        }
        if (colIndex != null) {
          this.search = new Search(this.source.getCSVData(), searchterm, colHeader);
          matchingRows = this.search.search();
          responseMap.put("type", "success");
          responseMap.put("searchterm", searchterm);
          responseMap.put("matching rows: ", matchingRows);
        } else {
          this.search = new Search(this.source.getCSVData(), searchterm);
          matchingRows = this.search.search();
          responseMap.put("type", "success");
          responseMap.put("searchterm", searchterm);
          responseMap.put("matching rows: ", matchingRows);
        }
      }
    } catch (Exception e) {
      responseMap.put("result", "error");
      responseMap.put("searchTerm", searchterm);
      // put the rows that match the search term in the responseMap
      return jsonAdapter.toJson(responseMap);
    }

    // return the adapted version of the responseMap
    return jsonAdapter.toJson(responseMap);
  }
}
