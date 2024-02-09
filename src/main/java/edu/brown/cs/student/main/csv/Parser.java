package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Interfaces.CreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Parser<T> {

  private CreatorFromRow<T> creator;
  private Reader reader;
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * This is the constructor for the developer for parsing the csv to the specified data type in the
   * parameter.
   *
   * @param creator the row object
   * @param reader the input file as a reader object
   */
  public Parser(CreatorFromRow<T> creator, Reader reader) {
    this.creator = creator;
    this.reader = reader;
  }

  /**
   * Constructor for parsing for the user that doesn't need the CreatorFromRow parameter
   *
   * @param reader the input file as a reader object
   */
  public Parser(Reader reader) {
    this.reader = reader;
  }

  /**
   * This method parses the csv into a list of list strings for the user
   *
   * @return a List of Lists of Strings representing the rows in a CSV file
   * @throws IOException
   */
  public List<List<String>> parseUser() throws IOException {
    List parsedCSVUser = new ArrayList<>();

    try (BufferedReader bufferedReader = new BufferedReader(this.reader)) {
      String line = bufferedReader.readLine();

      while (line != null) {
        try {
          String[] words = regexSplitCSVRow.split(line);
          List<String> wordList = List.of(words);
          parsedCSVUser.add(wordList);
        } catch (NullPointerException npe) {
          System.out.println("An error occurred, please try again.");
        }
        line = bufferedReader.readLine();
      }
    } catch (IOException e) {
      System.out.println("Sorry, an error occurred. Please try again.");
    }
    return parsedCSVUser;
  }

  /**
   * This method takes advantage of parsing the csv data for the user to provide the data as a list
   * of strings then changes each row into a type of generic type T
   *
   * @return a List of objects representing rows in a CSV file
   * @throws FactoryFailureException
   */
  public List<T> parseDeveloper() throws FactoryFailureException {
    if (this.creator == null) {
      throw new FactoryFailureException("CreatorFromRow is not initialized", null);
    }
    List<List<String>> userCSV = new ArrayList<>();
    try {
      userCSV = this.parseUser();
    } catch (IOException e) {
      System.out.println("Error reading CSV file: " + e.getMessage());
    }
    List<T> parsedCSVDeveloper = new ArrayList<>();

    for (List<String> row : userCSV) {
      T newRow = this.creator.create(row);
      parsedCSVDeveloper.add(newRow);
    }

    return parsedCSVDeveloper;
  }
}
