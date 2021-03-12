package com.smarttech.story.ui.category

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
import androidx.navigation.fragment.findNavController
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentCategoryBinding
import com.smarttech.story.databinding.FragmentCategoryListBinding
import com.smarttech.story.ui.home.HomeViewModel

/**
 * A fragment representing a list of Items.
 */
class CategoryFragment : Fragment() {
    private lateinit var categoryViewModel: CategoryViewModel
    private var columnCount = 2

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
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentCategoryListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_category_list, container, false
        )
        categoryViewModel =
            ViewModelProvider(this).get(CategoryViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.categoryViewModel = categoryViewModel

        val adapter = CategoryRecyclerViewAdapter(CategoryListener { categoryId ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            categoryViewModel.onCategoryClicked(categoryId)
        })
        binding.categoryList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        categoryViewModel.categories.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                //binding.categoryList.adapter = adapter

            }
        })
        categoryViewModel.navigateToCategory.observe(viewLifecycleOwner, Observer { category ->
            category?.let {
                val action = CategoryFragmentDirections
                    .actionCategoryFragmentToStoryFragment(category.id, category.name)
                this.findNavController().navigate(action)
                categoryViewModel.onCategoryNavigated()
            }
        })
        val manager = GridLayoutManager(activity, 2)
        binding.categoryList.layoutManager = manager

        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}