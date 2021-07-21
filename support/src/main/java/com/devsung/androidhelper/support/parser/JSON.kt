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

package com.devsung.androidhelper.support.parser

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * By parsing json to create a nested array.
 * @param json Data in json format.
 */
class JSON(json: Any) {

    /**
     * Parsing result.
     */
    val contents = child(json)

    private fun child(json: Any) : Array<Any> {
        val array = ArrayList<Any>()
        try {
            val jsonObject = JSONObject(json.toString())
            val names = jsonObject.names()
            for (i in 0 until names!!.length()) {
                val name = names.getString(i)
                val item = jsonObject.get(name)
                if (item is JSONObject || item is JSONArray)
                    array.add(Pair(name, child(item)))
                else
                    array.add(Pair(name, item))
            }
        } catch (e: JSONException) {
            val jsonArray = JSONArray(json.toString())
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.get(i)
                if (item is JSONObject || item is JSONArray)
                    array.add(child(item))
                else
                    array.add(item)
            }
        }
        return array.toArray()
    }
}