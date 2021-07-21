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

open class Color() {

    var components = ArrayList<Int>()

    constructor(r: Int, g: Int, b: Int) : this(0xFF, r, g, b)
    constructor(a: Int, r: Int, g: Int, b: Int) : this() {
        components = arrayListOf(a, r, g, b)
    }
    constructor(h: Int) : this() { setComponents(h.toString(16)) }
    constructor(h: String) : this() { setComponents(h.substring(1)) }

    private fun setComponents(h: String) {
        if (h.length != 6 && h.length != 8)
            throw Throwable("It's not Hexadecimal.", Throwable())
        if (h.length == 6)
            components.add(0xFF)
        for (i in 0 until (h.length / 2))
            components.add("${h[i * 2]}${h[(i * 2) + 1]}".toInt(16))
    }

    /**
     * Returns the color to hex.
     * @return Color is returned like decimal.
     */
    fun toHex(sum: Int = 0, i: Int = 0) : Int {
        return if (i >= components.size) sum
        else toHex(sum * 0x100 + components[i], i + 1)
    }

    /**
     * Returns the color to hex.
     * @return Color is returned like #00000000.
     */
    fun toHexString(i: Int = 0) : String {
        return "#${toHex().toString(16).substring(i)}"
    }

    fun alpha() = components[0]

    fun red() = components[1]

    fun green() = components[2]

    fun blue() = components[3]
}