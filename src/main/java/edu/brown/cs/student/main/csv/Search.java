package edu.brown.cs.student.main.csv;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Search<T> {

  private List<List<String>> parsedDataUser;
  private String columnIdentifier;
  private String searchTerm;
  private int columnIndex;

  /**
   * This constructor is when the user provides us with a column identifier
   *
   * @param parsedData the parsed data from the csv file
   * @param searchTerm the term being searched for in the csv file
   * @param columnIdentifier the identifier representing a specific column in the csv file
   */
  public Search(List<List<String>> parsedData, String searchTerm, String columnIdentifier)
      throws FileNotFoundException {
    this.parsedDataUser = parsedData;
    this.columnIdentifier = columnIdentifier;
    this.searchTerm = searchTerm;
    if (parsedData == null || parsedData.isEmpty()) {
      System.out.println("An error occurred, please try again.");
    }
  }

  /**
   * This constructor is used when the user provides us with no column identifier (only providing a
   * search term)
   *
   * @param parsedData the parsed data from the csv file
   * @param searchTerm the term being searched for in the csv file
   */
  public Search(List<List<String>> parsedData, String searchTerm) throws FileNotFoundException {
    this.parsedDataUser = parsedData;
    this.searchTerm = searchTerm;
    if (parsedData == null || parsedData.isEmpty()) {
      System.out.println("An error occurred, please try again.");
    }
  }

  /**
   * Constructor for searching by column index
   *
   * @param parsedData the parsed data from the csv file
   * @param searchTerm the term being searched for in the csv file
   * @param columnIndex the index representing a specific column in the csv file
   */
  public Search(List<List<String>> parsedData, String searchTerm, int columnIndex)
      throws FileNotFoundException {
    this.parsedDataUser = parsedData;
    this.searchTerm = searchTerm;
    this.columnIndex = columnIndex;
    if (parsedData == null || parsedData.isEmpty()) {
      System.out.println("An error occurred, please try again.");
    }
  }

  /**
   * This search method narrows down the search to a single column. If the single column to be
   * searched does not exist, search the rest of the csv file for any matching rows
   *
   * @return A list of lists of strings representing the rows that have the matching value being
   *     searched for
   */
  public List<List<String>> search() {
    List<List<String>> result = new ArrayList<>();
    // this loops through to find the index of the passed in column identifier
    if (columnIdentifier != null) {
      List<String> header = this.parsedDataUser.get(0);
      int column = 0;
      for (int i = 0; i < header.size(); i++) {
        // searching the header row for the columnIdentifier
        if (header
            .get(i)
            .toLowerCase()
            .strip()
            .equals(this.columnIdentifier.toLowerCase().strip())) {
          column = i;
        }
      }
      // this loops through to check the object only at that specific index
      for (List<String> row : this.parsedDataUser) {
        String word = row.get(column).toLowerCase().strip().replaceAll("[\"']", "");
        if (word.equals(this.searchTerm.toLowerCase().strip().replaceAll("[\"']", ""))) {
          result.add(row);
        }
      }
    } else if (columnIndex >= 0 && columnIndex < parsedDataUser.get(0).size()) {
      // Search by column index if there is no matching column Identifier
      int column = columnIndex;

      for (List<String> row : this.parsedDataUser) {
        String word = row.get(column).toLowerCase().strip().replaceAll("[\"']", "");

        if (word.equals(this.searchTerm.toLowerCase().strip().replaceAll("[\"']", ""))) {
          result.add(row);
        }
      }
    } else {
      // if the column index was not valid
      System.out.println("Invalid column index specified.");
    }

    // If no match found in the specified column, search all columns
    if (result.isEmpty()) {
      for (List<String> row : this.parsedDataUser) {
        boolean matchFound = false;

        for (String word : row) {
          word = word.toLowerCase().strip().replaceAll("[\"']", "");

          if (word.equals(this.searchTerm.toLowerCase().strip().replaceAll("[\"']", ""))) {
            matchFound = true;
            break;
          }
        }
        if (matchFound) {
          // add the row to the result list if it is matching the search term
          result.add(row);
        }
      }
    }
    // if there are no matching rows
    if (result.isEmpty()) {
      System.out.println(
          "The search term: "
              + "'"
              + this.searchTerm
              + "'"
              + " was not found in the parsed data. Please try again.");
    }
    return result;
  }
}
