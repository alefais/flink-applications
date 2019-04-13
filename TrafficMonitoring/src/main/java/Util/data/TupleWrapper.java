package Util.data;

import java.io.Serializable;

/**
 * Wrapper for tuples sent between spout and Map-Match
 * bolt. We can use it since we use shuffleGrouping.
 *
 * See https://stackoverflow.com/questions/32053795/how-to-use-apache-storm-tuple
 */
public class TupleWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    // add more properties as necessary
    private String vehicleID;
    private double latitude;
    private double longitude;
    private int speed;
    private int bearing;
    private double min_lat;
    private double max_lat;
    private double min_lon;
    private double max_lon;

    public TupleWrapper(String id, double lat, double lon, int s, int b) {
        vehicleID = id;
        latitude = lat;
        longitude = lon;
        speed = s;
        bearing = b;
        min_lat = 39.689602;
        max_lat = 40.122410;
        min_lon = 116.105789;
        max_lon = 116.670021;
    }

    public TupleWrapper(TupleWrapper t) {
        vehicleID = t.vehicleID;
        latitude = t.latitude;
        longitude = t.longitude;
        speed = t.speed;
        bearing = t.bearing;
        min_lat = 39.689602;
        max_lat = 40.122410;
        min_lon = 116.105789;
        max_lon = 116.670021;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getSpeed() {
        return speed;
    }

    public int getBearing() {
        return bearing;
    }

    public double getMinLat() {
        return min_lat;
    }

    public double getMaxLat() {
        return max_lat;
    }

    public double getMinLon() {
        return min_lon;
    }

    public double getMaxLon() {
        return max_lon;
    }

    public void setMinLat(double value) {
        min_lat = value;
    }

    public void setMaxLat(double value) {
        max_lat = value;
    }

    public void setMinLon(double value) {
        min_lon = value;
    }

    public void setMaxLon(double value) {
        max_lon = value;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("VehicleID: ")
                .append(vehicleID)
                .append(" Latitude: ")
                .append(latitude)
                .append(" Longitude: ")
                .append(longitude)
                .append(" Speed: ")
                .append(speed)
                .append(" Bearing: ")
                .append(bearing)
                .append(" LatRange [")
                .append(min_lat)
                .append(", ")
                .append(max_lat)
                .append("] LonRange [")
                .append(min_lon)
                .append(", ")
                .append(max_lon)
                .append("]");
        return s.toString();
    }
}
