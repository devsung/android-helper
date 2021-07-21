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

package com.devsung.androidhelper.support.network.download

import android.os.AsyncTask
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class Download {

    /**
     * URL of file to download.
     */
    var url: Array<URL>? = null
    /**
     * Path to which files will be stored.
     */
    var path: String? = null
    /**
     * Receive progress and file information of the file being downloaded from onProgress.
     */
    var progress = true
    /**
     * Pause the file download.
     */
    var pause = false
    /**
     * Cancel the file download.
     */
    var cancel = false
    /**
     * Time the operation took to complete.
     */
    var runtime = 0L
    /**
     * The amount of memory that temporarily stores data.
     * The default is 1024.
     */
    var bufferSize = 1024

    fun open(
        onResult: (path: Array<String>) -> Unit,
        onProgress: (
            progress: Int,
            file: Triple<String, Int, Long> // Triple<fileName, fileIndex, downloadSize>
        ) -> Unit
    ) {
        if (url == null || path == null)
            throw Throwable("This is not a valid request.", Throwable())
        class Download : AsyncTask<Void, Pair<Int, Triple<String, Int, Long>>, Array<String>>() {
            override fun doInBackground(vararg params: Void): Array<String> {
                runtime = System.currentTimeMillis()
                val array = Array(url!!.size) { "" }
                for (i in url!!.indices) {
                    val connection = url!![i].openConnection().apply { connect() }
                    val split = url!![i].toString().split("/")
                    val fileName = split[split.lastIndex]
                    val file = File("$path/$fileName")
                    if (!file.exists()) file.createNewFile()
                    val outputStream = FileOutputStream(file)
                    val inputStream = connection.getInputStream()
                    val buffer = ByteArray(bufferSize)
                    val fileSize = connection.contentLength
                    var downloadSize = 0L
                    do {
                        if (cancel) return array
                        if (!pause) {
                            val length = inputStream.read(buffer)
                            if (length <= 0) break
                            if (progress && fileSize > 0) {
                                downloadSize += length
                                val progress = (downloadSize / fileSize) * 100
                                publishProgress(Pair(progress.toInt(), Triple(fileName, i, downloadSize)))
                            }
                            outputStream.write(buffer, 0, length)
                        }
                    } while (true)
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()
                    array[i] = file.absolutePath
                }
                return array
            }
            override fun onProgressUpdate(vararg values: Pair<Int, Triple<String, Int, Long>>) = onProgress(values[0].first, values[0].second)
            override fun onPostExecute(result: Array<String>) {
                runtime = System.currentTimeMillis() - runtime
                onResult(result)
            }
        }
        Download().execute()
    }
}