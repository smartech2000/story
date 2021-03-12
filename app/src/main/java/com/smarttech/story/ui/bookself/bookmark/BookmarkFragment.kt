package com.smarttech.story.ui.bookself.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentBookmarkListBinding

/**
 * A fragment representing a list of Items.
 */
class BookmarkFragment : Fragment() {
    private lateinit var viewModel: BookmarkViewModel
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
        val binding: FragmentBookmarkListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_bookmark_list, container, false
        )
        viewModel =
            ViewModelProvider(this).get(BookmarkViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.viewModel = viewModel

        val adapter = BookmarkRecyclerViewAdapter(BookmarkListener { itemId ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            viewModel.onBookmarkClicked(itemId)
        })
        binding.bookmarkList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        viewModel.bookmarks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
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
            BookmarkFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}