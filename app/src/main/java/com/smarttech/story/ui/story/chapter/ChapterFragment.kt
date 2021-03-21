package com.smarttech.story.ui.story.chapter

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.smarttech.story.MainActivity
import com.smarttech.story.R
import com.smarttech.story.library.curl.implementations.SizeChangedObserver
import com.smarttech.story.library.curl.interfaces.IPageProvider
import com.smarttech.story.library.curl.views.CurlPage
import com.smarttech.story.library.curl.views.CurlView
import com.smarttech.story.library.extension.getBitmap
import com.smarttech.story.library.pagination.ReadState
import com.smarttech.story.library.views.OnActionListener
import com.smarttech.story.library.views.PaginatedTextView


class ChapterFragment : Fragment(), OnActionListener, IPageProvider {

    companion object {
        fun newInstance() = ChapterFragment()
    }

    private lateinit var mCurlView: CurlView
    private lateinit var tvBookContent: PaginatedTextView
    private var pagesCount : Int = 0

    private lateinit var linearBook : View
    private lateinit var tvPageNum : TextView
    private lateinit var tvReadPercent : TextView
    private lateinit var pProgressBar : ProgressBar

    private lateinit var viewModel: ChapterViewModel
    var chapterKey : String=""
    var chapterIndex: Int = 0
    val args: ChapterFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            chapterKey = args.chapterKey
            chapterIndex = args.chapterIndex
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chapter_fragment, container, false)
        tvBookContent = view.findViewById<View>(R.id.tv_book_text) as PaginatedTextView
        tvBookContent.setup("")
        tvBookContent.setOnActionListener(this)
        linearBook = view.findViewById(R.id.linearBook)
        tvPageNum = view.findViewById(R.id.tvPageNum)
        tvReadPercent = view.findViewById<TextView>(R.id.tvReadPercent)
        mCurlView = view.findViewById<View>(R.id.curl) as CurlView
        pProgressBar = view.findViewById<View>(R.id.progress_bar) as ProgressBar
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar!!.hide()
        (activity as MainActivity).findViewById<View>(R.id.nav_view).visibility = View.GONE
        val viewModelFactory = ChapterViewModelFactory(Application(), chapterKey, chapterIndex)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChapterViewModel::class.java)
        viewModel.chapterContent.observe(viewLifecycleOwner, Observer {
            tvBookContent.setup(it)
            pProgressBar.visibility = View.GONE
        })
    }

    override fun onDestroy() {
        (activity as MainActivity).supportActionBar!!.show()
        (activity as MainActivity).findViewById<View>(R.id.nav_view).visibility = View.VISIBLE
        super.onDestroy()
    }

    override fun onPageLoaded(state: ReadState) {
        val page = state.page as CurlPage?
        tvReadPercent.text = "${state.readPercent.toInt()}%"
        tvPageNum.text = "${state.currentIndex} / ${state.pagesCount}"

        val bitmap = linearBook.getBitmap()
        page?.setTexture(bitmap, CurlPage.SIDE_FRONT)
        val background = BitmapFactory.decodeResource(resources, R.drawable.anhnen)
        page?.setTexture(background, CurlPage.SIDE_BACK)
    }

    override fun onClick(paragraph: String) {
    }

    override fun onLongClick(word: String) {

    }

    public override fun onPause() {
        super.onPause()
        mCurlView.onPause()
    }

    public override fun onResume() {
        super.onResume()
        mCurlView.onResume()
    }

    override fun getPageCount(): Int {
        return pagesCount
    }

    override fun updatePage(page: CurlPage, width: Int, height: Int, index: Int) {
        activity?.runOnUiThread {
            tvBookContent.readPage(page, index)
        }
    }

    override fun onReady(state: ReadState) {
        pagesCount = state.pagesCount
        mCurlView.setPageProvider(this)
        mCurlView.setSizeChangedObserver(SizeChangedObserver(mCurlView!!))
        mCurlView.currentIndex = 0
        mCurlView.setBackgroundColor(Color.rgb(180, 180, 180))
    }
}