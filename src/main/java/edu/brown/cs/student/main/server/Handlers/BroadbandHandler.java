package edu.brown.cs.student.main.Server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.CSV.Records.LocationData;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.Server.Caching.Cache;
import edu.brown.cs.student.main.Server.Caching.CacheConfigurator;
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

  private Cache cache;

  private CacheConfigurator configuration;

  private final BroadbandDataSource broadbandDataSource;

  public BroadbandHandler() {
    try {
      this.configuration = new CacheConfigurator();
      this.broadbandDataSource = new BroadbandDataSource();

      this.cache = new Cache(this.configuration, this.broadbandDataSource);
    } catch (DatasourceException e) {
      throw new RuntimeException(e);
    }
    System.out.println(this.broadbandDataSource);
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String state = request.queryParams("state");

    String county = request.queryParams("county");

    System.out.println(state);
    System.out.println(county);

    LocationData data = new LocationData(state, county);

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    LocalDateTime requestTime = LocalDateTime.now();

    Moshi moshi = new Moshi.Builder().build();

    Type mapObject = Types.newParameterizedType(Map.class, String.class, Object.class);

    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapObject);

    Map<String, Object> responseMap = new HashMap<>();

    try {
      System.out.println("yehhee heee");
      if (state == null || county == null) {
        System.out.println("get good nigga");
        responseMap.put("type", "error");
        responseMap.put("errorType", "missing parameter");
      } else {
        System.out.println("haha ha");
        List<String> returnData = this.cache.inCache(data);
        System.out.println(returnData);
        responseMap.put("type", "success");
        responseMap.put("Date/Time", timeFormatter.format(requestTime));

        /** Probably going to change this formatting when it is working* */
        responseMap.put("Bandwith data: ", returnData);

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
