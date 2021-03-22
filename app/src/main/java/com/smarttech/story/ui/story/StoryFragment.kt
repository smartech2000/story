package com.smarttech.story.ui.story

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentStoryListBinding


/**
 * A fragment representing a list of Items.
 */
class StoryFragment : Fragment() {
    private lateinit var storyViewModel: StoryViewModel
    private var columnCount = 1
    val args: StoryFragmentArgs by navArgs()
    var categoryId: Int = 0
    var categoryName: String = ""
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
        val viewModelFactory = StoryViewModelFactory(Application(), categoryId, categoryName)
        storyViewModel =
            ViewModelProvider(this, viewModelFactory).get(StoryViewModel::class.java)

        binding.storyViewModel = storyViewModel
        // Set the adapter
        val adapter = this.context?.let {
            StoryRecyclerViewAdapter(it, StoryListener { storyViewInfo ->
                //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
                storyViewModel.onStoryClicked(storyViewInfo)
            })
        }
        binding.storyList.adapter = adapter
        storyViewModel.stories.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter!!.submitList(it)
                binding.progressBarLoading.visibility = View.GONE
                //binding.categoryList.adapter = adapter
            }
        })
        storyViewModel.navigateToStoryDetail.observe(viewLifecycleOwner, Observer { storyViewInfo ->
            storyViewInfo?.let {
                val action = StoryFragmentDirections.actionStoryFragmentToStoryDetailFragment(
                    storyViewInfo.story.id,
                    storyViewInfo.story.title!!
                )
                this.findNavController().navigate(action)
                storyViewModel.onStoryNavigated()
            }
        })
/*        val manager = GridLayoutManager(activity, 2)
        binding.storyList.layoutManager = manager*/

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