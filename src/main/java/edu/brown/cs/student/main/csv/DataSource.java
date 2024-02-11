package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.Interfaces.CreatorFromRow;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class DataSource {
  private List<List<String>> CSVData;
  public boolean isLoaded;
  private String filepath;
  private Search searcher;

  public boolean loadCSV(String filePath) throws Exception {
    try {
      Reader reader = new FileReader(filePath);
      CreatorFromRow<List<String>> creator = new MakeList();
      Parser CSVParser = new Parser(reader);
      this.CSVData = CSVParser.parse();
      this.isLoaded = true;
      return true;
    } catch (Exception e) {
      if (this.CSVData == null) {
        throw new Exception("File does not exist");
      }
      // Handle the by throwing a custom expression that is more informative at runtime
      throw new FileNotFoundException(e.getMessage());
    }
  }

  public List<List<String>> getCSVData() {
    return this.CSVData;
  }
}
