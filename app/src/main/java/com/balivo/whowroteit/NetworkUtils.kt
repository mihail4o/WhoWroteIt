package com.balivo.whowroteit

import android.net.Uri
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.io.IOException


class NetworkUtils {


    companion object {

        val LOG_TAG = "NetworkUtils"

        val BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?" // Base URI for the API:Books
        val QUERY_PARAM = "q" // Parameter for the search string
        val MAX_RESULTS = "maxResults" // Parameter that limits search results
        val PRINT_TYPE = "printType" // Parameter to filter by print type

        fun getBookInfo(queryString: String): String? {
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            var bookJSONString: String? = null

            try {
                //Build up your query URI, limiting results to 10 items and printed books
                val builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, queryString)
                        .appendQueryParameter(MAX_RESULTS, "10")
                        .appendQueryParameter(PRINT_TYPE, "books")
                        .build() as Uri
                val requestURL = URL(builtURI.toString())

                urlConnection = requestURL.openConnection() as HttpURLConnection
                urlConnection.setRequestMethod("GET")
                urlConnection.connect()

                val inputStream = urlConnection.getInputStream() as InputStream

                var buffer = StringBuffer()

                if (inputStream == null) {
                    return null
                }

                reader = BufferedReader(InputStreamReader(inputStream))
                var line = ""

                while (true) {
                    val line = reader.readLine() ?: break

                    /* Since it's JSON, adding a newline isn't necessary (it won't affect
                parsing) but it does make debugging a *lot* easier if you print out the
                completed buffer for debugging. */
                    buffer.append(line + "\n")
                }

                if (buffer.length == 0) {

                    // Stream was empty. No point in parsing.
                    return null
                }

                bookJSONString = buffer.toString()

            } catch (ex: Exception) {
                ex.printStackTrace()
                return null

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect()
                }

                if (reader != null) {

                    try {

                        reader.close()

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            Log.d(LOG_TAG, "bookJSONString: " + bookJSONString)
            return bookJSONString
        }
    }
}