package com.smarttech.story.ui.story.detail

import android.app.Application
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentStorydetailListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_storydetail_list, container, false
        )
        val viewModelFactory = StoryDetailViewModelFactory(Application(),storyId, storyName)
        storyDetailViewModel =
            ViewModelProvider(this,viewModelFactory).get(StoryDetailViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.storyDetailViewModel = storyDetailViewModel

        val adapter = StoryDetailRecyclerViewAdapter(ChapterListener { chapterDto ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            storyDetailViewModel.onCategoryClicked(chapterDto)
        })
        binding.chapterList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        storyDetailViewModel.chapterDtos.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                //binding.categoryList.adapter = adapter

            }
        })
/*        categoryViewModel.navigateToCategory.observe(viewLifecycleOwner, Observer { category ->
            category?.let {
                val action = CategoryFragmentDirections
                    .actionCategoryFragmentToStoryFragment(category.id, category.title!!)
                this.findNavController().navigate(action)
                categoryViewModel.onCategoryNavigated()
            }
        })
        val manager = GridLayoutManager(activity, 2)
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