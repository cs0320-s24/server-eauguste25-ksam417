package edu.brown.cs.student.main.CSV.DataSource;

import edu.brown.cs.student.main.CSV.Parser.Parser;
import edu.brown.cs.student.main.CSV.Parser.RetListString;
import edu.brown.cs.student.main.CSV.Search;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Interfaces.CreatorFromRow;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class CSVDataSource<T> {

  private List CSVData;
  public boolean isLoaded;
  private String filepath;
  private Search searcher;

  public CSVDataSource(String filePath) {
    this.filepath = filePath;
  }

  public CSVDataSource() {}

  public boolean loadCSV(String filePath) throws Exception {
    this.filepath = filePath;
    try {
      Reader reader = new FileReader(this.filepath);
      // CreatorFromRow<List<String>> creator = new MakeList();
      CreatorFromRow<T> rowObject = new RetListString<T>();

      Parser<T> CSVParser = new Parser<>(rowObject, reader);
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

  public List getCSVData() throws FactoryFailureException, FileNotFoundException {
    return this.CSVData;
  }

  public boolean getLoadStatus() {
    return this.isLoaded;
  }
}