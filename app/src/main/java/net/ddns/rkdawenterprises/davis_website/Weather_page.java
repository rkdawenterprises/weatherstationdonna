package net.ddns.rkdawenterprises.weatherstationdonna.davis_website;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Weather_page {
    String deviceId;
    String deviceUrlToken;
    String systemName;
    String gatewayType;
    String sensorType;
    String size;
    User_account_settings user_account_settings;

    public static final Gson m_GSON = new GsonBuilder().disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static String serialize_to_JSON( Weather_page object )
    {
        return m_GSON.toJson( object );
    }

    public static Weather_page deserialize_from_JSON( String string_JSON )
    {
        Weather_page object = null;
        try
        {
            object = m_GSON.fromJson( string_JSON,
                    Weather_page.class );
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
