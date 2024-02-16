package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.CSV.DataSource.CSVDataSource;
import edu.brown.cs.student.main.CSV.Search;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;
import java.util.List;
import org.junit.After;
import org.junit.Before;
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

  @Before
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

  @After
  public void tearDown() {}

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

  @Test
  public void testLoadAndView() throws Exception {

  }
}
