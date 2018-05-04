package com.balivo.whowroteit

import android.content.Context
import android.support.v4.content.AsyncTaskLoader


class BookLoader(context: Context, queryString : String) : AsyncTaskLoader<String>(context) {

    val mQueryString = queryString


    override fun loadInBackground(): String? {

        return NetworkUtils.getBookInfo(mQueryString)
    }


    override fun onStartLoading() {
        super.onStartLoading()

        forceLoad()
    }
}