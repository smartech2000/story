package com.smarttech.story.library.curl.implementations

import com.smarttech.story.library.curl.interfaces.ISizeChangedObserver
import com.smarttech.story.library.curl.views.CurlView

class SizeChangedObserver(private val mCurlView: CurlView) : ISizeChangedObserver {
    override fun onSizeChanged(w: Int, h: Int) {
        if (w > h) {
            mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES)
            mCurlView.setMargins(.1f, .05f, .1f, .05f)
        } else {
            mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE)
            //mCurlView.setMargins(.1f, .1f, .1f, .1f);
        }
    }
}