package edu.brown.cs.student.main.csv;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;

/**
 * This class shows a possible implementation of deserializing JSON from the BoredAPI into an
 * Activity.
 */
public class CSVAPIUtilities {

  /**
   * Deserializes JSON from the BoredAPI into an Activity object.
   *
   * @param jsonCSV
   * @return
   */
  public static CSVDataSource deserializeActivity(String jsonCSV) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to a CSV class then uses it to parse the JSON.
      JsonAdapter<CSVDataSource> adapter = moshi.adapter(CSVDataSource.class);

      CSVDataSource source = adapter.fromJson(jsonCSV);

      return source;
    }
    // Returns an empty DataSource...
    catch (IOException e) {
      e.printStackTrace();
      return new CSVDataSource();
    }
  }
}
