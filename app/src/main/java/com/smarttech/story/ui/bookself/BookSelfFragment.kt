package com.smarttech.story.ui.bookself

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentBookselfBinding
import com.smarttech.story.databinding.FragmentBookselfListBinding
import com.smarttech.story.databinding.FragmentHistoryListBinding
import com.smarttech.story.ui.bookself.history.HistoryListener
import com.smarttech.story.ui.bookself.history.HistoryRecyclerViewAdapter
import com.smarttech.story.ui.bookself.history.HistoryViewModel
import com.smarttech.story.ui.category.CategoryFragmentDirections

/**
 * A fragment representing a list of Items.
 */
class BookSelfFragment : Fragment() {
    private lateinit var viewModel: BookSelfViewModel
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
        val binding: FragmentBookselfListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_bookself_list, container, false
        )
        viewModel =
            ViewModelProvider(this).get(BookSelfViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.viewModel = viewModel

        val adapter = BookSelfRecyclerViewAdapter(FunctionListener { itemId ->
            //Toast.makeText(context, "${itemId}", Toast.LENGTH_LONG).show()
            viewModel.onFunctionClicked(itemId)
        })
        binding.bookselfList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        viewModel.functions.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToFunction.observe(viewLifecycleOwner, Observer { function ->
            function?.let {
                var action:NavDirections = BookSelfFragmentDirections
                    .actionNavigationBookselfToHistoryFragment()
                when (function.id) {
                    1->action = BookSelfFragmentDirections
                        .actionNavigationBookselfToDownloadFragment()
                    2-> action = BookSelfFragmentDirections
                        .actionNavigationBookselfToHistoryFragment()
                    3-> action = BookSelfFragmentDirections
                        .actionNavigationBookselfToBookmarkFragment()
                }



                this.findNavController().navigate(action)
                viewModel.onFunctionNavigated()
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
            BookSelfFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}