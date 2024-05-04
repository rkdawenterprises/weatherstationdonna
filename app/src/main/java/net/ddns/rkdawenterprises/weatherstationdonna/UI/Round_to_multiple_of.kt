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

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import java.math.BigDecimal
import java.math.RoundingMode
fun round_to_multiple_of(value: BigDecimal,
                         multiple: Int,
                         mode: RoundingMode = RoundingMode.UP): Int
{
    val big_multiple = BigDecimal(multiple);
    val half_big_multiple = big_multiple.divide(BigDecimal("2.0"),
                                                4,
                                                RoundingMode.HALF_EVEN);
    val modulus = value.remainder(big_multiple).abs();
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
                    value.minus(modulus).toInt();
                }
                else
                {
                    value.plus(big_multiple.minus(modulus)).toInt();
                }

                x;
            }

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round towards zero.
            RoundingMode.HALF_DOWN ->
            {
                val x = if(modulus <= half_big_multiple)
                {
                    value.minus(modulus).toInt();
                }
                else
                {
                    value.plus(big_multiple.minus(modulus)).toInt();
                }

                x;
            }

            // Towards the "nearest neighbor" unless both neighbors are equidistant, in which case round towards the even neighbor.
            RoundingMode.HALF_EVEN ->
            {
                val rounded_up = value.plus(big_multiple.minus(modulus)).toInt();
                val rounded_down = value.minus(modulus).toInt();
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
                    value.plus(modulus).toInt();
                }
                else
                {
                    value.minus(big_multiple.minus(modulus)).toInt();
                }

                x;
            }

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round towards zero.
            RoundingMode.HALF_DOWN ->
            {
                val x = if(modulus <= half_big_multiple)
                {
                    value.plus(modulus).toInt();
                }
                else
                {
                    value.minus(big_multiple.minus(modulus)).toInt();
                }

                x;
            }

            // Towards the "nearest neighbor" unless both neighbors are equidistant, in which case round towards the even neighbor.
            RoundingMode.HALF_EVEN ->
            {
                val rounded_down = value.plus(modulus).toInt();
                val rounded_up = value.minus(big_multiple.minus(modulus)).toInt();
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