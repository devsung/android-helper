/*
 * Copyright 2021 Devsung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devsung.androidhelper.support.color

import kotlin.math.roundToInt

class Blender {

    companion object {

        /**
         * Returns the colors in between by specifying two colors. This can be useful when creating gradation-like effects.
         *
         * @param start Start color of the gradient.
         * @param end End color of the gradient.
         * @param size Number of intermediate points.
         * @return A list of size +2 is returned because start and end colors are added to the list.
         */
        fun color(start: Color, end: Color, size: Int) : ArrayList<Color> {
            val array = ArrayList<Color>()
            val div = ArrayList<Float>().apply {
                for (i in 0 until start.components.size)
                    add((start.components[i] - end.components[i]) / size.toFloat())
            }
            array.add(start)
            for (i in 0 until size) {
                array.add(Color().apply {
                    for (j in 0 until start.components.size)
                        components.add(start.components[j] - (div[j] * i).roundToInt())
                })
            }
            array.add(end)
            return array
        }
    }
}