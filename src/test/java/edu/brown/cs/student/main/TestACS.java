package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.Records.ACSData;
import edu.brown.cs.student.main.CSV.Records.LocationData;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.Interfaces.ACSDataSource;
import edu.brown.cs.student.main.Server.Handlers.BroadbandHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

 public class TestACS {

  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  // Helping Moshi serialize Json responses; see the gearup for more info.
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;
  private JsonAdapter<List> dataAdapter;
    private ACSDataSource mockedSource =
        new ACSDataSource() {
          @Override
          public ACSData getPercent(LocationData loc) throws DatasourceException {
            return null;
          }
        };

    @BeforeEach
    public void setup() {

      Spark.get("Bandwidth", new BroadbandHandler());
      Spark.awaitInitialization(); // don't continue until the server is listening

      // New Moshi adapter for responses (and requests, too; see a few lines below)
      //   For more on this, see the Server gearup.
      Moshi moshi = new Moshi.Builder().build();
      this.adapter = moshi.adapter(mapStringObject);
      this.dataAdapter = moshi.adapter(List.class);
    }

  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/BandWidth");
    Spark.awaitStop(); // don't proceed until the server is stopped

  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL(apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testAPIRequestSuccessMock() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("http://localhost:3333/BandWidth?county=Boulder%20County&state=Colorado");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals("success", body.get("type"));

    // Mocked data: correct temp? We know what it is, because we mocked.
    List<String> list = (List<String>) body.get("Bandwidth");
    List<String> expected = new ArrayList<>();
    expected.add("Band Width:" + 93.4);
    expected.add("State:" + "Colorado");
    expected.add("County:" + "Boulder County");
    expected.add("Date:" + LocalDate.now());
    expected.add("Current time:" + LocalTime.now());
    //        String bandwidth = body.get("Bandwidth");
    //        Gson gson = new Gson();
    //
    //        // Parse the JSON string into a JsonObject
    //        JsonObject jsonObject = gson.fromJson(, body.get("Bandwidth"));
    Assert.assertEquals(list.get(0), expected.get(0));
    loadConnection.disconnect();
  }

  //  @Test
  //  public void testAPIRequestSuccessReal() throws IOException {
  //    /////////// LOAD DATASOURCE ///////////
  //    // Fetch the data from ACS
  //
  //    HttpURLConnection loadConnection =
  //        tryRequest(
  //
  // "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:051&in=state:01");
  //    // Get an OK response (the *connection* worked, the *API* provides an error response)
  //    assertEquals(200, loadConnection.getResponseCode());
  //    // Process the response
  //    Moshi moshi = new Moshi.Builder().build();
  //    JsonAdapter<List> realAdapter = moshi.adapter(List.class).nonNull();
  //    List<String> body =
  //        realAdapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
  //    ACSDataSource data = new ACSDataSource();
  //    List<List<String>> result =
  //        data.getDataFromURL(
  //            new URL(
  //
  // "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:013&in=state:08"));
  //
  //    assertTrue(this.mockedSource.getBandwith("051",
  // "01").get(0).contains(result.get(1).get(1)));
  //    loadConnection.disconnect();
  //  }

  //  @Test
  //  public void testViewCSVHandlerFailure() throws IOException {
  //    HttpURLConnection clientConnection1 = tryRequest(
  //        "loadcsv?path=data/census/income_by_race.csv&hasHeader=true");
  //    assertEquals(200, clientConnection1.getResponseCode());
  //    HttpURLConnection clientConnection = tryRequest("viewcsv");
  //    assertEquals(200, clientConnection.getResponseCode());
  //    Map<String, Object> response = adapter.fromJson(
  //        new Buffer().readFrom(clientConnection.getInputStream()));
  //    assertEquals("success", response.get("result"));
  //  }

  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }
 }
