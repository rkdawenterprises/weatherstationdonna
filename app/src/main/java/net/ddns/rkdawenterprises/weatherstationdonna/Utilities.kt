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
               "UnnecessaryVariable")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.content.Context
import android.content.res.Configuration
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import org.threeten.extra.PeriodDuration
import kotlin.time.toKotlinDuration
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

@Throws(IllegalArgumentException::class,
        JsonProcessingException::class)
fun serialize_object(name: String? = null,
                     instance: Any?): String
{
    val mapper = ObjectMapper().registerModule(JavaTimeModule());
    val root_node: ObjectNode = mapper.createObjectNode();
    if(instance == null)
    {
        root_node.put(name,
                      "null");
    }
    else
    {
        root_node.set(name
                          ?: (instance.javaClass.name),
                      mapper.valueToTree(instance)) as ObjectNode;
    }

    return mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(root_node);
}

fun round_to_multiple_of(value: BigDecimal,
                         multiple: Int,
                         mode: RoundingMode = RoundingMode.UP): Int
{
    val big_multiple = BigDecimal(multiple);
    val half_big_multiple = big_multiple.divide(BigDecimal("2.0"),
                                                4,
                                                RoundingMode.HALF_EVEN);
    val modulus = value.remainder(big_multiple)
            .abs();
    val big_muliple_minus_modulus = big_multiple.minus(modulus);

    if(modulus.compareTo(BigDecimal.ZERO) == 0)
    {
        return value.toInt();
    }

    return if(value.signum() > 0)
    {
        when(mode)
        {
            //  Away from zero, or towards positive infinity.
            RoundingMode.UP, RoundingMode.CEILING -> (value.plus(big_muliple_minus_modulus)).toInt();

            // Towards zero, or towards negative infinity.
            RoundingMode.DOWN, RoundingMode.FLOOR -> (value.minus(modulus)).toInt();

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round away from zero.
            RoundingMode.HALF_UP ->
            {
                val x = if(modulus < half_big_multiple)
                {
                    value.minus(modulus)
                            .toInt();
                }
                else
                {
                    value.plus(big_multiple.minus(modulus))
                            .toInt();
                }

                x;
            }

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round towards zero.
            RoundingMode.HALF_DOWN ->
            {
                val x = if(modulus <= half_big_multiple)
                {
                    value.minus(modulus)
                            .toInt();
                }
                else
                {
                    value.plus(big_multiple.minus(modulus))
                            .toInt();
                }

                x;
            }

            // Towards the "nearest neighbor" unless both neighbors are equidistant, in which case round towards the even neighbor.
            RoundingMode.HALF_EVEN ->
            {
                val rounded_up = value.plus(big_multiple.minus(modulus))
                        .toInt();
                val rounded_down = value.minus(modulus)
                        .toInt();
                val round_down_is_even = rounded_down % 2 == 0;
                val round_up_is_even = rounded_up % 2 == 0;

                val x = if((modulus.compareTo(half_big_multiple) == 0) && (round_down_is_even))
                {
                    rounded_down;
                }
                else if((modulus.compareTo(half_big_multiple) == 0) && (round_up_is_even))
                {
                    rounded_up;
                }
                else if(modulus < half_big_multiple)
                {
                    rounded_down;
                }
                else
                {
                    rounded_up;
                }

                x;
            }

            else ->
            {
                throw (ArithmeticException());
            }
        }
    }
    else
    {
        when(mode)
        {
            //  Towards zero, or towards positive infinity.
            RoundingMode.DOWN, RoundingMode.CEILING -> (value.plus(modulus)).toInt();

            // Away from zero, or towards negative infinity.
            RoundingMode.UP, RoundingMode.FLOOR -> (value.minus(big_muliple_minus_modulus)).toInt();

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round away from zero.
            RoundingMode.HALF_UP ->
            {
                val x = if(modulus < half_big_multiple)
                {
                    value.plus(modulus)
                            .toInt();
                }
                else
                {
                    value.minus(big_multiple.minus(modulus))
                            .toInt();
                }

                x;
            }

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round towards zero.
            RoundingMode.HALF_DOWN ->
            {
                val x = if(modulus <= half_big_multiple)
                {
                    value.plus(modulus)
                            .toInt();
                }
                else
                {
                    value.minus(big_multiple.minus(modulus))
                            .toInt();
                }

                x;
            }

            // Towards the "nearest neighbor" unless both neighbors are equidistant, in which case round towards the even neighbor.
            RoundingMode.HALF_EVEN ->
            {
                val rounded_down = value.plus(modulus)
                        .toInt();
                val rounded_up = value.minus(big_multiple.minus(modulus))
                        .toInt();
                val round_down_is_even = rounded_down % 2 == 0;
                val round_up_is_even = rounded_up % 2 == 0;

                val x = if((modulus.compareTo(half_big_multiple) == 0) && (round_down_is_even))
                {
                    rounded_down;
                }
                else if((modulus.compareTo(half_big_multiple) == 0) && (round_up_is_even))
                {
                    rounded_up;
                }
                else if(modulus < half_big_multiple)
                {
                    rounded_down;
                }
                else
                {
                    rounded_up;
                }

                x;
            }

            else ->
            {
                throw (ArithmeticException());
            }
        }
    }
}

