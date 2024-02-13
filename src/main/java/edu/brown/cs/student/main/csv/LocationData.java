package edu.brown.cs.student.main.csv;

public record LocationData(String state, String county) {

  public String toOurServerParams() {
    return "state=" + this.state + "county=" + this.county;
  }
}
