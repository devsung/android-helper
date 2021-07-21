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

package com.devsung.androidhelper.support.security

/**
 * It plays a role of helping to properly use Base64 encode and decode.
 */
open class Base64 {

    internal fun encode(byteArray: ByteArray) : String =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            java.util.Base64.getEncoder().encodeToString(byteArray)
        else
            android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)

    internal fun decode(string: String) : ByteArray =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            java.util.Base64.getDecoder().decode(string)
        else
            android.util.Base64.decode(string, android.util.Base64.DEFAULT)
}