/*
 * Copyright (c) 2019-2023 RKDAW Enterprises and Ralph Williamson.
 *       email: rkdawenterprises@gmail.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ddns.rkdawenterprises.davis_website;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class Weather_data {
    public int windDirection;
    public Forecast_overview[] forecastOverview;
    public String highAtStr;
    public String loAtStr;
    public String timeZoneId;
    public String timeFormat;
    public String barometerUnits;
    public String windUnits;
    public String rainUnits;
    public String tempUnits;
    public String temperatureFeelLike;
    public String temperature;
    public String hiTemp;
    public String loTemp;
    public String wind;
    public String gust;
    public long gustAt;
    public String humidity;
    public String rain;
    public String seasonalRain;
    public String barometer;
    public String barometerTrend;
    public long lastReceived;
    public long hiTempDate;
    public long loTempDate;

    public static String get_part_of_day(int the_hour) {
        if ((the_hour >= 5) && (the_hour < 12)) {
            return "morning";
        } else if ((the_hour >= 12) && (the_hour < 17)) {
            return "afternoon";
        } else if ((the_hour >= 17) && (the_hour < 21)) {
            return "evening";
        } else if (((the_hour >= 21) && (the_hour < 24)) || ((the_hour >= 0) && (the_hour < 5))) {
            return "night";
        } else {
            throw new IllegalArgumentException("Invalid hour of the day: " + the_hour);
        }
    }

    public static String get_forecast_icon_uri_for_date(ZonedDateTime zoned_date_time_local,
                                                        Forecast_overview[] forecast_overviews) {
        String part_of_day = get_part_of_day(zoned_date_time_local.getHour());
        String local_date_time_formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(zoned_date_time_local);

        for (Forecast_overview fo : forecast_overviews) {
            if (fo.date.equals(local_date_time_formatted)) {
                return fo.get_overview_for_string(part_of_day).weatherIconUrl;
            }
        }

        throw new IllegalArgumentException("Could not find forecast overview for: " + part_of_day + " of " + local_date_time_formatted);
    }

    public static final Gson m_GSON = new GsonBuilder().disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static String serialize_to_JSON(Weather_data object) {
        return m_GSON.toJson(object);
    }

    public static Weather_data deserialize_from_JSON(String string_JSON) {
        Weather_data object = null;
        try {
            object = m_GSON.fromJson(string_JSON,
                    Weather_data.class);
        } catch (com.google.gson.JsonSyntaxException exception) {
            System.out.println("Bad data format for Weather_data: " + exception);
            System.out.println(">>>" + string_JSON + "<<<");
        }

        return object;
    }

    public String serialize_to_JSON() {
        return serialize_to_JSON(this);
    }
}
