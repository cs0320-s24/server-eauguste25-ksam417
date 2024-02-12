package edu.brown.cs.student.main.server;

/** Criteria */
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.csv.InternetData;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Endpoint which sends back the broadband data from the ACS described.
 *
 * <p>Include date and time that all data was retrieved from the ACS API by your API server, as well
 * as the state and county names your server received.
 */
public class BroadbandHandler implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    return null;
  }

  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException("unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }

//  private static InternetData getCurrentData(String state, String county) throws DatasourceException, IllegalArgumentException {
//    try {
//       Double-check that the coordinates are valid. Yes, this has already
//       been checked, in principle, when the caller gave a Geolocation object.
//       But this is very cheap, and protects against mistakes I might make later
//       (such as making this method public, which would bypass the first check).
//      if(!Geolocation.isValidGeolocation(lat, lon)) {
//        throw new IllegalArgumentException("Invalid geolocation");
//      }
//
//      // NWS is not robust to high precision; limit to X.XXXX
//      lat = Math.floor(lat * 10000.0) / 10000.0;
//      lon = Math.floor(lon * 10000.0) / 10000.0;
//
//      System.out.println("Debug: getCurrentWeather: "+lat+";"+lon);
//
//      GridResponse gridResponse = resolveGridCoordinates(lat, lon);
//      String gid = gridResponse.properties().gridId();
//      String gx = gridResponse.properties().gridX();
//      String gy = gridResponse.properties().gridY();
//
//      System.out.println("Debug: gridResponse: "+gid+";"+gx+";"+gy);
//
//      URL requestURL = new URL("https", "api.weather.gov", "/gridpoints/"+gid+"/"+gx+","+gy);
//      HttpURLConnection clientConnection = connect(requestURL);
//      Moshi moshi = new Moshi.Builder().build();
//
//      // NOTE WELL: THE TYPES GIVEN HERE WOULD VARY ANYTIME THE RESPONSE TYPE VARIES
//      JsonAdapter<ForecastResponse> adapter = moshi.adapter(ForecastResponse.class).nonNull();
//
//      ForecastResponse body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
//
//      System.out.println(body); // records are nice for giving auto toString
//
//      clientConnection.disconnect();
//
//      // Validity checks for response
//      if(body == null || body.properties() == null || body.properties().temperature() == null)
//        throw new DatasourceException("Malformed response from NWS");
//      if(body.properties().temperature().values().isEmpty())
//        throw new DatasourceException("Could not obtain temperature data from NWS");
//
//      // TODO not well protected, always takes first timestamp of report
//      return new WeatherData(body.properties().temperature().values().get(0).value());
//    } catch (IOException e) {
//      throw new DatasourceException(e.getMessage(), e);
//    }
//  }
}
