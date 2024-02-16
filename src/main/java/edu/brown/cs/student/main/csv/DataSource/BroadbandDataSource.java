package edu.brown.cs.student.main.CSV.DataSource;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import okio.Buffer;

public class BroadbandDataSource {
  private List<List<String>> stateCodes;

  public BroadbandDataSource() throws DatasourceException {
    this.ensureStateCodesInitialized();
  }

  /**
   * Ensures that the list of state codes is initialized and populated. This synchronized method
   * checks if the stateCodes list is null, indicating that it has not yet been initialized or
   * populated. If it is null, it attempts to fetch the state codes by calling the getStateCodes
   * method. If an exception occurs during the fetching process, it initializes stateCodes to an
   * empty list to ensure that the list is not null, preventing NullPointerExceptions in future uses
   * of stateCodes. This approach allows the class to handle failures gracefully by ensuring that
   * stateCodes is always in a valid state for use, even if it means having an empty list when data
   * retrieval fails.
   *
   * @throws DatasourceException if an error occurs while fetching the state codes. Note that if an
   *     exception is caught, it is handled by initializing stateCodes to an empty list, and no
   *     exception is thrown from the catch block. The original DatasourceException is not re-thrown
   *     or propagated outside of this method.
   */
  private synchronized void ensureStateCodesInitialized() throws DatasourceException {
    // Check if stateCodes is null, indicating that it has not been initialized.
    if (this.stateCodes == null) {
      try {
        // Attempt to fetch the state codes using the getStateCodes method.
        // This method itself may throw a DatasourceException if it encounters issues.
        this.getStateCodes();
      } catch (DatasourceException e) {
        // If fetching the state codes fails, initialize stateCodes to an empty list.
        // This ensures that stateCodes is never null, avoiding NullPointerExceptions
        // and allowing other methods to safely check the size or iterate over stateCodes
        // without additional null checks.
        this.stateCodes = new ArrayList<>();
      }
    }
  }

  /**
   * Fetches a list of U.S. states and their corresponding codes from a Census Bureau API endpoint.
   * This method constructs a URL to query the Census Bureau's API for a list of state names and
   * codes, leveraging the 2010 Decennial SF1 data. It uses the Moshi library to parse the JSON
   * response into a Java List of Lists, where each inner List contains two elements: the state name
   * and its code. This method assumes a stable internet connection and that the API endpoint
   * remains unchanged and available.
   *
   * @return A List of Lists, where each inner List contains a String for the state name and another
   *     for its code.
   * @throws DatasourceException if there's an error during the HTTP request or if the JSON response
   *     cannot be correctly parsed, indicating either a problem with the connection, the API, or
   *     the data format.
   */
  private List<List<String>> getStateCodes() throws DatasourceException {
    try {
      // Construct the URL for the Census API request targeting state names and codes.
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");

      // Open a connection to the specified URL using a helper method to configure and initiate the
      // HTTP request.
      HttpURLConnection clientConnection = connect(requestURL);

      // Initialize the Moshi JSON parser library with a new instance.
      Moshi moshi = new Moshi.Builder().build();

      // Create a JsonAdapter instance for parsing the JSON response into a List structure. The use
      // of .nonNull()
      // ensures that null values are not accepted, reducing the risk of NullPointerExceptions.
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();

      // Parse the JSON response from the API into the stateCodes list, ensuring data is read
      // correctly from the input stream.
      this.stateCodes = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

      // Properly disconnect the HTTP connection to free up network resources.
      clientConnection.disconnect();

      // Check if the stateCodes list is empty, which could indicate an issue with the API response,
      // and throw an exception.
      if (this.stateCodes.isEmpty()) throw new DatasourceException("Malformed response from NWS");

      // Return the successfully parsed list of state names and codes.
      return this.stateCodes;
    } catch (IOException e) {
      // Catch any IOExceptions thrown during the connection or reading process and wrap them in a
      // DatasourceException,
      // providing the caller with a clear indication that data retrieval failed.
      throw new DatasourceException(e.getMessage());
    }
  }

