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

import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * By parsing xml to create a nested array.
 * @param xml Data in xml format.
 */
class XML(xml: Any) {

    /**
     * Parsing result.
     */
    val contents: Array<Any>

    init {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(InputSource(StringReader(xml.toString())))
        contents = child(document.documentElement.childNodes)
    }

    private fun child(items: NodeList) : Array<Any> {
        val array = ArrayList<Any>()
        for (i in 0 until items.length) {
            val parent = items.item(i).childNodes
            for (j in 0 until parent.length) {
                val child = parent.item(j)
                if (child.nodeName == "#text")
                    array.add(Pair(items.item(i).nodeName, child.nodeValue))
                else {
                    array.add(child(parent))
                    break
                }
            }
        }
        return array.toArray()
    }
}