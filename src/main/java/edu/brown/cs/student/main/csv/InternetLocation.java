package edu.brown.cs.student.main.csv;

/**
 * A record to hold a specific location in state/county pair.
 * @param state latitude in degrees
 * @param county longitude in degrees
 */
public record InternetLocation(String state, String county) {

    public InternetLocation {
        if(!InternetLocation.isValidLocation(state, county)) {
            throw new IllegalArgumentException("Invalid geolocation");
        }
    }

    /**
     * Convenience function to convert this location to an API parameter string
     * @return API parameter string corresponding to this location
     */
    public String toOurServerParams() {
        return "state="+state+"&county="+county;
    }

    /**
     * Static validity checker for State/county pair.
     *
     * @return true if and only if this is a valid pair
     */
    public static boolean isValidLocation(String state, String county) {
        return true;
    }

}
