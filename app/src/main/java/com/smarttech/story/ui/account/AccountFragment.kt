package com.smarttech.story.ui.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentAccountListBinding
import com.smarttech.story.ui.bookself.BookSelfFragmentDirections
import com.smarttech.story.utils.CommonUtils

/**
 * A fragment representing a list of Items.
 */
class AccountFragment : Fragment() {
    private lateinit var viewModel: AccountViewModel
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
        val binding: FragmentAccountListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_account_list, container, false
        )
        viewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.viewModel = viewModel

        val adapter = BookSelfRecyclerViewAdapter(FunctionListener { function ->
            //Toast.makeText(context, "${itemId}", Toast.LENGTH_LONG).show()
            viewModel.onFunctionClicked(function)
        })
        binding.accountList.adapter = adapter
        ///binding.categoryList.adapter = adapter
        viewModel.functions.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        viewModel.navigateToFunction.observe(viewLifecycleOwner, Observer { function ->
            function?.let {
                var action: NavDirections = BookSelfFragmentDirections
                    .actionNavigationBookselfToHistoryFragment()
                when (function.id) {
                    2->CommonUtils.ratingApp(requireContext())
                    3->CommonUtils.shareApp(requireContext())
                    4->CommonUtils.goToMyStore(requireContext())
                    5->  {val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                        defaultBrowser.data = Uri.parse("https://khosach.firebaseapp.com/index.html#contact")
                        startActivity(defaultBrowser)}
                    6->  {val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                        defaultBrowser.data = Uri.parse("https://khosach.firebaseapp.com/terms-conditions.html")
                        startActivity(defaultBrowser)}
                    7->  {val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                        defaultBrowser.data = Uri.parse("https://khosach.firebaseapp.com/privacy-policy.html")
                        startActivity(defaultBrowser)}
                }
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
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}