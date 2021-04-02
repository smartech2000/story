package com.smarttech.story.ui.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentCategoryListBinding
import com.smarttech.story.ui.story.StoryFilterEnum

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
        //Create adapter: Onclick item listener, change item property value
        val adapter = CategoryRecyclerViewAdapter(CategoryListener { categoryId ->
            categoryViewModel.onCategoryClicked(categoryId)
        })
        //Bind adapter to list
        binding.categoryList.adapter = adapter
        //Observe list data
        categoryViewModel.categories.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                binding.progressBarLoading.visibility = View.GONE
            }
        })
        //Observe item clicked
        categoryViewModel.navigateToCategory.observe(viewLifecycleOwner, Observer { category ->
            category?.let {
                val navController = findNavController();
                val saveSate = navController.previousBackStackEntry?.savedStateHandle
                saveSate?.set("key", StoryFilterEnum.UPDATE.filter.toString())
                val action = CategoryFragmentDirections
                    .actionCategoryFragmentToStoryFragment(category.id, category.title!!)
                this.findNavController().navigate(action)
                categoryViewModel.onCategoryNavigated()
            }
        })
        //Change layout manager
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