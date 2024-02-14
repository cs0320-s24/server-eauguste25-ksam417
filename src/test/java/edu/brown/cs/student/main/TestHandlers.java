package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.csv.DataSource;
import org.junit.jupiter.api.Test;

public class TestHandlers {

  @Test
  public void testLoadCSV() throws Exception {
    DataSource source = new DataSource();
    assertTrue(source.loadCSV("/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/dol_ri_earnings_disparity.csv"));
  }
}
