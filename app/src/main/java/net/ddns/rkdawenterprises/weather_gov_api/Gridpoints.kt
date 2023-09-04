/*
 * Copyright (c) 2023 RKDAW Enterprises and Ralph Williamson.
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

@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName",
               "PackageName",
               "unused")

package net.ddns.rkdawenterprises.weather_gov_api

import java.math.BigDecimal

class Gridpoints
{
    var context: List<String>? = null
    var id: String? = null
    var type: String? = null
    var geometry: Geometry? = null
    var properties: Properties? = null
}

class Geometry
{
    var type: String? = null
    var coordinates: List<List<List<BigDecimal>>>? = null
}

class Properties
{
    var id: String? = null
    var type: String? = null
    var updateTime: String? = null
    var validTimes: String? = null
    var elevation: Elevation? = null
    var forecastOffice: String? = null
    var gridId: String? = null
    var gridX: String? = null
    var gridY: String? = null
    var temperature: Temperature? = null
    var dewpoint: Dewpoint? = null
    var maxTemperature: MaxTemperature? = null
    var minTemperature: MinTemperature? = null
    var relativeHumidity: RelativeHumidity? = null
    var apparentTemperature: ApparentTemperature? = null
    var wetBulbGlobeTemperature: WetBulbGlobeTemperature? = null
    var heatIndex: HeatIndex? = null
    var windChill: WindChill? = null
    var skyCover: SkyCover? = null
    var windDirection: WindDirection? = null
    var windSpeed: WindSpeed? = null
    var windGust: WindGust? = null
    var weather: Weather? = null
    var hazards: Hazards? = null
    var probabilityOfPrecipitation: ProbabilityOfPrecipitation? = null
    var quantitativePrecipitation: QuantitativePrecipitation? = null
    var iceAccumulation: IceAccumulation? = null
    var snowfallAmount: SnowfallAmount? = null
    var snowLevel: SnowLevel? = null
    var ceilingHeight: CeilingHeight? = null
    var visibility: Visibility? = null
    var transportWindSpeed: TransportWindSpeed? = null
    var transportWindDirection: TransportWindDirection? = null
    var mixingHeight: MixingHeight? = null
    var hainesIndex: HainesIndex? = null
    var lightningActivityLevel: LightningActivityLevel? = null
    var twentyFootWindSpeed: TwentyFootWindSpeed? = null
    var twentyFootWindDirection: TwentyFootWindDirection? = null
    var waveHeight: WaveHeight? = null
    var wavePeriod: WavePeriod? = null
    var waveDirection: WaveDirection? = null
    var primarySwellHeight: PrimarySwellHeight? = null
    var primarySwellDirection: PrimarySwellDirection? = null
    var secondarySwellHeight: SecondarySwellHeight? = null
    var secondarySwellDirection: SecondarySwellDirection? = null
    var wavePeriod2: WavePeriod2? = null
    var windWaveHeight: WindWaveHeight? = null
    var dispersionIndex: DispersionIndex? = null
    var pressure: Pressure? = null
    var probabilityOfTropicalStormWinds: ProbabilityOfTropicalStormWinds? = null
    var probabilityOfHurricaneWinds: ProbabilityOfHurricaneWinds? = null
    var potentialOf15mphWinds: PotentialOf15mphWinds? = null
    var potentialOf25mphWinds: PotentialOf25mphWinds? = null
    var potentialOf35mphWinds: PotentialOf35mphWinds? = null
    var potentialOf45mphWinds: PotentialOf45mphWinds? = null
    var potentialOf20mphWindGusts: PotentialOf20mphWindGusts? = null
    var potentialOf30mphWindGusts: PotentialOf30mphWindGusts? = null
    var potentialOf40mphWindGusts: PotentialOf40mphWindGusts? = null
    var potentialOf50mphWindGusts: PotentialOf50mphWindGusts? = null
    var potentialOf60mphWindGusts: PotentialOf60mphWindGusts? = null
    var grasslandFireDangerIndex: GrasslandFireDangerIndex? = null
    var probabilityOfThunder: ProbabilityOfThunder? = null
    var davisStabilityIndex: DavisStabilityIndex? = null
    var atmosphericDispersionIndex: AtmosphericDispersionIndex? = null
    var lowVisibilityOccurrenceRiskIndex: LowVisibilityOccurrenceRiskIndex? = null
    var stability: Stability? = null
    var redFlagThreatIndex: RedFlagThreatIndex? = null
}

class Elevation
{
    var unitCode: String? = null
    var value: BigDecimal? = null
}

class Temperature
{
    var uom: String? = null
    var values: List<Value>? = null
}

class Dewpoint
{
    var uom: String? = null
    var values: List<Value>? = null
}

class MaxTemperature
{
    var uom: String? = null
    var values: List<Value>? = null
}

class MinTemperature
{
    var uom: String? = null
    var values: List<Value>? = null
}

class RelativeHumidity
{
    var uom: String? = null
    var values: List<Value>? = null
}

class ApparentTemperature
{
    var uom: String? = null
    var values: List<Value>? = null
}

class WetBulbGlobeTemperature
{
    var uom: String? = null
    var values: List<Value>? = null
}

class HeatIndex
{
    var uom: String? = null
    var values: List<Value>? = null
}

class WindChill
{
    var uom: String? = null
    var values: List<Value>? = null
}

class SkyCover
{
    var uom: String? = null
    var values: List<Value>? = null
}

class WindDirection
{
    var uom: String? = null
    var values: List<Value>? = null
}

class WindSpeed
{
    var uom: String? = null
    var values: List<Value>? = null
}

class WindGust
{
    var uom: String? = null
    var values: List<Value>? = null
}

class Weather
{
    var values: List<Value_weather>? = null
}

class Hazards
{
    var values: List<Value_hazards>? = null
}

class ProbabilityOfPrecipitation
{
    var uom: String? = null
    var values: List<Value>? = null
}

class QuantitativePrecipitation
{
    var uom: String? = null
    var values: List<Value>? = null
}

class IceAccumulation
{
    var uom: String? = null
    var values: List<Value>? = null
}

class SnowfallAmount
{
    var uom: String? = null
    var values: List<Value>? = null
}

class SnowLevel
{
    var values: List<Any>? = null
}

class CeilingHeight
{
    var uom: String? = null
    var values: List<Value>? = null
}

class Visibility
{
    var uom: String? = null
    var values: List<Value>? = null
}

class TransportWindSpeed
{
    var uom: String? = null
    var values: List<Value>? = null
}

class TransportWindDirection
{
    var uom: String? = null
    var values: List<Value>? = null
}

class MixingHeight
{
    var uom: String? = null
    var values: List<Value>? = null
}

class HainesIndex
{
    var values: List<Value>? = null
}

class LightningActivityLevel
{
    var values: List<Any>? = null
}

class TwentyFootWindSpeed
{
    var uom: String? = null
    var values: List<Value>? = null
}

class TwentyFootWindDirection
{
    var uom: String? = null
    var values: List<Value>? = null
}

class WaveHeight
{
    var values: List<Any>? = null
}

class WavePeriod
{
    var values: List<Any>? = null
}

class WaveDirection
{
    var values: List<Any>? = null
}

class PrimarySwellHeight
{
    var values: List<Any>? = null
}

class PrimarySwellDirection
{
    var values: List<Any>? = null
}

class SecondarySwellHeight
{
    var values: List<Any>? = null
}

class SecondarySwellDirection
{
    var values: List<Any>? = null
}

class WavePeriod2
{
    var values: List<Any>? = null
}

class WindWaveHeight
{
    var values: List<Any>? = null
}

class DispersionIndex
{
    var values: List<Any>? = null
}

class Pressure
{
    var values: List<Any>? = null
}

class ProbabilityOfTropicalStormWinds
{
    var values: List<Any>? = null
}

class ProbabilityOfHurricaneWinds
{
    var values: List<Any>? = null
}

class PotentialOf15mphWinds
{
    var values: List<Any>? = null
}

class PotentialOf20mphWindGusts
{
    var values: List<Any>? = null
}

class PotentialOf25mphWinds
{
    var values: List<Any>? = null
}

class PotentialOf30mphWindGusts
{
    var values: List<Any>? = null
}

class PotentialOf35mphWinds
{
    var values: List<Any>? = null
}

class PotentialOf40mphWindGusts
{
    var values: List<Any>? = null
}

class PotentialOf45mphWinds
{
    var values: List<Any>? = null
}

class PotentialOf50mphWindGusts
{
    var values: List<Any>? = null
}

class PotentialOf60mphWindGusts
{
    var values: List<Any>? = null
}

class GrasslandFireDangerIndex
{
    var values: List<Any>? = null
}

class ProbabilityOfThunder
{
    var values: List<Any>? = null
}

class DavisStabilityIndex
{
    var values: List<Value>? = null
}

class AtmosphericDispersionIndex
{
    var values: List<Any>? = null
}

class LowVisibilityOccurrenceRiskIndex
{
    var values: List<Value>? = null
}

class Stability
{
    var values: List<Value>? = null
}

class RedFlagThreatIndex
{
    var values: List<Any>? = null
}

class Value
{
    var validTime: String? = null
    var value: String? = null
}

class Value_weather
{
    var validTime: String? = null
    var value: List<Value>? = null

    class Value
    {
        var coverage: String? = null
        var weather: String? = null
        var intensity: String? = null
        var visibility: Visibility? = null
        var attributes: List<Any>? = null

        class Visibility
        {
            var unitCode: String? = null
            var value: Any? = null
        }
    }
}

class Value_hazards
{
    var validTime: String? = null
    var value: List<Value>? = null

    class Value
    {
        var phenomenon: String? = null
        var significance: String? = null
        var eventNumber: Any? = null
    }
}
