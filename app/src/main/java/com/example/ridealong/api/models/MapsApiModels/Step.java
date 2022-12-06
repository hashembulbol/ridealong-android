
package com.example.ridealong.api.models.MapsApiModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {

    @SerializedName("distance")
    @Expose
    private Distance__1 distance;
    @SerializedName("duration")
    @Expose
    private Duration__1 duration;
    @SerializedName("end_location")
    @Expose
    private EndLocation__1 endLocation;
    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;
    @SerializedName("polyline")
    @Expose
    private Polyline polyline;
    @SerializedName("start_location")
    @Expose
    private StartLocation__1 startLocation;
    @SerializedName("travel_mode")
    @Expose
    private String travelMode;
    @SerializedName("maneuver")
    @Expose
    private String maneuver;

    public Distance__1 getDistance() {
        return distance;
    }

    public void setDistance(Distance__1 distance) {
        this.distance = distance;
    }

    public Duration__1 getDuration() {
        return duration;
    }

    public void setDuration(Duration__1 duration) {
        this.duration = duration;
    }

    public EndLocation__1 getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(EndLocation__1 endLocation) {
        this.endLocation = endLocation;
    }

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public StartLocation__1 getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(StartLocation__1 startLocation) {
        this.startLocation = startLocation;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Step.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("distance");
        sb.append('=');
        sb.append(((this.distance == null)?"<null>":this.distance));
        sb.append(',');
        sb.append("duration");
        sb.append('=');
        sb.append(((this.duration == null)?"<null>":this.duration));
        sb.append(',');
        sb.append("endLocation");
        sb.append('=');
        sb.append(((this.endLocation == null)?"<null>":this.endLocation));
        sb.append(',');
        sb.append("htmlInstructions");
        sb.append('=');
        sb.append(((this.htmlInstructions == null)?"<null>":this.htmlInstructions));
        sb.append(',');
        sb.append("polyline");
        sb.append('=');
        sb.append(((this.polyline == null)?"<null>":this.polyline));
        sb.append(',');
        sb.append("startLocation");
        sb.append('=');
        sb.append(((this.startLocation == null)?"<null>":this.startLocation));
        sb.append(',');
        sb.append("travelMode");
        sb.append('=');
        sb.append(((this.travelMode == null)?"<null>":this.travelMode));
        sb.append(',');
        sb.append("maneuver");
        sb.append('=');
        sb.append(((this.maneuver == null)?"<null>":this.maneuver));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
