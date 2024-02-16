package edu.brown.cs.student.main.CSV.Mock;

import edu.brown.cs.student.main.CSV.DataSource.BroadbandDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.Data;

public class MockDataSource1 extends BroadbandDataSource {

  private List<String> constantData;

  public MockDataSource1() throws DatasourceException {
  }

  @Override
  public List<String> getInternetAccess(String county, String state) throws DatasourceException {

      this.constantData = new ArrayList<>();
      constantData.add("Band Width:" + 93.4);
      constantData.add("State:" + state);
      constantData.add("County:" + county);
      constantData.add("Date:" + LocalDate.now());
      // constantData.add("Current time:" + LocalTime.now());

      return constantData;
    }

}
