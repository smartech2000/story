package com.smarttech.story.ui.bookself.history

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
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.smarttech.story.MainActivity
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentCategoryListBinding
import com.smarttech.story.databinding.FragmentHistoryListBinding
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.ui.category.*
import com.smarttech.story.ui.home.HomeFragment
import com.smarttech.story.ui.story.StoryFragmentDirections
import com.smarttech.story.ui.story.detail.StoryDetailFragmentDirections
import com.smarttech.story.utils.ChapterUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A fragment representing a list of Items.
 */
class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentHistoryListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_history_list, container, false
        )
        viewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.viewModel = viewModel

        val adapter =
            HistoryRecyclerViewAdapter(requireContext(), HistoryListener { storyViewInfo ->
                //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
                viewModel.onStoryClicked(storyViewInfo)
            })
        binding.historyList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        viewModel.histories.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                binding.progressBarLoading.visibility = View.GONE
            }
        })
        viewModel.navigateToStoryDetail.observe(viewLifecycleOwner, Observer { storyViewInfo ->

            storyViewInfo?.let {
                val action = HistoryFragmentDirections.actionHistoryFragmentToStoryDetailFragment(
                    storyViewInfo.story.id,
                    storyViewInfo.story.title!!
                )
                findNavController().navigate(action)
                viewModel.onStoryNavigated()
            }
        })
        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}