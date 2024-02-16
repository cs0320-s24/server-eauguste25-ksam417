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

  private synchronized void ensureStateCodesInitialized() throws DatasourceException {
    if (this.stateCodes == null) {
      try {
        this.getStateCodes();
      } catch (DatasourceException e) {
        // Handle the exception (e.g., log it) or set a default value for stateCodes
        this.stateCodes = new ArrayList<>();
      }
    }
  }

  /**
   * This method reads the JSON list of string containing state and code.
   *
   * @return
   * @throws DatasourceException
   */
  private List<List<String>> getStateCodes() throws DatasourceException {
    try {
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      // NOTE: important! pattern for handling the input stream
      this.stateCodes = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (this.stateCodes.isEmpty()) throw new DatasourceException("Malformed response from NWS");
      return this.stateCodes;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  public List<List<String>> returnStateCounties(String stateCode) throws DatasourceException {
    try {
      // Construct the URL for the API request using the specified state code.
      // This URL is for the 2021 ACS 1-year estimates, retrieving data for all counties within the specified state.
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
      // Each inner list represents a county's data, including its name and the specified variable (S2802_C03_022E).
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
   * This method takes the stateName to search through the api for state to stateCode.
   *
   * @param stateName
   * @return
   * @throws DatasourceException
   */
  public String returnStateName(String stateName) throws DatasourceException {
    String stateCode = "*";
    for (int i = 0; i < this.stateCodes.size(); i++) {
      if (this.stateCodes.get(i).get(0).equals(stateName)) {
        stateCode = this.stateCodes.get(i).get(1);
      }
    }
    if (stateCode != "*") {
      return stateCode;
    } else {
      throw new DatasourceException("State not found");
    }
  }

  // This method attempts to find a county's code by its name from a provided list of county information.
// Each entry in the countyList is expected to be a list of strings containing county details.
  public String returnCountyCode(List<List<String>> countyList, String countyName)
          throws DatasourceException {
    // Initializes a default stateCode value to "*" which will be used to indicate if the county's code has been found.
    String stateCode = "*";

    // Iterate over each list (representing a county's information) within the countyList.
    for (List<String> strings : countyList) {
      // Extracts the full name (which may include the state and other details, separated by commas) of the county.
      String name = strings.get(0);
      // Splits the name by comma to separate components like county name, state, etc., and stores them in a list.
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
// This method is designed to be used internally and is static, meaning it can be called without an instance of the class.
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    // Opens a connection to the specified URL and stores it in urlConnection.
    URLConnection urlConnection = requestURL.openConnection();
    // Checks if the opened connection is an instance of HttpURLConnection.
    // If not, it throws a DatasourceException indicating that the expected HTTP connection type was not received.
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
    // Returns the established HttpURLConnection if successful, allowing for further operations such as reading the response.
    return clientConnection;
  }
}
