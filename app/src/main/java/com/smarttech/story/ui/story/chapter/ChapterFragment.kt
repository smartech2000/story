package com.smarttech.story.ui.story.chapter

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.smarttech.story.MainActivity
import com.smarttech.story.R
import com.smarttech.story.cache.MemoryCache
import com.smarttech.story.constants.Repo
import com.smarttech.story.databinding.ChapterFragmentBinding
import com.smarttech.story.library.curl.implementations.SizeChangedObserver
import com.smarttech.story.library.curl.interfaces.IPageProvider
import com.smarttech.story.library.curl.views.CurlPage
import com.smarttech.story.library.curl.views.CurlView
import com.smarttech.story.library.extension.getBitmap
import com.smarttech.story.library.pagination.ReadState
import com.smarttech.story.library.views.OnActionListener
import com.smarttech.story.library.views.PaginatedTextView
import kotlinx.android.synthetic.main.chapter_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


class ChapterFragment : Fragment(), OnActionListener, IPageProvider {

/*    companion object {
        fun newInstance() = ChapterFragment()
    }*/

    private var pagesCount: Int = 0
    private lateinit var viewModel: ChapterViewModel
    var chapterKey: String = ""
    var chapterIndex: Int = 0
    var chapterTitle: String = ""
    var storyId: Int = 0
    var storyName: String = ""

    private lateinit var binding: ChapterFragmentBinding
    private var background: Bitmap? = null

    val args: ChapterFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            chapterKey = args.chapterKey
            chapterIndex = args.chapterIndex
            storyId = args.storyId
            storyName = args.storyName
            chapterTitle = args.chapterTitle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.chapter_fragment, container, false
        )
        binding.tvBookText.setup("", 1.0)
        binding.tvBookText.setOnActionListener(this)

        binding.tvBookName.text = storyName + " - " + chapterTitle
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        val viewModelFactory =
            ChapterViewModelFactory(Application(), requireContext(), storyId, chapterKey, chapterIndex)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChapterViewModel::class.java)
        viewModel.chapterContent.observe(viewLifecycleOwner, Observer {
            binding.tvBookText.setup(it, 0.6)
            binding.progressBar.visibility = View.GONE
        })
    }

    override fun onDestroy() {
        (activity as MainActivity).supportActionBar!!.show()
        (activity as MainActivity).findViewById<View>(R.id.nav_view).visibility = View.VISIBLE
        super.onDestroy()
    }

    override fun onPageLoaded(state: ReadState) {
        val page = state.page as CurlPage?
        binding.tvReadPercent.text = "${state.readPercent.toInt()}%"
        binding.tvPageNum.text = "${state.currentIndex} / ${state.pagesCount}"
        val bitmap = binding.linearBook.getBitmap()
        if (bitmap != null) {
            page?.setTexture(bitmap, CurlPage.SIDE_FRONT)
        }
        if (page != null) {
            setBackgroundBitmap(page)
        }

    }

    fun setBackgroundBitmap(page: CurlPage) {
        background = MemoryCache.getInstance().get(storyId)
        if (background != null) {
            val avatarFile = File(Repo.AVATAR.getRepo(requireContext().cacheDir), "$storyId")
            if (avatarFile.exists()) {
                val b = avatarFile.readBytes()
                background = BitmapFactory.decodeByteArray(b, 0, b.size)
                MemoryCache.getInstance().put(storyId, background)
            }
        }
        if (background != null) {
            val cloneBm = background!!.copy(background!!.config, true)
            page.setTexture(cloneBm, CurlPage.SIDE_BACK)
        }
    }

    override fun onClick(paragraph: String) {
    }

    override fun onLongClick(word: String) {

    }

    public override fun onPause() {
        super.onPause()
        binding.curl.onPause()
    }

    public override fun onResume() {
        super.onResume()
        binding.curl.onResume()
    }

    override fun getPageCount(): Int {
        return pagesCount + 1
    }

    override fun updatePage(page: CurlPage, width: Int, height: Int, index: Int) {
        if (index >= pagesCount) {
          //  binding.tvLastPage.text = "Hết chương " // + chapterTitle + "( Truyện " + storyName + ")"
            val bmp = binding.tvLastPage.getBitmap(binding.linearBook.width, binding.linearBook.height)
            if (bmp != null) {
                page.setTexture(bmp, CurlPage.SIDE_FRONT)
            }
            setBackgroundBitmap(page)
            return
        }
        activity?.runOnUiThread {
            binding.tvBookText.readPage(page, index)
        }
    }

    override fun onReady(state: ReadState) {
        pagesCount = state.pagesCount
        binding.curl.setPageProvider(this)
        binding.curl.setSizeChangedObserver(SizeChangedObserver(binding.curl!!))
        binding.curl.currentIndex = 0
        binding.curl.setBackgroundColor(Color.rgb(180, 180, 180))
    }
}