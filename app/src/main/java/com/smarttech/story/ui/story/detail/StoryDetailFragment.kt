package com.smarttech.story.ui.story.detail

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentCategoryListBinding
import com.smarttech.story.databinding.FragmentStorydetailBinding
import com.smarttech.story.databinding.FragmentStorydetailListBinding
import com.smarttech.story.ui.category.CategoryFragmentDirections
import com.smarttech.story.ui.category.CategoryListener
import com.smarttech.story.ui.category.CategoryRecyclerViewAdapter
import com.smarttech.story.ui.category.CategoryViewModel
import com.smarttech.story.ui.story.StoryFragmentArgs
import com.smarttech.story.ui.story.StoryViewModelFactory
import kotlinx.android.synthetic.main.fragment_storydetail_list.*

/**
 * A fragment representing a list of Items.
 */
class StoryDetailFragment : Fragment() {
    private lateinit var storyDetailViewModel: StoryDetailViewModel
    private var columnCount = 1
    var storyId: Int = 0
    var storyName : String=""
    val args: StoryDetailFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            storyId = args.storyId
            storyName = args.storyName
        }

        //notify the fragment that it should participate in options menu handling.
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO Add your menu entries here
       // menu?.findItem(R.id.download_menu)?.isVisible = true
        menu?.findItem(R.id.bookmark_menu)?.isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.requireActivity().invalidateOptionsMenu()
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentStorydetailListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_storydetail_list, container, false
        )
        val viewModelFactory = StoryDetailViewModelFactory(Application() , context!! ,storyId, storyName)
        storyDetailViewModel =
            ViewModelProvider(this,viewModelFactory).get(StoryDetailViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.storyDetailViewModel = storyDetailViewModel



        val adapter = StoryDetailRecyclerViewAdapter(ChapterListener { chapterDto ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            storyDetailViewModel.onChapterClicked(chapterDto)
        })
        binding.chapterList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        storyDetailViewModel.chapterDtos.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                //binding.categoryList.adapter = adapter

            }
        })
        storyDetailViewModel.navigateToChapter.observe(viewLifecycleOwner, Observer { chapterDto ->
            chapterDto?.let {
                val action = StoryDetailFragmentDirections
                    .actionStoryDetailFragmentToChapterFragment(chapterDto.key, chapterDto.index)
                this.findNavController().navigate(action)
                storyDetailViewModel.onChapterNavigated()
            }
        })


        storyDetailViewModel.storyDesc.observe(viewLifecycleOwner, Observer {
            desc_tv.text = it
        })
 /*       val manager = GridLayoutManager(activity, 2)
        binding.categoryList.layoutManager = manager*/

        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            StoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}