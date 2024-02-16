package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spark.Spark.after;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.DataSource.CSVDataSource;
import edu.brown.cs.student.main.CSV.Search;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;
import spark.Request;
import spark.Response;

public class TestHandlers {
  private LoadHandler loadHandler;
  private CSVDataSource source;
  private List<List<Object>> csvData;
  private String colHeader;
  private int colIndex;
  private String searchTerm;
  private ViewHandler viewHandler;
  private SearchHandler searchHandler;

  @BeforeEach
  public void setUp() throws Exception {
    this.source = new CSVDataSource();

    this.loadHandler =
        new LoadHandler(
            this.source,
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity.csv");

    this.viewHandler = new ViewHandler(this.loadHandler, this.loadHandler.getSource());
    this.searchHandler = new SearchHandler(this.loadHandler, this.loadHandler.getSource());

    // Load CSV data
    this.source.loadCSV(
        "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity.csv");

    // Retrieve CSV data
    this.csvData = this.source.getCSVData();

    // Parse CSV data
    Search searcherWithHeader =
        new Search(this.source.getCSVData(), this.searchTerm, this.colHeader);
    Search searchWithIndex = new Search(this.source.getCSVData(), this.searchTerm, this.colIndex);
    Search searchWithoutIdentifier = new Search(this.source.getCSVData(), this.searchTerm);

  }

  @AfterEach
  public void tearDown() {
  }

  @Test
  public void testNoHeadersOnSecondLoad() throws Exception {
    // View CSV (expecting headers)

    String firstResponse = viewCSV();
    assertEquals("[[State, Data Type, Average Weekly Earnings, Number of Workers, Earnings Disparity, Employed Percent], [RI, White,  $1,058.47 , 395773.6521,  $1.00 , 75%], [RI, Black,  $770.26 , 30424.80376,  $0.73 , 6%], [RI, Native American/American Indian,  $471.07 , 2315.505646,  $0.45 , 0%], [RI, Asian-Pacific Islander,  $1,080.09 , 18956.71657,  $1.02 , 4%], [RI, Hispanic/Latino,  $673.14 , 74596.18851,  $0.64 , 14%], [RI, Multiracial,  $971.89 , 8883.049171,  $0.92 , 2%]]",
        firstResponse);

    // Load CSV without headers
    loadCSV("/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity_no_headers.csv");

    // View CSV (expecting no headers)
    String secondResponse = viewCSV();
    assertEquals("[[RI, White,  $1,058.47 , 395773.6521,  $1.00 , 75%], [RI, Black,  $770.26 , 30424.80376,  $0.73 , 6%], [RI, Native American/American Indian,  $471.07 , 2315.505646,  $0.45 , 0%], [RI, Asian-Pacific Islander,  $1,080.09 , 18956.71657,  $1.02 , 4%], [RI, Hispanic/Latino,  $673.14 , 74596.18851,  $0.64 , 14%], [RI, Multiracial,  $971.89 , 8883.049171,  $0.92 , 2%]]",
        secondResponse);
  }

  private void loadCSV(String csvPath) throws Exception {
    // Load CSV data
    this.source.loadCSV(csvPath);
    this.csvData = this.source.getCSVData();
  }

  private String viewCSV() throws Exception {
    // View CSV data
    System.out.println(this.source.getCSVData().toString());
    return this.source.getCSVData().toString();
  }

  /**
   * This method tests whether LoadCSV in DataSource is able to load a CSV file
   *
   * @throws Exception
   */
  @Test
  public void testLoadCSVTrue() throws Exception {
    this.setUp();
    assertTrue(this.source.getLoadStatus());
    this.tearDown();
  }

  /**
   * This method tests whether the isLoaded status returns false on a data Source that isn't loaded
   *
   * @throws Exception
   */
  @Test
  public void testLoadCSVFalse() throws Exception {
    CSVDataSource data = new CSVDataSource();
    assertFalse(data.getLoadStatus());
  }

  /** Tests the getCSVData method on a loaded CSV file */
  @Test
  public void testGetCSVDataAfterLoading() throws Exception {
    this.setUp();
    assertNotNull(this.csvData);
    this.tearDown();
  }

}
