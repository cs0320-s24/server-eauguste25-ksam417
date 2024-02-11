package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.Interfaces.CreatorFromRow;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Helps handle the data creation for parsing CSV rows and returning a list of trimmed strings. */
public class MakeList implements CreatorFromRow<List<String>> {

  /**
   * Parses a CSV row, trims each string value, and returns a list of trimmed strings.
   *
   * @param row The CSV row to parse.
   * @return A list of trimmed string values from the CSV row.
   */
  @Override
  public List<String> create(List<String> row) {
    List<String> trimmedList = new ArrayList<>();
    for (int i = 0; i < row.size(); i++) {
      String trimmed = row.get(i).trim();
      trimmedList.add(trimmed);
    }
    // Remove quotations from the parsed list so that JSON doesn't double quote it
    List<String> modifiedList =
        trimmedList.stream().map(item -> item.replaceAll("[\"']", "")).collect(Collectors.toList());
    return modifiedList;
  }
}
