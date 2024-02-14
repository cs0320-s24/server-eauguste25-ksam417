package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;
import edu.brown.cs.student.main.csv.DataSource;
import edu.brown.cs.student.main.csv.MakeList;
import org.junit.jupiter.api.Test;

public class TestHandlers {

  @Test
  public void testLoadCSV() throws Exception {
    DataSource source = new DataSource();
    System.out.println(
        source.loadCSV(
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity.csv"));
    System.out.println(source.getCSVData());
    assertTrue(
        source.loadCSV(
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity.csv"));
    System.out.println(source.isLoaded);
  }

  @Test
  public void testDataSourceCSVData() {
    // List<List<String>> csvData = List.of("[[State, Data Type, Average Weekly Earnings, Number of
    // Workers, Earnings Disparity, Employed Percent], [RI, White, \" $1,058.47 \", 395773.6521,
    // $1.00 , 75%], [RI, Black,  $770.26 , 30424.80376,  $0.73 , 6%], [RI, Native American/American
    // Indian,  $471.07 , 2315.505646,  $0.45 , 0%], [RI, Asian-Pacific Islander, \" $1,080.09 \",
    // 18956.71657,  $1.02 , 4%], [RI, Hispanic/Latino,  $673.14 , 74596.18851,  $0.64 , 14%], [RI,
    // Multiracial,  $971.89 , 8883.049171,  $0.92 , 2%]]\n")
  }

  @Test
  public void testViewHandlerData() throws Exception {
    DataSource source = new DataSource();
    MakeList list = new MakeList();
    LoadHandler load =
        new LoadHandler(
            source,
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/test.csv");
    source.loadCSV(
        "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/test.csv");
    ViewHandler view = new ViewHandler(load, load.getSource());
    System.out.println(source.getCSVData());
    System.out.println(load.getSource().getLoadStatus());
    System.out.println(load.getSource());
    System.out.println(load.getFilePath());
    System.out.println(load.getSource().getCSVData());
  }

  public void testSearchHandler() throws Exception {
    DataSource source = new DataSource();
    MakeList list = new MakeList();
    LoadHandler load =
        new LoadHandler(
            source,
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/test.csv");
    source.loadCSV(
        "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/test.csv");
    SearchHandler search = new SearchHandler(load, load.getSource());
    System.out.println(source.getCSVData());
  }
}
