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


  public CSVDataSource() {
  }

  /**
   * Loads CSV data from a specified file path into this object.
   * This method attempts to read and parse the CSV file located at the given filePath.
   * It uses a generic Parser to convert the CSV rows into objects of type T, storing the results
   * in this.CSVData. If successful, it sets isLoaded to true and returns true. If the file cannot
   * be read or parsed, it throws an exception. A FileNotFoundException is thrown if the file
   * does not exist or cannot be accessed, providing a more specific error message than the generic
   * Exception might.
   *
   * @param filePath The path to the CSV file to be loaded.
   * @return true if the CSV data is successfully loaded, false otherwise.
   * @throws Exception if the file does not exist or an error occurs during reading/parsing.
   * @throws FileNotFoundException if the file cannot be found or accessed, with a specific error message.
   */
  public boolean loadCSV(String filePath) throws Exception {
    try {
      Reader reader = new FileReader(filePath); // Attempt to open the file
      // Generic parser setup to convert CSV rows into type T objects
      CreatorFromRow<T> rowObject = new RetListString<T>();
      Parser<T> CSVParser = new Parser<>(rowObject, reader); // Initialize the parser
      this.CSVData = CSVParser.parse(); // Parse the file and store the data
      this.isLoaded = true; // Mark as successfully loaded
      return true; // Return success
    } catch (Exception e) { // Catch parsing or file reading exceptions
      if (this.CSVData == null) { // If no data was loaded
        throw new Exception("File does not exist"); // Throw a general exception
      }
      // Throw a more specific file not found exception with the original error message
      throw new FileNotFoundException(e.getMessage());
    }
  }

  /**
   * Retrieves the CSV data stored within this object.
   * This method provides access to the CSV data that has previously been loaded or set within this
   * instance. It's designed to return a List representing the CSV data, which could be rows of CSV
   * parsed into a suitable list format. It is important that the CSV data is initialized and loaded
   * before this method is called to avoid returning null or an uninitialized object.
   *
   * @return A List representing the loaded CSV data. The structure of this List will depend on how
   *         the CSV data was parsed and stored. For example, it might be a List of String arrays,
   *         where each array represents a row of CSV data.
   * @throws FactoryFailureException if there is an issue with the underlying data factory mechanism,
   *         indicating that the CSV data might not have been properly initialized or loaded.
   * @throws FileNotFoundException if the CSV data file was not found or could not be accessed,
   *         indicating that the data has not been loaded into this instance.
   */
  public List getCSVData() throws FactoryFailureException, FileNotFoundException {
    return this.CSVData;
  }


  /**
   * Checks if the data has been successfully loaded.
   * This method returns the load status of the data, indicating whether the data
   * (e.g., CSV data, configuration settings, or other resources) has been successfully
   * loaded into the object. The load status is represented by a boolean value, where
   * true indicates that the data is loaded and false indicates that it is not.
   *
   * @return A boolean value representing the load status of the data. True if the
   * data has been successfully loaded, false otherwise.
   */
  public boolean getLoadStatus() {
    return this.isLoaded;
  }
}
