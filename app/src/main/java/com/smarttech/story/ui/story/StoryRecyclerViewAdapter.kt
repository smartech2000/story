package com.smarttech.story.ui.story

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.smarttech.story.R
import com.smarttech.story.cache.FileCache
import com.smarttech.story.cache.MemoryCache
import com.smarttech.story.constants.Constants
import com.smarttech.story.constants.Repo
import com.smarttech.story.databinding.FragmentStoryBinding
import com.smarttech.story.databinding.FragmentStoryListBinding
import com.smarttech.story.model.dto.StoryViewInfo
import com.smarttech.story.networking.DropboxService
import kotlinx.android.synthetic.main.fragment_story.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class StoryRecyclerViewAdapter(
    val ctx: Context,
    val clickListener: StoryListener
) : ListAdapter<StoryViewInfo, StoryRecyclerViewAdapter.ViewHolder>(StoryDiffCallback()) {
    val repo: Repo = Repo.AVATAR

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var storyViewInfo = getItem(position)
        holder.bind(storyViewInfo!!, clickListener)

        if (storyViewInfo.story.avatar == null || storyViewInfo.story.avatar?.length == 0) {
            return
        }
        if (MemoryCache.getInstance().get(storyViewInfo.story.id) != null) {
            holder.binding.imageView2.setImageBitmap(MemoryCache.getInstance().get(storyViewInfo.story.id))
            return
        }
        // Load avatar
        GlobalScope.launch (Dispatchers.IO) {
            val storyAvatarFile = File(repo.getRepo(ctx.cacheDir), "${storyViewInfo.story.avatar}")
            var b: ByteArray?
            if (storyAvatarFile.exists()) {
                b = storyAvatarFile.readBytes()
            } else {
                val url = repo.getUri("${storyViewInfo.story.avatar}", "${storyViewInfo.story.id}")
                b = LoadFromUrl(url)
                if (b != null) {
                    storyAvatarFile.writeBytes(b)
                }
            }
            if (b != null) {
                val bmp = BitmapFactory.decodeByteArray(b, 0, b.size)
                MemoryCache.getInstance().put(storyViewInfo.story.id, bmp)
                withContext(Dispatchers.Main) {
                    holder.binding.imageView2.setImageBitmap(bmp)
                }
            }
        }
    }

    fun LoadFromUrl(url: String): ByteArray? {
        val response = DropboxService.getInstance().downlload(url).execute()
        val body = response.body()
        if (body == null) {
            return null
        }
        val ins: InputStream = body.byteStream()
        val input = BufferedInputStream(ins)
        val output: ByteArrayOutputStream = ByteArrayOutputStream()

        val data = ByteArray(1024)

        var count = 0
        while (input.read(data).also({ count = it }) !== -1) {
            output.write(data, 0, count)
        }

        output.flush()
        output.close()
        input.close()

        return output.toByteArray()
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