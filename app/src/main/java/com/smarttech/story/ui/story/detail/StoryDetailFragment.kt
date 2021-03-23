package com.smarttech.story.ui.story.detail

import android.app.Application
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.smarttech.story.R
import com.smarttech.story.cache.MemoryCache
import com.smarttech.story.constants.Repo
import com.smarttech.story.databinding.FragmentStorydetailBinding
import com.smarttech.story.ui.category.*
import com.smarttech.story.ui.story.detail.chapter.ChapterListFragment
import com.smarttech.story.ui.story.detail.desc.StoryDescFragment
import java.io.File

/**
 * A fragment representing a list of Items.
 */
class StoryDetailFragment : Fragment() {
    private lateinit var storyDetailViewModel: StoryDetailViewModel
    private var columnCount = 1
    var storyId: Int = 0
    var storyName: String = ""
    val args: StoryDetailFragmentArgs by navArgs()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            storyId = args.storyId
            storyName = args.storyName
        }

        //notify the fragment that it should participate in options menu handling.
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO Add your menu entries here
        // menu?.findItem(R.id.download_menu)?.isVisible = true
        menu?.findItem(R.id.bookmark_menu)?.isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById(R.id.chap_list_vp)
        var tabAdapter = TabAdapter(storyId,this)
        viewPager.adapter = tabAdapter
        tabLayout = view.findViewById(R.id.chap_list_tl)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text ="Mô tả"
                1 -> tab.text ="Danh sách chương"
            }
        }.attach()
        var x = 0
    }


    class TabAdapter(
        storyId: Int,
        fragment: Fragment
    ) : FragmentStateAdapter(fragment) {
        private var storyId: Int = storyId

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            // Return a NEW fragment instance in createFragment(int)
            var fragment: Fragment

            when (position) {
                0 -> {
                    fragment = StoryDescFragment.newInstance()
                    fragment.arguments = Bundle().apply {
                        // Our object is just an integer :-P
                        putInt("storyId", storyId)
                    }
                }
                1 -> {
                    fragment = ChapterListFragment.newInstance()
                    fragment.arguments = Bundle().apply {
                        // Our object is just an integer :-P
                        putInt("storyId", storyId)
                    }
                }
                else -> {
                    fragment = StoryDescFragment.newInstance()
                    fragment.arguments = Bundle().apply {
                        // Our object is just an integer :-P
                        putInt("storyId", storyId)
                    }
                }
            }

            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.requireActivity().invalidateOptionsMenu()
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentStorydetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_storydetail, container, false
        )
        val viewModelFactory =
            StoryDetailViewModelFactory(Application(), context!!, storyId, storyName)
        storyDetailViewModel =
            ViewModelProvider(this, viewModelFactory).get(StoryDetailViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.storyDetailViewModel = storyDetailViewModel

        var bmp = MemoryCache.getInstance().get(storyId)
        if (bmp == null) {
            val avatarFile = File(Repo.AVATAR.getRepo(context!!.cacheDir), "$storyId")
            if (avatarFile.exists()) {
                val b = avatarFile.readBytes()
                bmp = BitmapFactory.decodeByteArray(b, 0, b.size)
            }
        }
        if (bmp != null) {
            binding.imageView2.setImageBitmap(bmp)
        }

        return binding.root
    }

    companion object {
        private const val ARG_OBJECT = "object"

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            StoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}