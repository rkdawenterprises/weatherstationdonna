package net.ddns.rkdawenterprises.davis_website;

class Forecast_overview {
    String date;
    Overview morning;
    Overview afternoon;
    Overview evening;
    Overview night;

    Overview get_overview_for_string(String part_of_day) {
        switch (part_of_day) {
            case "morning":
                return morning;
            case "afternoon":
                return afternoon;
            case "evening":
                return evening;
            case "night":
                return night;
            default:
                throw new IllegalArgumentException("Invalid part of the day: " + part_of_day);
        }
    }
}
