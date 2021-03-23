package com.smarttech.story.ui.story.detail.chapter

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentStoryChapterBinding
import com.smarttech.story.databinding.FragmentStoryChapterListBinding
import com.smarttech.story.ui.story.detail.ChapterListener
import com.smarttech.story.ui.story.detail.StoryDetailRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_storydetail_list.*

class ChapterListFragment : Fragment() {

    companion object {
        fun newInstance() = ChapterListFragment()
    }
    var storyId: Int = 0
    var storyName: String = ""
    private lateinit var viewModel: ChapterListViewModel
    private lateinit var adapter: StoryDetailRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            storyId = it.getInt("storyId")
            var  x =0
        }

        //notify the fragment that it should participate in options menu handling.
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentStoryChapterListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_story_chapter_list, container, false
        )
        val viewModelFactory =
            ChapterListViewModelFactory(Application(), context!!, storyId)
        viewModel =
            ViewModelProvider(this,viewModelFactory).get(ChapterListViewModel::class.java)


        adapter = StoryDetailRecyclerViewAdapter(ChapterListener { chapterDto ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            viewModel.onChapterClicked(chapterDto)
        })

        binding.chapterList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        viewModel.chapterDtos.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                //binding.progressBarLoading.visibility = View.GONE
                //binding.categoryList.adapter = adapter

            }
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChapterListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}