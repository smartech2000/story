package com.smarttech.story.library.curl.interfaces

import com.smarttech.story.library.curl.views.CurlPage

interface IPageProvider {
    fun getPageCount() : Int
    fun updatePage(page: CurlPage, width: Int, height: Int, index: Int)
}