fun convert_time_to_local(time: ZonedDateTime): ZonedDateTime
{
    return time.withZoneSameInstant(ZoneId.of(TimeZone.getDefault()
                                                      .getID())
                                            .normalized())
}

fun convert_time_to_local_string(time: ZonedDateTime,
                                 pattern: String?): String
{
    return DateTimeFormatter.ofPattern(pattern)
            .format(convert_time_to_local(time))
}


const val BIG_DECIMAL_SCALE_DEFAULT = 4;

val BIG_DECIMAL_ZERO: BigDecimal = BigDecimal.ZERO.setScale(BIG_DECIMAL_SCALE_DEFAULT);

data class Size_BD(val width: BigDecimal,
                   val height: BigDecimal)
{
    fun to_size(): Size
    {
        return Size(width = this.width.toFloat(),
                    height = this.height.toFloat());
    }
}

@Composable
fun Dp.to_float(): Float =
    with(LocalDensity.current) { this@to_float.toPx() }

@Composable
fun Dp.to_big_decimal(scale: Int = BIG_DECIMAL_SCALE_DEFAULT): BigDecimal =
    with(LocalDensity.current) {
        this@to_big_decimal.toPx()
                .toBigDecimal()
                .setScale(scale)
    }

@Composable
fun Int.to_dp() =
    with(LocalDensity.current) { this@to_dp.toDp() }

@Composable
fun Float.to_dp() =
    with(LocalDensity.current) { this@to_dp.toDp() }

@Composable
fun BigDecimal.to_dp(): Dp =
    with(LocalDensity.current) {
        this@to_dp.toFloat()
                .toDp()
    }

fun Int.to_big_decimal(scale: Int = BIG_DECIMAL_SCALE_DEFAULT): BigDecimal =
    this@to_big_decimal.toBigDecimal()
            .setScale(scale);

fun int_to_big_decimal(value: Int,
                       scale: Int = BIG_DECIMAL_SCALE_DEFAULT): BigDecimal =
    value.toBigDecimal()
            .setScale(scale);

fun Float.to_big_decimal(scale: Int = BIG_DECIMAL_SCALE_DEFAULT): BigDecimal
{
    val f = this@to_big_decimal;
    return if((f.isInfinite()) || (f.isNaN()))
    {
        BIG_DECIMAL_ZERO;
    }
    else
    {
        f.toBigDecimal()
                .setScale(scale);
    }
}

fun DrawScope.dp_to_big_decimal(x: Dp,
                                scale: Int = BIG_DECIMAL_SCALE_DEFAULT): BigDecimal =
    x.toPx()
            .toBigDecimal()
            .setScale(scale);

fun BigDecimal.divide(value: Int,
                      scale: Int = BIG_DECIMAL_SCALE_DEFAULT,
                      mode: RoundingMode = RoundingMode.HALF_EVEN): BigDecimal =
    this@divide.divide(BigDecimal(value),
                       scale,
                       mode)

fun BigDecimal.divide(value: String,
                      scale: Int = BIG_DECIMAL_SCALE_DEFAULT,
                      mode: RoundingMode = RoundingMode.HALF_EVEN): BigDecimal =
    this@divide.divide(BigDecimal(value),
                       scale,
                       mode)

fun convert_Celsius_to_Fahrenheit(c: BigDecimal): BigDecimal
{
    val conversion_factor = BigDecimal("9.0").divide("5.0");
    val intermediate = c * conversion_factor;
    val converted = intermediate + BigDecimal("32.0");
    return converted;
}

