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
import com.google.gson.JsonSyntaxException;

public class Weather_page {
    public String deviceId;
    public String deviceUrlToken;
    public String systemName;
    public String gatewayType;
    public String sensorType;
    public String size;
    public User_account_settings user_account_settings;

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
    public static String get_delimited_substring( String string,
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

    public static Weather_page scrape_page(String page )
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
        catch( JsonSyntaxException exception )
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
