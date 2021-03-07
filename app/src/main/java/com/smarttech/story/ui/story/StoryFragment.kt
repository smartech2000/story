package com.smarttech.story.ui.story

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentCategoryListBinding
import com.smarttech.story.databinding.FragmentStoryListBinding
import com.smarttech.story.ui.category.CategoryListener
import com.smarttech.story.ui.category.CategoryRecyclerViewAdapter
import com.smarttech.story.ui.category.CategoryViewModel
import com.smarttech.story.ui.story.dummy.DummyContent

/**
 * A fragment representing a list of Items.
 */
class StoryFragment : Fragment() {
    private lateinit var storyViewModel: StoryViewModel
    private var columnCount = 1
    val args: StoryFragmentArgs by navArgs()
    var categoryId: Long = 0L
    var categoryName : String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            categoryId = args.categoryId
            categoryName = args.categoryName
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentStoryListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_story_list, container, false
        )
        val viewModelFactory = StoryViewModelFactory(categoryId, categoryName)
        storyViewModel =
            ViewModelProvider(this, viewModelFactory).get(StoryViewModel::class.java)

        binding.storyViewModel = storyViewModel
        // Set the adapter
        val adapter = StoryRecyclerViewAdapter(StoryListener { categoryId ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            storyViewModel.onCategoryClicked(categoryId)
        })
        binding.storyList.adapter = adapter
        storyViewModel.stories.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)

                //binding.categoryList.adapter = adapter
            }
        })
        val manager = GridLayoutManager(activity, 2)
        binding.storyList.layoutManager = manager
        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            StoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}