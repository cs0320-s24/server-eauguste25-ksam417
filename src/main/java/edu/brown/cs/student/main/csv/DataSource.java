package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Interfaces.CreatorFromRow;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class DataSource<T> {

  private List<List<String>> CSVData;
  public boolean isLoaded;
  private String filepath;
  private Search searcher;

  public DataSource(String filePath) {
    this.filepath = filePath;
  }

  public DataSource() {
  }

  public boolean loadCSV(String filePath) throws Exception {
    this.filepath = filePath;
    try {
      Reader reader = new FileReader(filepath);
      // CreatorFromRow<List<String>> creator = new MakeList();
      CreatorFromRow<T> rowObject = new RetListString<>();

      Parser CSVParser = new Parser(rowObject, reader);
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

  public List<List<String>> getCSVData() throws FactoryFailureException, FileNotFoundException {
    return this.CSVData;
  }
}
