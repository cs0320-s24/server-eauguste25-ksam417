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

public abstract class BroadbandDataSource {

  private List<List<String>> stateCodes;

  public BroadbandDataSource() {
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
      if (stateCodes.isEmpty())
        throw new DatasourceException("Malformed response from NWS");
      return this.stateCodes;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * Method that synchronizes the query of the states.
   */
  private synchronized void ensureStateCodesInitialized() throws DatasourceException {
    if (this.stateCodes == null) {
      try {
        this.getStateCodes();
      } catch (DatasourceException e) {
        // Handle the exception (e.g., log it) or set a default value for stateCodes
        this.stateCodes = new ArrayList<>();
      }
=======
      private List<List<String>> stateCodes;

    public BroadbandDataSource() throws DatasourceException {
        this.ensureStateCodesInitialized();

      }

      /**
       * This method reads the JSON list of string containing state and code.
       * @return
       * @throws DatasourceException
       */
      private List<List<String>> getStateCodes () throws DatasourceException {
        try {
          URL requestURL = new URL("https", "api.census.gov",
              "/data/2010/dec/sf1?get=NAME&for=state:*");
          HttpURLConnection clientConnection = connect(requestURL);
          Moshi moshi = new Moshi.Builder().build();
          JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
          // NOTE: important! pattern for handling the input stream
          this.stateCodes = adapter.fromJson(
              new Buffer().readFrom(clientConnection.getInputStream()));
          clientConnection.disconnect();
          if (this.stateCodes.isEmpty())
            throw new DatasourceException("Malformed response from NWS");
          return this.stateCodes;
        } catch (IOException e) {
          throw new DatasourceException(e.getMessage());
        }
      }
    }

    /**
     * This is a set of the data with all counties from the same state
     *
     * @param stateCode
     * @return
     * @throws DatasourceException
     */
    public List<List<String>> returnStateCounties (String stateCode) throws DatasourceException {
      try {
        URL requestURL =
            new URL(
                "https",
                "api.census.gov",
                "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:"
                    + stateCode);
        HttpURLConnection clientConnection = connect(requestURL);
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
        // NOTE: important! pattern for handling the input stream
        List<List<String>> countyList =
            adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        clientConnection.disconnect();
        if (countyList.isEmpty()) {
          throw new DatasourceException("Malformed response from ACS");
        }
        return countyList;
      } catch (IOException e) {
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
    public String returnStateName (String stateName) throws DatasourceException {
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

    public String returnCountyCode (List < List < String >> countyList, String countyName)
      throws DatasourceException {
      String stateCode = "*";
      for (List<String> strings : countyList) {
        String name = strings.get(0);
        List<String> parts = Arrays.asList(name.split(","));
        if (parts.get(0).equals(countyName)) {
          stateCode = strings.get(3);
        }
      }
<<<<<<<HEAD
      if (!stateCode.equals("*")) {
        return stateCode;
      } else {
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
    public List<List<String>> getDataFromURL (URL requestURL) throws DatasourceException {
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
    public List<String> getInternetAccess (String county, String state) throws DatasourceException {
      try {
        String stateCode = this.returnStateName(state);
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

        if (result.isEmpty())
          throw new DatasourceException("Malformed response from ACS");
        return new ArrayList<>(result);

      } catch (IOException e) {
        throw new DatasourceException(e.getMessage());
      }
    }

    private static HttpURLConnection connect (URL requestURL) throws
    DatasourceException, IOException {
      URLConnection urlConnection = requestURL.openConnection();
      if (!(urlConnection instanceof HttpURLConnection))
        throw new DatasourceException("unexpected: result of connection wasn't HTTP");
      HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
      clientConnection.connect(); // GET
      if (clientConnection.getResponseCode() != 200) {
        throw new DatasourceException(
            "unexpected: API connection not success status "
                + clientConnection.getResponseMessage());
      }
      return clientConnection;
    }

    /**
     * This helper returns a list of list strings from the narrowed down list that we have of a specific county
     * @param requestURL
     * @return
     * @throws DatasourceException
     */
    public List<List<String>> getDataFromURL (URL requestURL) throws DatasourceException {
      HttpURLConnection connection = null;
      List<List<String>> data;

      try {
        connection = (HttpURLConnection) requestURL.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        StringBuilder jsonResponse = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          jsonResponse.append(line);
        }
        reader.close();

        // Use Moshi to parse the JSON response
        Moshi moshi = new Moshi.Builder().build();
        Type listType = Types.newParameterizedType(List.class,
            Types.newParameterizedType(List.class, String.class));
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
        // This is to ensure that we never return null. Throw an appropriate exception or return an empty list.
        throw new DatasourceException("Failed to parse JSON response");
      }
      return data;
    }

    /**
     * Returns a JSON of the bandwidth, date and time by calling on the helper methods in the class
     * @param county
     * @param state
     * @return a List that includes the bandwith of internet access for the queried state/county
     * @throws DatasourceException
     */
    public List<String> getInternetAccess (String county, String state) throws DatasourceException {
      try {
        String stateCode = this.returnStateName(state);
        String countyCode = this.returnCountyCode(this.returnStateCounties(stateCode), county);
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        URL requestURL = new URL("https", "api.census.gov",
            "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:" + countyCode
                + "&in=state:" + stateCode);
        List<List<String>> data = this.getDataFromURL(requestURL);

//            Moshi moshi = new Moshi.Builder().build();
//            JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
        List<String> result = new ArrayList<>();
        result.add("Band Width:" + data.get(1).get(1));
        result.add("State:" + state);
        result.add("County:" + county);
        result.add("Date:" + date);
        result.add("Current time:" + time);

        if (result.isEmpty())
          throw new DatasourceException("Malformed response from ACS");
        return new ArrayList<>(result);

      } catch (IOException e) {
        throw new DatasourceException(e.getMessage());
      }
    }

    private static HttpURLConnection connect (URL requestURL) throws
    DatasourceException, IOException {
      URLConnection urlConnection = requestURL.openConnection();
      if (!(urlConnection instanceof HttpURLConnection))
        throw new DatasourceException("unexpected: result of connection wasn't HTTP");
      HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
      clientConnection.connect(); // GET
      if (clientConnection.getResponseCode() != 200) {
        throw new DatasourceException(
            "unexpected: API connection not success status "
                + clientConnection.getResponseMessage());
      }
      return clientConnection;
    }

    public abstract List<String> getBandWidth (String county, String state) throws
    DatasourceException;
  }
}
