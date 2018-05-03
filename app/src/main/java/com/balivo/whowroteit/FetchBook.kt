package com.balivo.whowroteit

import android.os.AsyncTask
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject


class FetchBook : AsyncTask<String, Void, String> {

    private lateinit var mTitleText: TextView
    private lateinit var mAuthorText: TextView

    val title: String? = null
    val authors: String? = null

    constructor(mTitleText: TextView, mAuthorText: TextView) {
        this.mTitleText = mTitleText
        this.mAuthorText = mAuthorText
    }

    override fun doInBackground(vararg p0: String?): String {
        return NetworkUtils.getBookInfo(p0[0].toString()).toString()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        try {
            var jsonObject = JSONObject(result) as JSONObject
            var itemsArray = jsonObject.getJSONArray("items") as JSONArray

            for (i in 0 until itemsArray.length()) {
                val book = itemsArray.getJSONObject(i) //Get the current item
                var title: String? = null
                var authors: String? = null
                val volumeInfo = book.getJSONObject("volumeInfo")

                try {
                    title = volumeInfo.getString("title")
                    authors = volumeInfo.getString("authors")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //If both a title and author exist, update the TextViews and return
                if (title != null && authors != null) {
                    mTitleText.text = title
                    mAuthorText.text = authors
                    return
                }
            }
            mTitleText.text = "No Results Found"
            mAuthorText.text = ""

        } catch (e: Exception) {
            mTitleText.text = "No Results Found"
            mAuthorText.text = ""
            e.printStackTrace()
        }
    }

}