  public List<List<String>> returnStateCounties(String stateCode) throws DatasourceException {
    try {
      // Construct the URL for the API request using the specified state code.
      // This URL is for the 2021 ACS 1-year estimates, retrieving data for all counties within the
      // specified state.
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:"
                  + stateCode);

      // Establish a connection to the URL and return a HttpURLConnection object.
      HttpURLConnection clientConnection = connect(requestURL);

      // Initialize Moshi, a JSON library for Java, to handle JSON parsing.
      Moshi moshi = new Moshi.Builder().build();

      // Create a JsonAdapter for parsing the JSON response into a List of Lists.
      // The .nonNull() method ensures that the adapter will not accept null values.
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();

      // Use the JsonAdapter to parse the JSON response from the API into a List of Lists.
      // Each inner list represents a county's data, including its name and the specified variable
      // (S2802_C03_022E).
      List<List<String>> countyList =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

      // Disconnect the HttpURLConnection after the data has been retrieved.
      clientConnection.disconnect();

      // Check if the retrieved list of counties is empty, indicating a malformed response,
      // and throw a DatasourceException if it is.
      if (countyList.isEmpty()) {
        throw new DatasourceException("Malformed response from ACS");
      }

      // Return the list of counties if the response is valid and contains data.
      return countyList;
    } catch (IOException e) {
      // Catch any IOExceptions that occur during the URL connection setup,
      // data retrieval, or JSON parsing, and wrap them in a DatasourceException.
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * Searches for a given state name in the pre-fetched list of state names and codes, returning the
   * corresponding state code if found. This method iterates through the list of state codes
   * (assumed to be previously retrieved and stored in a class field) to find a match for the
   * provided state name. If a match is found, the corresponding state code is returned. If no match
   * is found, a DatasourceException is thrown indicating that the state name could not be found.
   *
   * @param stateName The name of the state for which the code is to be retrieved.
   * @return The code of the state if found.
   * @throws DatasourceException if the state name is not found in the list of state codes,
   *     indicating either an incomplete or incorrect dataset.
   */
  public String returnStateName(String stateName) throws DatasourceException {
    // Initialize a placeholder for the state code. '*' is used to indicate that no match has been
    // found yet.
    String stateCode = "*";

    // Iterate through the list of state codes. This list is assumed to be a class field
    // previously populated with state names and their corresponding codes.
    for (int i = 0; i < this.stateCodes.size(); i++) {
      // Check if the current entry's state name matches the provided state name.
      if (this.stateCodes.get(i).get(0).equals(stateName)) {
        // If a match is found, update stateCode with the corresponding state code.
        stateCode = this.stateCodes.get(i).get(1);
        // Once the match is found, there's no need to continue the loop.
        break; // This break statement optimizes the loop by exiting once a match is found.
      }
    }

    // Check if a state code was found (i.e., stateCode no longer holds the initial placeholder
    // value).
    if (!stateCode.equals("*")) {
      // Return the found state code.
      return stateCode;
    } else {
      // If no state code was found (stateCode is still '*'), throw an exception indicating the
      // state was not found.
      throw new DatasourceException("State not found");
    }
  }

  // This method attempts to find a county's code by its name from a provided list of county
  // information.
  // Each entry in the countyList is expected to be a list of strings containing county details.
  public String returnCountyCode(List<List<String>> countyList, String countyName)
      throws DatasourceException {
    // Initializes a default stateCode value to "*" which will be used to indicate if the county's
    // code has been found.
    String stateCode = "*";

    // Iterate over each list (representing a county's information) within the countyList.
    for (List<String> strings : countyList) {
      // Extracts the full name (which may include the state and other details, separated by commas)
      // of the county.
      String name = strings.get(0);
      // Splits the name by comma to separate components like county name, state, etc., and stores
      // them in a list.
      List<String> parts = Arrays.asList(name.split(","));
      // Check if the first part (assumed to be the county name) matches the countyName argument.
      if (parts.get(0).equals(countyName)) {
        // If a match is found, updates stateCode with the code from the current list,
        // assuming the code is located at index 3.
        stateCode = strings.get(3);
      }
    }
    // After the loop, checks if stateCode was updated from its initial value.
    if (!stateCode.equals("*")) {
      // If stateCode has a valid value (not "*"), returns it as the county's code.
      return stateCode;
    } else {
      // If stateCode remains "*", it indicates that no matching county was found in the list,
      // and thus throws a DatasourceException indicating that the county was not found.
      throw new DatasourceException("County not found");
    }
  }

  /**
   * This helper returns a list of list strings from the narrowed down list that we have of a
   * specific county
   *
   * @param requestURL
   * @return
   * @throws DatasourceException
   */
  public List<List<String>> getDataFromURL(URL requestURL) throws DatasourceException {
    HttpURLConnection connection = null;
    List<List<String>> data;

    try {
      connection = (HttpURLConnection) requestURL.openConnection();
      connection.setRequestMethod("GET");

      BufferedReader reader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder jsonResponse = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        jsonResponse.append(line);
      }
      reader.close();

      // Use Moshi to parse the JSON response
      Moshi moshi = new Moshi.Builder().build();
      Type listType =
          Types.newParameterizedType(
              List.class, Types.newParameterizedType(List.class, String.class));
      JsonAdapter<List<List<String>>> jsonAdapter = moshi.adapter(listType);

      data = jsonAdapter.fromJson(jsonResponse.toString());

    } catch (IOException e) {
      // Handle exceptions properly here
      throw new DatasourceException("Failed to get data from URL: " + e.getMessage());
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    if (data == null) {
      // This is to ensure that we never return null. Throw an appropriate exception or return an
      // empty list.
      throw new DatasourceException("Failed to parse JSON response");
    }

    return data;
  }

  /**
   * Returns a JSON of the bandwidth, date and time by calling on the helper methods in the class
   *
   * @param county
   * @param state
   * @return a List that includes the bandwith of internet access for the queried state/county
   * @throws DatasourceException
   */
  public List<String> getInternetAccess(String state, String county) throws DatasourceException {
    try {
      System.out.println("heeeee");
      System.out.println(county);
      System.out.println(state);
      String stateCode = this.returnStateName(state);
      System.out.println(stateCode);
      System.out.println(this.returnStateCounties(stateCode));
      String countyCode = this.returnCountyCode(this.returnStateCounties(stateCode), county);
      LocalDate date = LocalDate.now();
      LocalTime time = LocalTime.now();
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                  + countyCode
                  + "&in=state:"
                  + stateCode);
      List<List<String>> data = this.getDataFromURL(requestURL);

      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      List<String> result = new ArrayList<>();
      result.add("Band Width:" + data.get(1).get(1));
      result.add("State:" + state);
      result.add("County:" + county);
      result.add("Date:" + date);
      result.add("Current time:" + time);

      if (result.isEmpty()) throw new DatasourceException("Malformed response from ACS");
      return new ArrayList<>(result);

    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  // Attempts to establish a connection to a specified URL and ensures it's an HTTP connection.
  // This method is designed to be used internally and is static, meaning it can be called without
  // an instance of the class.
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    // Opens a connection to the specified URL and stores it in urlConnection.
    URLConnection urlConnection = requestURL.openConnection();
    // Checks if the opened connection is an instance of HttpURLConnection.
    // If not, it throws a DatasourceException indicating that the expected HTTP connection type was
    // not received.
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    // Casts the URLConnection to HttpURLConnection after confirming its instance type.
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    // Initiates the connection to the URL. This line is essentially performing a GET request.
    clientConnection.connect();
    // Checks the response code of the HTTP connection to ensure it is successful (HTTP 200 OK).
    // If the response code is not 200, it throws a DatasourceException with the HTTP error message.
    if (clientConnection.getResponseCode() != 200) {
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    }
    // Returns the established HttpURLConnection if successful, allowing for further operations such
    // as reading the response.
    return clientConnection;
  }
}
