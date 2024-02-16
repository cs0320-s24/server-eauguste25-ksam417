package edu.brown.cs.student.main.Server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
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

  private final BroadbandDataSource broadbandDataSource;

  public BroadbandHandler(BroadbandDataSource broadbandDataSource) {
    this.broadbandDataSource = broadbandDataSource;
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
      if (state == null || county == null) {
        responseMap.put("type", "error");
        responseMap.put("errorType", "missing parameter");
      }
      else{
        List<String> data = this.broadbandDataSource.getInternetAccess(state, county);
        responseMap.put("type", "success");
        responseMap.put("Date/Time", timeFormatter.format(requestTime));

        /** Probably going to change this formatting when it is working* */
        responseMap.put("Bandwith data: ", data);

        return adapter.toJson(responseMap);
      }
    } catch (DatasourceException e) {
      responseMap.put("type", "error");
      responseMap.put("errorType", "datasource");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);
    }
    return adapter.toJson(responseMap);
  }
}
