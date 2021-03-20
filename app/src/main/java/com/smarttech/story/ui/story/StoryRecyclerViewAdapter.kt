package com.smarttech.story.ui.story

import android.app.Application
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.smarttech.story.R
import com.smarttech.story.databinding.FragmentStoryBinding
import com.smarttech.story.databinding.FragmentStoryListBinding
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.UnzipUtility
import kotlinx.android.synthetic.main.fragment_story.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class StoryRecyclerViewAdapter(
    val clickListener: StoryListener
) : ListAdapter<StoryViewInfo, StoryRecyclerViewAdapter.ViewHolder>(StoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var storyViewInfo = getItem(position)
        holder.bind(storyViewInfo!!, clickListener)
         // Load avatar
        GlobalScope.launch(Dispatchers.IO) {
            val url =
                "https://www.dropbox.com/s/${storyViewInfo.story.avatar}/${storyViewInfo.story.id}?dl=1"
            var stringResponse: String?
            val response = DropboxService.getInstance().downlload(url).execute()
            val body = response.body()
            stringResponse = body?.string()
            if (stringResponse != null) {
                withContext(Dispatchers.Main) {
                    //holder.itemView.textView10.text = stringResponse
                }

            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    // override fun getItemCount(): Int = values.size

    class ViewHolder private constructor(val binding: FragmentStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoryViewInfo, clickListener: StoryListener) {
            binding.storyViewInfo = item
            binding.clickListener = clickListener
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentStoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class StoryDiffCallback : DiffUtil.ItemCallback<StoryViewInfo>() {

    override fun areItemsTheSame(oldItem: StoryViewInfo, newItem: StoryViewInfo): Boolean {
        return oldItem.story.id == newItem.story.id
    }

    override fun areContentsTheSame(oldItem: StoryViewInfo, newItem: StoryViewInfo): Boolean {
        return oldItem.story.title == newItem.story.title
    }
}

class StoryListener(val clickListener: (storyViewInfo: StoryViewInfo) -> Unit) {
    fun onClick(storyViewInfo: StoryViewInfo) = clickListener(storyViewInfo)
}

/**
 * A fragment representing a list of Items.
 */
class StoryFragment : Fragment() {
    private lateinit var storyViewModel: StoryViewModel
    private var columnCount = 1
    val args: StoryFragmentArgs by navArgs()
    var categoryId: Int = 0
    var categoryName : String=""
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
        val adapter = StoryRecyclerViewAdapter(StoryListener { storyViewInfo ->
            //Toast.makeText(context, "${categoryId}", Toast.LENGTH_LONG).show()
            storyViewModel.onStoryClicked(storyViewInfo)
        })
        binding.storyList.adapter = adapter
        storyViewModel.stories.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)

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