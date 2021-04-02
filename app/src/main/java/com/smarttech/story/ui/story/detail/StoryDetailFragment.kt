package com.smarttech.story.ui.story.detail

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.smarttech.story.MainActivity
import com.smarttech.story.R
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.databinding.FragmentStorydetailBinding
import com.smarttech.story.model.local.Bookmark
import com.smarttech.story.model.local.Download
import com.smarttech.story.ui.story.detail.chapter.ChapterListFragment
import com.smarttech.story.ui.story.detail.desc.StoryDescFragment
import com.smarttech.story.utils.AvatarUtil
import com.smarttech.story.utils.ChapterUtil
import kotlinx.android.synthetic.main.fragment_storydetail.*
import kotlinx.coroutines.*

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bookmark_menu -> {
                Toast.makeText(context, "Đang thêm vào danh sách đánh dấu...", Toast.LENGTH_SHORT)
                    .show()
                // add history
                val storyDao = AppDatabase(requireContext()).storyDao()
                if (!storyDao.bookmarkExist(storyId)) {
                    var bookmark = Bookmark(storyId)
                    storyDao.insertBookmarkLocal(bookmark)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById(R.id.chap_list_vp)
        var tabAdapter = TabAdapter(storyId, this)
        viewPager.adapter = tabAdapter
        tabLayout = view.findViewById(R.id.chap_list_tl)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Mô tả"
                1 -> tab.text = "Danh sách chương"
            }
        }.attach()
        downloadStory.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Đang tải về offline...", Toast.LENGTH_SHORT).show()
            // add download
            GlobalScope.launch(Dispatchers.IO) {
                val chapterDtos =
                    context?.let { it1 -> ChapterUtil.getChapterListFromServer(it1, storyId) }
                chapterDtos?.forEach {
                    GlobalScope.async(Dispatchers.IO) {
                        context?.let { it1 ->
                            ChapterUtil.downloadChapterFromServer(it1,
                                storyId,
                                it.index,
                                it.key)
                        }
                    }
                }
            }
            val storyDao = AppDatabase(requireContext()).storyDao()
            if (!storyDao.downloadExist(storyId)) {
                var donwnload = Download(storyId)
                AppDatabase(requireContext()).storyDao().insertDownloadLocal(donwnload)
            }
        })
        readStoryBtn.setOnClickListener(View.OnClickListener {
            val action =
                StoryDetailFragmentDirections
                    .actionStoryDetailFragmentToChapterFragment("",
                        -1,
                        storyId = storyId,
                        storyName = storyName,
                        chapterTitle = "")
            findNavController().navigate(action)
            (activity as MainActivity).supportActionBar!!.hide()
            (activity as MainActivity).findViewById<View>(R.id.nav_view).visibility =
                View.GONE

        })

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
            StoryDetailViewModelFactory(Application(), requireContext(), storyId, storyName)
        storyDetailViewModel =
            ViewModelProvider(this, viewModelFactory).get(StoryDetailViewModel::class.java)
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.storyDetailViewModel = storyDetailViewModel

        AvatarUtil.bindFromLocal(requireContext(), binding.avatar, storyId)
        storyDetailViewModel.story.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.progressBarLoading.visibility = View.GONE
                //binding.categoryList.adapter = adapter

            }
        })
/*        readStoryBtn.setOnClickListener(View.OnClickListener {
            val action = StoryDetailFragmentDirections
                .actionStoryDetailFragmentToChapterFragment(chapterDto.key,
                    chapterDto.index,
                    storyId = storyId,
                    storyName = storyName,
                    chapterTitle = chapterDto.title)
            this.findNavController().navigate(action)
            (activity as MainActivity).supportActionBar!!.hide()
            (activity as MainActivity).findViewById<View>(R.id.nav_view).visibility = View.GONE
            viewModel.onChapterNavigated()
        })*/

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