package com.smarttech.story.library.curl.implementations

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.smarttech.story.library.curl.interfaces.IPageProvider
import com.smarttech.story.library.curl.views.CurlPage

class PageProvider(private val mContext: Context,private val size : Int) : IPageProvider {

    override fun getPageCount(): Int {
        return size
    }

    override fun updatePage(page: CurlPage, width: Int, height: Int, index: Int) {
        Log.d("Current Page ", index.toString() + "")
//        val front = mBitmaps.get(index)
//        page.setTexture(front, CurlPage.SIDE_FRONT)
        page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK)
    }
}