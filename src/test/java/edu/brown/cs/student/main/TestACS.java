package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.CSV.DataSource.CSVDataSource;
import edu.brown.cs.student.main.CSV.Mock.MockDataSource1;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.Server.Handlers.BroadbandHandler;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestACS {

  public TestACS() throws DatasourceException {
  }

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
  private MockDataSource1 mockedSource = new MockDataSource1();

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

  /**
   * Tests a mock data source with the ACS API
   * @throws DatasourceException
   */
  @Test
  public void testMockDataSource1() throws DatasourceException {
    MockDataSource1 mockDataSource = new MockDataSource1();

    List<String> result = mockDataSource.getInternetAccess("Boulder County", "Colorado");

    List<String> expected = new ArrayList<>();
    expected.add("Band Width:" + 93.4);
    expected.add("State:" + "Colorado");
    expected.add("County:" + "Boulder County");
    expected.add("Date:" + LocalDate.now());
    // expected.add("Current time:" + LocalTime.now());

    // Compare the actual result with the expected result
    assertEquals(expected, result);
  }

  /**
   * Tests whether an error is thrown when an invalid request is made to the API in the form of
   * an incorrect state name
   * @throws DatasourceException
   */

  @Test
  public void testInvalidAPIResponse() throws DatasourceException {
    BroadbandDataSource data = new BroadbandDataSource();

    // Assuming you have a method to make API requests
    // Ensure that your application handles the invalid response gracefully
    assertThrows(DatasourceException.class, () ->
        data.returnStateName("Greece"));
  }

  /**
   * Tests calls on csv files and calls to the API at the same time
   * @throws Exception
   */

  @Test
  public void testCSVAndACSHandling() throws Exception {
    MockDataSource1 mockDataSource = new MockDataSource1();
    CSVDataSource source = new CSVDataSource();
    source.loadCSV(
        "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/RI City & Town Income from American Community Survey 5-Year Estimates Source_ US Census Bureau, 2017-2021 American Community Survey 5-Year Estimates 2017-2021 - Sheet1.csv");

    List<String> result = mockDataSource.getInternetAccess("Boulder County", "Colorado");

    List<String> expected = new ArrayList<>();
    expected.add("Band Width:" + 93.4);
    expected.add("State:" + "Colorado");
    expected.add("County:" + "Boulder County");
    expected.add("Date:" + LocalDate.now());
    // expected.add("Current time:" + LocalTime.now());

    // Compare the actual result with the expected result
    assertEquals(expected, result);
  }

}