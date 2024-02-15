package edu.brown.cs.student.main.Interfaces;

import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.csv.Records.ACSData;
import edu.brown.cs.student.main.csv.Records.LocationData;

/*
Used to get the percentages from a specific county
 */
public interface ACSDataSource {

  ACSData getPercent(LocationData loc) throws DatasourceException;
}
