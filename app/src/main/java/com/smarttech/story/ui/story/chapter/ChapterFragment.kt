package com.smarttech.story.ui.story.chapter

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.smarttech.story.R
import com.smarttech.story.ui.story.detail.StoryDetailFragment
import com.smarttech.story.ui.story.detail.StoryDetailFragmentArgs
import com.smarttech.story.ui.story.detail.StoryDetailViewModelFactory
import kotlinx.android.synthetic.main.chapter_fragment.*

class ChapterFragment : Fragment() {

    companion object {
        fun newInstance() = ChapterFragment()
    }

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
        return inflater.inflate(R.layout.chapter_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModelFactory = ChapterViewModelFactory(Application(),chapterKey,chapterIndex)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChapterViewModel::class.java)
        viewModel.chapterContent.observe(viewLifecycleOwner, Observer {
            text_chapter.text = it
        })
        // TODO: Use the ViewModel
    }

}