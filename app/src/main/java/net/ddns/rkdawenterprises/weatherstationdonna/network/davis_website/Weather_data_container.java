package net.ddns.rkdawenterprises.weatherstationdonna.network.davis_website;

public class Weather_data_container {
    public final Weather_page page_data;
    public final Weather_data json_data;

    Weather_data_container(Weather_page page, Weather_data data) {
        page_data = page;
        json_data = data;
    }
}
