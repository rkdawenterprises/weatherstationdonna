package net.ddns.rkdawenterprises.weatherstationdonna.network.davis_website;

import com.google.gson.Gson;

public class Data_parser
{
    public static Weather_data_container parse(String page, String json )
    {
        Gson gson = new Gson();
        Weather_data json_data = gson.fromJson( json, Weather_data.class );

        Weather_page page_data = scrape_page( page );

        return new Weather_data_container( page_data, json_data );
    }

    /**
     * Returns a new string that is the substring of the string, delimited by the two delimiter strings.
     * @param string The string to search.
     * @param start_delimiter The string that delimits the beginning of the substring. The returned substring
     *      will not contain this delimiter. If empty, then the beginning of the string will be the delimiter.
     * @param end_delimiter The string that delimits the end of the substring. The returned substring
     *      will not contain this delimiter. If empty, then the end of the string will be the delimiter.
     * @return The substring found between the two delimiters, or an empty string if either of the
     *      delimiters were not found.
     */
    static String get_delimited_substring( String string,
                                                  String start_delimiter,
                                                  String end_delimiter )
    {
        if( string.length() < 2 ) return "";

        int start = 0;
        if( start_delimiter.length() > 0 )
        {
            start = string.indexOf( start_delimiter );
            if( start == -1 ) return "";
            start = start + start_delimiter.length();
        }

        int end = string.length() - 1;
        int length = ( end - start ) + 1;
        if( end_delimiter.length() > 0 )
        {
            end = string.indexOf( end_delimiter, start );
            if( end == -1 ) return "";
            length = end - start;
        }

        if( length > 0 )
        {
            return string.substring( start, end );
        }
        else
        {
            return "";
        }
    }

    private static Weather_page scrape_page(String page )
    {
        String deviceId = get_delimited_substring( page, "wl._deviceId = \"", "\";" );
        String deviceUrlToken = get_delimited_substring( page, "wl._deviceUrlToken = \"", "\";" );
        String systemName = get_delimited_substring( page, "wl._systemName = \"", "\";" );
        String gatewayType = get_delimited_substring( page, "wl._gatewayType = \"", "\";" );
        String sensorType = get_delimited_substring( page, "wl._sensorType = \"", "\";" );
        String size = get_delimited_substring( page, "wl._size = \"", "\";" );

        String json = get_delimited_substring( page, "var userAccountSettings = ", ";\n" );
        Gson gson = new Gson();
        User_account_settings json_data = gson.fromJson( json, User_account_settings.class );

        Weather_page result = new Weather_page();
        result.deviceId = deviceId;
        result.deviceUrlToken = deviceUrlToken;
        result.systemName = systemName;
        result.gatewayType = gatewayType;
        result.sensorType = sensorType;
        result.size = size;
        result.user_account_settings = json_data;

        return result;
    }
}
