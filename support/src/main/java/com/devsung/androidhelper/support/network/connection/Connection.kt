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

package com.devsung.androidhelper.support.network.connection

import android.os.AsyncTask
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class Connection {

    companion object {
        const val POST = 0
        const val GET = 1
    }

    var url: URL? = null
    var crt: File? = null
    var verifier: HostnameVerifier? = null
    var contents = ArrayList<Pair<String, String>>()
    var files = ArrayList<Pair<String, File>>()
    var request = POST
    var response = 0
    var runtime = 0L
    var timeout = 10000
    private var charset = "UTF-8"
    private var boundary = "-------"
    private var linefeed = "\r\n"

    /**
     * Receive events when communication with the server ends.
     */
    fun open(onConnected: (result: Any?) -> Unit) {
        if (url == null || !(request == POST || request == GET))
            throw Throwable("This is not a valid request.", Throwable())
        class Connection : AsyncTask<Void, Void, Any>() {
            override fun doInBackground(vararg params: Void) : Any {
                runtime = System.currentTimeMillis()
                val connection = url!!.openConnection() as HttpsURLConnection
                connection.requestMethod = if (request == POST) "POST" else "GET"
                crt?.let {
                    if (verifier == null)
                        verifier = HostnameVerifier { _, _ -> true }
                    val inputStream = BufferedInputStream(FileInputStream(it))
                    val certificateFactory = CertificateFactory.getInstance("X.509")
                    val certificate = inputStream.use {
                        certificateFactory.generateCertificate(inputStream) as X509Certificate
                    }
                    val keyStoreType = KeyStore.getDefaultType()
                    val keyStore = KeyStore.getInstance(keyStoreType).apply {
                        load(null, null)
                        setCertificateEntry("ca", certificate)
                    }
                    val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                    val tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
                        init(keyStore)
                    }
                    val context = SSLContext.getInstance("TLS").apply {
                        init(null, tmf.trustManagers, null)
                    }
                    connection.sslSocketFactory = context.socketFactory
                    connection.hostnameVerifier = verifier
                }
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.connectTimeout = timeout
                if (request == POST) {
                    connection.setRequestProperty("Content-Type", "multipart/form-data; charset=$charset; boundary=$boundary")
                    val outputStream = connection.outputStream
                    val printWriter = PrintWriter(OutputStreamWriter(outputStream, charset), true)
                    contents.forEach {
                        printWriter
                            .append("--$boundary").append(linefeed)
                            .append("Content-Disposition: form-data; name=\"${it.first}\"").append(linefeed)
                            .append("Content-Type: text/plain; charset=$charset").append(linefeed)
                            .append(linefeed).append(it.second).append(linefeed)
                            .flush()
                    }
                    files.forEach {
                        printWriter
                            .append("--$boundary").append(linefeed)
                            .append("Content-Disposition: form-data; name=\"${it.first}\"; filename=\"${it.second.name}\"").append(linefeed)
                            .append("Content-Type: ${URLConnection.guessContentTypeFromName(it.second.name)}").append(linefeed)
                            .append("Content-Transfer-Encoding: binary").append(linefeed).append(linefeed)
                            .flush()
                        val inputStream = FileInputStream(it.second)
                        val buffer = ByteArray(it.second.length().toInt())
                        var bytesRead: Int
                        while ((inputStream.read(buffer).also { byte -> bytesRead = byte }) != -1)
                            outputStream.write(buffer, 0, bytesRead)
                        outputStream.flush()
                        printWriter.append(linefeed).flush()
                    }
                    printWriter.append("--$boundary--").append(linefeed)
                    printWriter.close()
                }
                connection.connect()
                connection.instanceFollowRedirects = true
                response = connection.responseCode
                val bufferedReader: BufferedReader =
                    if (response == HttpsURLConnection.HTTP_OK || response == HttpsURLConnection.HTTP_CREATED)
                        BufferedReader(InputStreamReader(connection.inputStream))
                    else
                        BufferedReader(InputStreamReader(connection.errorStream))
                var inputLine: String?
                val buffer = StringBuffer()
                while ((bufferedReader.readLine().also { inputLine = it }) != null)
                    buffer.append(inputLine)
                bufferedReader.close()
                return buffer.toString()
            }
            override fun onPostExecute(result: Any) {
                runtime = System.currentTimeMillis() - runtime
                onConnected(result)
            }
        }
        Connection().execute()
    }
}