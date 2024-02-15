package edu.brown.cs.student.main.CSV.Parser;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Interfaces.CreatorFromRow;
import java.util.LinkedList;
import java.util.List;

public class RetListString<T> implements CreatorFromRow {

  /**
   * Helper function for creating a CreatorFromRow Object for input in CSVParser methods
   *
   * @param row a List representing a row
   * @return a List of Strings
   */
  @Override
  public List<String> create(List row) throws FactoryFailureException {
    List<String> newListString = new LinkedList<>();
    for (Object R : row) {
      newListString.add(R.toString());
    }
    return newListString;
  }
}