fun convert_KPH_to_MPH(kph: BigDecimal): BigDecimal
{
    val conversion_factor = BigDecimal("0.621371192");
    return (conversion_factor * kph);
}

fun String.get_text_width(paint: Paint): Float
{
    return paint.measureText(this)
}

fun String.get_text_height(paint: Paint): Int
{
    val bounds = Rect()
    paint.getTextBounds(this,
                        0,
                        this.length,
                        bounds)
    return bounds.height()
}

/**
 * Compares two PeriodDurations. Yes, this is not allowed, I know.
 * But depending on the content, if the period contains years and/or months, this will only be
 * an approximation according to the following assumptions.
 *     - No daylight savings time involved at all.
 *     - Uses a standard solar year (365 days 5 hours 48 minutes 46 seconds) to eliminate leap year.
 *     - Includes 30 days per month.
 *     - No normalization of the components.
 * Each component of the two objects is converted to seconds, with nanos to fractional seconds,
 * summed together, then compared. Uses BigDecimal to mitigate potential overflow.
 * Should only use this for period/durations that you know are similarly comparible.
 */
operator fun PeriodDuration.compareTo(other: PeriodDuration): Int
{
    val to_seconds: PeriodDuration.() -> BigDecimal = {
        val days = BigDecimal(period.months * 30L) + BigDecimal(period.days) + BigDecimal(period.years.toLong() * 365L);
        val (hours, minutes, seconds, nanoseconds) = duration.toKotlinDuration()
                .toComponents() { hours, minutes, seconds, nanoseconds ->
                    arrayOf(BigDecimal(hours + (5L * period.years)),
                            BigDecimal(minutes + (48L * period.years)),
                            BigDecimal(seconds + (46L * period.years)),
                            BigDecimal(nanoseconds).setScale(9));
                }

        ((days * BigDecimal(86400)) + (hours * BigDecimal(3600)) + (minutes * BigDecimal(60)) + seconds + (nanoseconds / BigDecimal(1000000000L)))
    }

    val number_of_seconds_this = this.to_seconds();
    val number_of_seconds_other = other.to_seconds();
    return (number_of_seconds_this.compareTo(number_of_seconds_other));
}

fun is_system_in_dark_mode(context: Context): Boolean
{
    var current = false;
    when(context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK))
    {
        Configuration.UI_MODE_NIGHT_YES ->
        {
            current = true;
        }
    }

    return current;
}

fun update_dark_mode(context: Context,
                     selection: Int)
{
    val selections = context.resources.getStringArray(R.array.dark_mode_options);

    val mode: Int = if(selections[selection].contains("dark",
                                                      true))
    {
        AppCompatDelegate.MODE_NIGHT_YES;
    }
    else if(selections[selection].contains("light",
                                           true))
    {
        AppCompatDelegate.MODE_NIGHT_NO;
    }
    else
    {
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    AppCompatDelegate.setDefaultNightMode(mode);
}

/**
 * Holder for use in singleton parent classes where a constructor with an argument is required.
 * Example singleton class definition and usage as follows...
 *
 *     class Your_singleton_class private constructor(construction_argument: Arguments_type?)
 *     {
 *         init
 *         {
 *              // Initialize the instance using the argument.
 *         }
 *
 *         companion object: Singleton_holder<Your_singleton_class, Arguments_type>(::Your_singleton_class)
 *
 *         fun do_something(): Result_type
 *         {
 *             ...
 *             return Result_type()
 *         }
 *     }
 *
 *     val construction_argument: Arguments_type
 *     val result: Result_type = Your_singleton_class.get_singleton(construction_argument).do_something()
 */
open class Singleton_holder<out T, in A>(parent: (A?) -> T)
{
    private var m_parent: ((A?) -> T)? = parent

    @Volatile
    private var m_instance: T? = null

    /**
     * Uses double-checked locking algorithm.
     */
    fun get_singleton(construction_argument: A? = null): T
    {
        val instance = m_instance
        if(instance != null)
        {
            return instance
        }

        return synchronized(this)
        {
            val instance_double = m_instance
            if(instance_double != null)
            {
                instance_double
            }
            else
            {
                val an_object = m_parent!!(construction_argument)
                m_instance = an_object
                m_parent = null
                an_object
            }
        }
    }

    operator fun invoke(construction_argument: A? = null): T
    {
        return get_singleton(construction_argument = construction_argument)
    }
}
