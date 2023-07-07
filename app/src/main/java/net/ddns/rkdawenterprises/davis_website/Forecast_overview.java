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

public class Forecast_overview {
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
