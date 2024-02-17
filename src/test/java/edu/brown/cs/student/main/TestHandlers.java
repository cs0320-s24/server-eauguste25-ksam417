package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.CSV.DataSource.CSVDataSource;
import edu.brown.cs.student.main.CSV.Search;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
  public void tearDown() {}

  /**
   * This method tests the interaction of loading one file and viewing it and then loading an
   * entirely different file and viewing it
   *
   * @throws Exception
   */
  @Test
  public void testDifferentLoadsAndViews() throws Exception {
    // View CSV (expecting headers)
    this.setUp();

    String firstResponse = viewCSV();
    assertEquals(
        "[[State, Data Type, Average Weekly Earnings, Number of Workers, Earnings Disparity, Employed Percent], [RI, White,  $1,058.47 , 395773.6521,  $1.00 , 75%], [RI, Black,  $770.26 , 30424.80376,  $0.73 , 6%], [RI, Native American/American Indian,  $471.07 , 2315.505646,  $0.45 , 0%], [RI, Asian-Pacific Islander,  $1,080.09 , 18956.71657,  $1.02 , 4%], [RI, Hispanic/Latino,  $673.14 , 74596.18851,  $0.64 , 14%], [RI, Multiracial,  $971.89 , 8883.049171,  $0.92 , 2%]]",
        firstResponse);

    // Load CSV without headers
    loadCSV("/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/test.csv");

    // View CSV (expecting no headers)
    String secondResponse = viewCSV();
    assertEquals("[[Name, Age], [Eric, 21.0], [Javier, 22.0], [Auguste, 23.0]]", secondResponse);
    this.tearDown();
  }

  /**
   * This method tests the functionality of loading one CSV file in setup, viewing that file, and
   * then loading the same file without the header row and viewing that file by returning Strings of
   * the CSV data
   *
   * @throws Exception
   */
  @Test
  public void testCSVChangeWithMultipleLoads() throws Exception {
    // View CSV (expecting headers)
    this.setUp();

    String firstResponse = viewCSV();
    assertEquals(
        "[[State, Data Type, Average Weekly Earnings, Number of Workers, Earnings Disparity, Employed Percent], [RI, White,  $1,058.47 , 395773.6521,  $1.00 , 75%], [RI, Black,  $770.26 , 30424.80376,  $0.73 , 6%], [RI, Native American/American Indian,  $471.07 , 2315.505646,  $0.45 , 0%], [RI, Asian-Pacific Islander,  $1,080.09 , 18956.71657,  $1.02 , 4%], [RI, Hispanic/Latino,  $673.14 , 74596.18851,  $0.64 , 14%], [RI, Multiracial,  $971.89 , 8883.049171,  $0.92 , 2%]]",
        firstResponse);

    // Load CSV without headers
    loadCSV(
        "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity_no_headers.csv");

    // View CSV (expecting no headers)
    String secondResponse = viewCSV();
    assertEquals(
        "[[RI, White,  $1,058.47 , 395773.6521,  $1.00 , 75%], [RI, Black,  $770.26 , 30424.80376,  $0.73 , 6%], [RI, Native American/American Indian,  $471.07 , 2315.505646,  $0.45 , 0%], [RI, Asian-Pacific Islander,  $1,080.09 , 18956.71657,  $1.02 , 4%], [RI, Hispanic/Latino,  $673.14 , 74596.18851,  $0.64 , 14%], [RI, Multiracial,  $971.89 , 8883.049171,  $0.92 , 2%]]",
        secondResponse);
    this.tearDown();
  }

  /**
   * Helper method for loading CSV data
   *
   * @param csvPath
   * @throws Exception
   */
  private void loadCSV(String csvPath) throws Exception {
    // Load CSV data
    this.source.loadCSV(csvPath);
    this.csvData = this.source.getCSVData();
  }

  /**
   * Helper method for viewing CSV data
   *
   * @return
   * @throws Exception
   */
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
   */
  @Test
  public void testLoadCSVFalse() {
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

  /**
   * Tests loading a false csv file
   *
   * @throws Exception
   */
  @Test
  public void testLoadNotCSV() throws Exception {
    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              loadCSV("hello");
            });
    String expectedMessage = "hello (No such file or directory)";
    String actualMessage = exception.getMessage();
    assertEquals(expectedMessage, actualMessage);
  }
}
