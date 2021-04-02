package com.smarttech.story.ui.bookself.download

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smarttech.story.MainActivity
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentDownloadListBinding
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.ui.bookself.history.HistoryFragmentDirections

/**
 * A fragment representing a list of Items.
 */
class DownloadFragment : Fragment() {
    private lateinit var viewModel: DownloadViewModel
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
        val binding: FragmentDownloadListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_download_list, container, false
        )
        viewModel =
            ViewModelProvider(this).get(DownloadViewModel::class.java)

        val adapter = DownloadRecyclerViewAdapter(requireContext(),DownloadListener { storyViewInfo ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            viewModel.onStoryClicked(storyViewInfo)
        })
        binding.downloadList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        viewModel.downloads.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                binding.progressBarLoading.visibility = View.GONE
            }
        })

        viewModel.navigateToStoryDetail.observe(viewLifecycleOwner, Observer { storyViewInfo ->
            storyViewInfo?.let {
                val action =
                    DownloadFragmentDirections
                        .actionDownloadFragmentToChapterFragment("",
                            -1,
                            storyId = storyViewInfo.story.id!!,
                            storyName = storyViewInfo.story.title!!,
                            chapterTitle = "")
                findNavController().navigate(action)
                (activity as MainActivity).supportActionBar!!.hide()
                (activity as MainActivity).findViewById<View>(R.id.nav_view).visibility =
                    View.GONE
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
            DownloadFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}