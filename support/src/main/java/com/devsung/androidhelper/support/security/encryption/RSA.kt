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
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * RSA is a two-way encryption technology that uses an asymmetric key.
 */
class RSA : Base64() {

    /**
     * Size of the public key and private key. The default is 2048.
     */
    var keySize = 2048
    /**
     * Public key used for encryption.
     */
    var publicKey: String? = null
    /**
     * Private key used for decryption.
     */
    var privateKey: String? = null
    /**
     * Digital signature algorithm.
     */
    var signatureAlgorithm = "SHA256withRSA"

    /**
     * Generates a public key and a private key.
     */
    fun createKeyPair() {
        val random = SecureRandom()
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(keySize, random)
        val keyPair = generator.genKeyPair()
        publicKey = encode(keyPair.public.encoded)
        privateKey = encode(keyPair.private.encoded)
    }

    /**
     * Encrypts a string.
     */
    fun encrypt(input: String, publicKey: String) : String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey))
        val encrypt = cipher.doFinal(input.toByteArray())
        return encode(encrypt)
    }

    /**
     * Decrypts a string.
     */
    fun decrypt(input: String, privateKey: String) : String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey))
        val decrypt = cipher.doFinal(decode(input))
        return String(decrypt)
    }

    /**
     * Used for digital signatures.
     */
    fun sign(fingerPrint: String, caPrivateKey: String): String {
        val signature = Signature.getInstance(signatureAlgorithm)
        signature.initSign(getPrivateKey(caPrivateKey))
        signature.update(fingerPrint.toByteArray())
        return encode(signature.sign())
    }

    /**
     * Used for digital signatures.
     */
    fun verify(fingerPrint: String, sign: String, caPublicKey: String): Boolean {
        val signature = Signature.getInstance(signatureAlgorithm)
        signature.initVerify(getPublicKey(caPublicKey))
        signature.update(fingerPrint.toByteArray())
        return signature.verify(decode(sign))
    }

    private fun getPublicKey(publicKey: String) : PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val byteArray = decode(publicKey)
        val spec = X509EncodedKeySpec(byteArray)
        return keyFactory.generatePublic(spec)
    }

    private fun getPrivateKey(privateKey: String) : PrivateKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val byteArray = decode(privateKey)
        val spec = PKCS8EncodedKeySpec(byteArray)
        return keyFactory.generatePrivate(spec)
    }
}