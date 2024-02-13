package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.Exceptions.DatasourceException;

/*
Used to get the percentages from a specific county
 */
public interface ACSDataSource {

  ACSData getPercent(LocationData loc) throws DatasourceException;
}
