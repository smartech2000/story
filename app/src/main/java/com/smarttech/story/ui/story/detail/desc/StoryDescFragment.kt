package com.smarttech.story.ui.story.detail.desc

import android.app.Application
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.smarttech.story.R

class StoryDescFragment : Fragment() {

    companion object {
        fun newInstance() = StoryDescFragment()
    }

    var storyId: Int = 0
    var storyName: String = ""
    private lateinit var viewModel: StoryDescViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            storyId = it.getInt("storyId")
            var x = 0
        }

        //notify the fragment that it should participate in options menu handling.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModelFactory =
            StoryDescViewModelFactory(Application(), storyId)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(StoryDescViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_story_desc, container, false)
        val textView: TextView = root.findViewById(R.id.story_desc_tv)
        viewModel.storyDesc.observe(viewLifecycleOwner, Observer {
            textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(it)
            }
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StoryDescViewModel::class.java)
        // TODO: Use the ViewModel
    }

}