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

package com.devsung.androidhelper.support.security.hash

import com.devsung.androidhelper.support.security.Base64
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Encrypts an arbitrary string into a fixed-length string.
 * @param input String to be encrypted.
 * @param algorithm Hash function.
 */
class Hash(input: String, private val algorithm: String) : Base64() {

    /**
     * Encryption string generated through a secure hash algorithm.
     */
    val output: String

    init {
        val digest = MessageDigest.getInstance(algorithm)
        digest.reset()
        digest.update(input.toByteArray())
        output = encode(digest.digest())
    }

    /**
     * If you add salt, you can expect better safety.
     * @param size Size of salt.
     * @return The first has a salt-encrypted string and the second has a salt.
     */
    fun getSalt(size: Int) : Pair<String, String> {
        val byteArray = ByteArray(size)
        SecureRandom().nextBytes(byteArray)
        val salt = encode(byteArray)
        return Pair(Hash(output + salt, algorithm).output, salt)
    }
}