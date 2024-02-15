package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;
import edu.brown.cs.student.main.csv.DataSource;
import edu.brown.cs.student.main.csv.MakeList;
import edu.brown.cs.student.main.csv.Search;
import java.io.FileNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class TestHandlers {
  private LoadHandler loadHandler;
  private DataSource source;
  private String colHeader;
  private int colIndex;
  private String searchTerm;
  private ViewHandler viewHandler;
  private SearchHandler searchHandler;

  @Before
  public void setUp() throws FileNotFoundException, FactoryFailureException {
    this.source = new DataSource();

    this.loadHandler =
        new LoadHandler(
            this.source,
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity.csv");

    this.viewHandler = new ViewHandler(this.loadHandler, this.loadHandler.getSource());
    this.searchHandler = new SearchHandler(this.loadHandler, this.loadHandler.getSource());
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
    assertTrue(
        this.source.loadCSV(
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity.csv"));
    System.out.println(this.source.isLoaded);
    this.tearDown();
  }

  /**
   * This method tests whether ViewHandler is able to access loaded CSV data
   * @throws Exception
   */
  @Test
  public void testViewHandlerData() throws Exception {
    this.setUp();

    this.tearDown();
  }

  @Test
  public void testSearchHandler() throws Exception {
    this.setUp();
    System.out.println(this.searchHandler.getSource());
    this.tearDown();
  }
}
