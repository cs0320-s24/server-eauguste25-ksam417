package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.Interfaces.CreatorFromRow;
import edu.brown.cs.student.main.csv.Parser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import org.junit.Test;

/**
 * Testing class for CSV Parser Read Includes JUnit Testing for edge cases, exceptions and
 * functionality specifics
 *
 * @param <T>
 */
public class TestParser<T> {

  private Reader filename;
  private boolean hasHeader;
  private CreatorFromRow rowObject;

  /** Testing the case where Read takes in a file that does not exist */
  @Test
  public void testFileDoesNotExist() throws NullPointerException {
    try {
      this.filename =
          new FileReader("/Users/ericauguste/Desktop/CS32/csv-EAuguste25/data/stars/nine-star.csv");
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
    this.hasHeader = true;
    Parser<T> csvParser = new Parser<>(this.filename);
    assertThrows(NullPointerException.class, csvParser::parseUser);
  }

  /** Tests the simple functionality of the parseUser function */
  @Test
  public void testParseUser() throws IOException {
    this.filename =
        new FileReader("/Users/ericauguste/Desktop/CS32/csv-EAuguste25/data/stars/ten-star.csv");

    Parser<T> csvReader = new Parser<>(this.filename);
    List<List<String>> result = csvReader.parseUser();
    assertEquals(11, result.size()); // number of rows
  }

  /** Tests the functionality of parseUser on a StringReader without a header */
  @Test
  public void testReadWithoutHeaderStringReaderUser() throws IOException {
    String stringCSV = "Eric,21,Boston\nJavier,22,Providence\nAuguste,23,New York";
    Reader stringReader = new StringReader(stringCSV);
    Parser<T> csvReader = new Parser<>(stringReader);
    List<List<String>> result = csvReader.parseUser();
    assertEquals(3, result.size()); // Three rows
    assertEquals(List.of("Javier", "22", "Providence"), result.get(1)); // Second data row
  }

  /** Tests the functionality of parseUser on a StringReader with a header */
  @Test
  public void testReadWithStringReader() throws IOException {
    String stringCSV = "Name,Age,Location\nEric,21,Boston\nJavier,22,Providence";
    Reader stringReader = new StringReader(stringCSV);
    Parser<T> csvReader = new Parser(stringReader);
    List<List<String>> result = csvReader.parseUser();
    assertEquals(3, result.size());
    assertEquals(List.of("Name", "Age", "Location"), result.get(0)); // Header row
  }

  /** Tests the functionality of parseUser on malformed Data */
  @Test
  public void testReadMalformedData() throws IOException {
    this.filename =
        new FileReader(
            "/Users/ericauguste/Desktop/CS32/csv-EAuguste25/data/malformed/malformed_signs.csv");

    Parser<T> csvReader = new Parser<>(this.filename);
    List<List<String>> result = csvReader.parseUser();
    assertEquals(13, result.size()); // number of rows
  }

  /** Tests a malformed csv file without a header */
  @Test
  public void testReadMalformedWithoutHeader() throws IOException {
    this.filename =
        new FileReader(
            "/Users/ericauguste/Desktop/CS32/csv-EAuguste25/data/withoutHeader/no-header-ten-star.csv");

    Parser<T> csvReader = new Parser<>(this.filename);
    List<List<String>> result = csvReader.parseUser();
    assertEquals(10, result.size()); // number of rows
  }

  /** This tests a csv file that is outside the protected directory */
  @Test
  public void testUnprotectedCSVFile() throws FileNotFoundException {
    try {
      this.filename =
          new FileReader(
              "/Users/ericauguste/Desktop/CS32/csv-EAuguste25/unprotectedData/unprotected.csv");
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
  }
  /** Tests the new csv file for Sprint 2 */
  @Test
  public void testNewCSV() throws IOException {
    this.filename =
        new FileReader(
            "/Users/ericauguste/Desktop/CS32/Projects/server-eauguste25-ksam417/data/RI City & Town Income from American Community Survey 5-Year Estimates Source_ US Census Bureau, 2017-2021 American Community Survey 5-Year Estimates 2017-2021 - Sheet1.csv");

    Parser<T> csvReader = new Parser<>(this.filename);
    List<List<String>> result = csvReader.parseUser();
    assertEquals(41, result.size()); // number of rows
  }
}
