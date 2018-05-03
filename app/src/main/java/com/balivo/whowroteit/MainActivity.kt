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


class MainActivity : AppCompatActivity() {

    lateinit var mBookInput: EditText
    lateinit var mAuthorText: TextView
    lateinit var mTitleText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBookInput = findViewById(R.id.bookInput)
        mAuthorText = findViewById(R.id.authorText)
        mTitleText = findViewById(R.id.titleText)
    }

    fun searchBooks(view: View) {
        val mQueryString = mBookInput.text.toString()

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

            FetchBook(mTitleText, mAuthorText).execute(mQueryString)

            mTitleText.text = getString(R.string.waitTitle)
            mAuthorText.text = ""

        } else {

            if (mQueryString.length == 0) {
                mAuthorText.setText("")
                mTitleText.text = "Please enter a search term"

            } else {

                mAuthorText.text = ""
                mTitleText.text = "Please check your network connection and try again."
            }
        }
    }
}
