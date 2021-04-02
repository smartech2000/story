package com.smarttech.story.ui.story

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentStoryListBinding
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.ui.story.StoryRecyclerViewAdapter.ViewHolder.Companion.from
import kotlinx.android.synthetic.main.fragment_story_list.*


/**
 * A fragment representing a list of Items.
 */
class StoryFragment : Fragment(), StoryBottomDialogFragment.ItemClickListener {
    private lateinit var storyViewModel: StoryViewModel
    private var columnCount = 1
    val args: StoryFragmentArgs by navArgs()
    var categoryId: Int = 0
    var categoryName: String = ""
    private lateinit var adapter: StoryRecyclerViewAdapter
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
        adapter =
            StoryRecyclerViewAdapter(requireContext(), StoryListener { storyViewInfo ->
                //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
                storyViewModel.onStoryClicked(storyViewInfo)
            })

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
        binding.filterLL.setOnClickListener(View.OnClickListener {
/*            val addPhotoBottomDialogFragment = StoryBottomDialogFragment.newInstance()
            addPhotoBottomDialogFragment.show(requireActivity().supportFragmentManager, StoryBottomDialogFragment.TAG)*/
            Navigation.findNavController(it).navigate(R.id.storyBottomDialogFragment);

        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController();
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")
            ?.observe(viewLifecycleOwner, Observer {
                var data:List<StoryViewInfo> = ArrayList<StoryViewInfo>(adapter.currentList)
                when (it.toInt()) {
                    StoryFilterEnum.UPDATE.filter -> {
                        data = data.sortedBy { it.story.id }
                        textViewFilterType.text = "Thứ tự cập nhật"

                    }
                    StoryFilterEnum.RATE.filter -> {
                        data = data.sortedByDescending { it.story.rate }
                        textViewFilterType.text = "Đánh giá giảm dần"
                    }
                    StoryFilterEnum.VIEW.filter -> {
                        data = data.sortedByDescending { it.story.view?.toInt() }
                        textViewFilterType.text = "Số lượng xem giảm dần"
                    }
                    StoryFilterEnum.CHAPTER.filter -> {
                        data = data.sortedByDescending { it.story.chapNum }
                        textViewFilterType.text = "Số chương giảm dần"
                    }
                    StoryFilterEnum.NAME.filter -> {
                        data = data.sortedBy{ it.story.title }
                        textViewFilterType.text = "Tên truyện từ A tới Z"
                    }
                }
                adapter.submitList(data)
                story_list.postDelayed(Runnable {
                    // or use other API
                    story_list.smoothScrollToPosition(0)

                    // give a delay of one second
                }, 1000)

            })
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

    override fun onItemClick(item: String) {
        TODO("Not yet implemented")
    }
}