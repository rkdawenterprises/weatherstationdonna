package net.ddns.rkdawenterprises.davis_website;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Weather_data {
    int windDirection;
    Forecast_overview[] forecastOverview;
    String highAtStr;
    String loAtStr;
    String timeZoneId;
    String timeFormat;
    String barometerUnits;
    String windUnits;
    String rainUnits;
    String tempUnits;
    String temperatureFeelLike;
    String temperature;
    String hiTemp;
    String loTemp;
    String wind;
    String gust;
    long gustAt;
    String humidity;
    String rain;
    String seasonalRain;
    String barometer;
    String barometerTrend;
    long lastReceived;
    long hiTempDate;
    long loTempDate;

    public static final Gson m_GSON = new GsonBuilder().disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static String serialize_to_JSON( Weather_data object )
    {
        return m_GSON.toJson( object );
    }

    public static Weather_data deserialize_from_JSON( String string_JSON )
    {
        Weather_data object = null;
        try
        {
            object = m_GSON.fromJson( string_JSON,
                    Weather_data.class );
        }
        catch( com.google.gson.JsonSyntaxException exception )
        {
            System.out.println( "Bad data format for Weather_data: " + exception );
            System.out.println( ">>>" + string_JSON + "<<<" );
        }

        return object;
    }

    public String serialize_to_JSON()
    {
        return serialize_to_JSON( this );
    }
}
