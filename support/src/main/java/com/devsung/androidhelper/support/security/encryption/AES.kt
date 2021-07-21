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

package com.devsung.androidhelper.support.security.encryption

import com.devsung.androidhelper.support.security.Base64
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES is a two-way encryption technology that uses a symmetric key.
 * @param key String to use as symmetric key.
 */
class AES(private val key: String) : Base64() {

    /**
     * Size of salt. The default is 32.
     */
    var saltSize = 32
    /**
     * Encryption block size.
     * The default is 256. Additional 128 and 192 can be used.
     */
    var keyLength = 256
    /**
     * Number of cryptographic iteration. The default is 1000.
     */
    var iterationCount = 1000
    /**
     * Key generation algorithm.
     */
    var algorithm = "PBKDF2WithHmacSHA1"
    /**
     * Algorithm, block cipher modes of operation, and padding.
     */
    var transformation = "AES/CBC/PKCS5Padding"

    /**
     * Encrypts a string.
     */
    fun getEncrypt(input: String) : String {
        val salt = ByteArray(saltSize)
        SecureRandom().nextBytes(salt)
        val spec = PBEKeySpec(key.toCharArray(), salt, iterationCount, keyLength)
        val secretKey = SecretKeyFactory.getInstance(algorithm).generateSecret(spec)
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(secretKey.encoded, "AES"))
        val params = cipher.parameters
        val iv = params.getParameterSpec(IvParameterSpec::class.java).iv
        val encrypt = cipher.doFinal(input.toByteArray())
        val buffer = ByteArray(salt.size + iv.size + encrypt.size)
        System.arraycopy(salt, 0, buffer, 0, salt.size)
        System.arraycopy(iv, 0, buffer, salt.size, iv.size)
        System.arraycopy(encrypt, 0, buffer, salt.size + iv.size, encrypt.size)
        return encode(buffer)
    }

    /**
     * Decrypts a string.
     */
    fun getDecrypt(input: String) : String {
        val cipher = Cipher.getInstance(transformation)
        val buffer = ByteBuffer.wrap(decode(input))
        val salt = ByteArray(saltSize)
        val iv = ByteArray(cipher.blockSize)
        buffer.get(salt, 0, salt.size)
        buffer.get(iv, 0, iv.size)
        val decrypt = ByteArray(buffer.capacity() - salt.size - iv.size)
        buffer.get(decrypt)
        val spec = PBEKeySpec(key.toCharArray(), salt, iterationCount, keyLength)
        val secretKey = SecretKeyFactory.getInstance(algorithm).generateSecret(spec)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(secretKey.encoded, "AES"), IvParameterSpec(iv))
        return String(cipher.doFinal(decrypt))
    }
}