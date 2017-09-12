package com.blovvme.wundergroud.Model;

import java.util.List;

public class HourlyForecastOrdered {
    String label = "";
    List<HourlyForecast> hourlyForecastOrdered;


    public HourlyForecastOrdered(String label, List<HourlyForecast> hourlyForecastOrdered) {
        this.label = label;
        this.hourlyForecastOrdered = hourlyForecastOrdered;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<HourlyForecast> getHourlyForecastOrdered() {
        return hourlyForecastOrdered;
    }

    public void setHourlyForecastOrdered(List<HourlyForecast> hourlyForecastOrdered) {
        this.hourlyForecastOrdered = hourlyForecastOrdered;
    }
}
