package com.balivo.whowroteit

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<String> {

    lateinit var mBookInput: EditText
    lateinit var mAuthorText: TextView
    lateinit var mTitleText: TextView
    lateinit var mEpubText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBookInput = findViewById(R.id.bookInput)
        mAuthorText = findViewById(R.id.authorText)
        mTitleText = findViewById(R.id.titleText)
        mEpubText = findViewById(R.id.epubText)

        /*
        If the loader exists, initialize it. You only want to reassociate the loader
        to the Activity if a query has already been executed. In the initial state
        of the app, no data is loaded so there is none to preserve.
         */
        if (supportLoaderManager.getLoader<Any>(0) != null) {
            supportLoaderManager.initLoader(0, null, this)
        }
    }

    fun searchBooks(view: View) {

        val mQueryString = mBookInput.text.toString()

        val queryBundle = Bundle()
        queryBundle.putString("queryString", mQueryString)

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }

        var networkInfo : NetworkInfo? = null
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo()
        }

        if (networkInfo != null && networkInfo.isConnected() && mQueryString.length != 0) {

            // ***************************
            // Create, restart Loader!!! *
            // ***************************
            getSupportLoaderManager().restartLoader(0, queryBundle,this)

            mTitleText.text = getString(R.string.waitTitle)
            mAuthorText.text = ""
            mEpubText.text = ""

        } else {

            if (mQueryString.length == 0) {
                mAuthorText.text = ""
                mEpubText.text = ""
                mTitleText.text = "Please enter a search term"

            } else {

                mAuthorText.text = ""
                mEpubText.text = ""
                mTitleText.text = "Please check your network connection and try again."
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<String> {
        return BookLoader(this, args!!.getString("queryString"))
    }

    override fun onLoadFinished(loader: Loader<String>, data: String?) {
        try {
            var jsonObject = JSONObject(data) as JSONObject
            var itemsArray = jsonObject.getJSONArray("items") as JSONArray

            for (i in 0 until itemsArray.length()) {
                val book = itemsArray.getJSONObject(i) //Get the current item
                var title: String? = null
                var authors: String? = null
                var pdf: Boolean? = null
                val volumeInfo = book.getJSONObject("volumeInfo")
                val accessInfo = book.getJSONObject("accessInfo")
                val pdfObj = accessInfo.getJSONObject("pdf")

                try {
                    title = volumeInfo.getString("title")
                    authors = volumeInfo.getString("authors")
                    pdf = pdfObj.getBoolean("isAvailable")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //If both a title and author exist, update the TextViews and return
                if (title != null && authors != null && pdf != null) {
                    mTitleText.text = title
                    authors = authors.replace('"', ' ')
                    mAuthorText.text = authors.trim('[', ']')

                    mEpubText.text = if(pdf) { "has PDF version"} else { "no PDF version"}

                    return
                }
            }
            mTitleText.text = "No Results Found"
            mAuthorText.text = ""
            mEpubText.text = ""

        } catch (e: Exception) {
            mTitleText.text = "No Results Found"
            mAuthorText.text = ""
            mEpubText.text = ""
            e.printStackTrace()
        }
    }

    override fun onLoaderReset(loader: Loader<String>) {

    }

}
