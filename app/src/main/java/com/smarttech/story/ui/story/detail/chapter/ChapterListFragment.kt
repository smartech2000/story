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
import androidx.navigation.fragment.findNavController
import com.smarttech.story.MainActivity
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentStoryChapterBinding
import com.smarttech.story.databinding.FragmentStoryChapterListBinding
import com.smarttech.story.model.dto.ChapterDto
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.ui.story.detail.StoryDetailFragmentDirections
import com.smarttech.story.ui.story.detail.chapter.ChapterListener
import kotlinx.android.synthetic.main.fragment_story_chapter_list.*
import kotlinx.android.synthetic.main.fragment_story_list.*

class ChapterListFragment : Fragment() {

    companion object {
        fun newInstance() = ChapterListFragment()
    }
    var storyId: Int = 0
    var storyName: String = ""
    private lateinit var viewModel: ChapterListViewModel
    private lateinit var adapter: ChapterListRecyclerViewAdapter
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
            ChapterListViewModelFactory(Application(), requireContext(), storyId)
        viewModel =
            ViewModelProvider(this,viewModelFactory).get(ChapterListViewModel::class.java)


        adapter = ChapterListRecyclerViewAdapter(ChapterListener { chapterDto ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            viewModel.onChapterClicked(chapterDto)
        })

        binding.chapterList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        viewModel.chapterDtos.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                binding.progressBarLoading.visibility = View.GONE
                //binding.categoryList.adapter = adapter

            }
        })

        viewModel.navigateToChapter.observe(viewLifecycleOwner, Observer { chapterDto ->
            chapterDto?.let {
                val action = StoryDetailFragmentDirections
                    .actionStoryDetailFragmentToChapterFragment(chapterDto.key,
                        chapterDto.index,
                        storyId = storyId,
                        storyName = storyName,
                        chapterTitle = chapterDto.title)
                this.findNavController().navigate(action)
                (activity as MainActivity).supportActionBar!!.hide()
                (activity as MainActivity).findViewById<View>(R.id.nav_view).visibility = View.GONE
                viewModel.onChapterNavigated()
            }
        })

        binding.filterLL.setOnClickListener(View.OnClickListener {
            var data:List<ChapterDto> = ArrayList<ChapterDto>(adapter.currentList)
            data =data.reversed()
            adapter.submitList(data)
            if (binding.textViewFilterType.text.equals("Thứ tự tăng dần")) {
                binding.textViewFilterType.text = "Thứ tự giảm dần"
            } else {
                binding.textViewFilterType.text = "Thứ tự tăng dần"
            }


            chapter_list.postDelayed(Runnable {
                // or use other API
                chapter_list.smoothScrollToPosition(0)

                // give a delay of one second
            }, 1000)
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChapterListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}