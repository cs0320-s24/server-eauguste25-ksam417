package edu.brown.cs.student.main.Server.Handlers;

/** Criteria */
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.csv.ACSData;
import edu.brown.cs.student.main.Interfaces.ACSDataSource;
import edu.brown.cs.student.main.csv.LocationData;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Endpoint which sends back the broadband data from the ACS described.
 *
 * <p>Include date and time that all data was retrieved from the ACS API by your API server, as well
 * as the state and county names your server received.
 */
public class BroadbandHandler implements Route {

  private final ACSDataSource state;

  public BroadbandHandler(ACSDataSource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String state = request.queryParams("state");
    String county = request.queryParams("county");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime requestTime = LocalDateTime.now();

    Moshi moshi = new Moshi.Builder().build();

    Type mapObject = Types.newParameterizedType(Map.class, String.class, Object.class);

    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapObject);

    Map<String, Object> responseMap = new HashMap<>();

    try {
      LocationData locationData = new LocationData(state, county);

      ACSData data = this.state.getPercent(locationData);
      String percent = data.percent();

      if (state == null || county == null) {
        responseMap.put("type", "error");
        responseMap.put("errorType", "missing parameter");
      }

      responseMap.put("type", "success");
      responseMap.put("Date/Time", timeFormatter.format(requestTime));

      /** Probably going to change this formatting when it is working* */
      responseMap.put("County: " + county + ", State: " + state + "percentage", percent);

      return adapter.toJson(responseMap);
    } catch (DatasourceException e) {
      responseMap.put("type", "error");
      responseMap.put("errorType", "datasource");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    }
  }

  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }
}
