package com.smarttech.story.ui.story

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.smarttech.story.R
import kotlinx.android.synthetic.main.bottom_sheet_story.*
import java.lang.RuntimeException

class StoryBottomDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        val TAG = "ActionBottomDialog"
        fun newInstance(): StoryBottomDialogFragment {
            return StoryBottomDialogFragment()
        }
    }

    private var mListener: ItemClickListener? = null

    interface ItemClickListener {
        fun onItemClick(item: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*        sortRB.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i -> {
            val x =0
        }  })*/
        sortRB.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = view.findViewById(checkedId)
            var id: Int = group.checkedRadioButtonId
            val navController = findNavController();
            val saveSate = navController.previousBackStackEntry?.savedStateHandle
            when (radio.id) {
                radioButtonUpdate.id -> saveSate?.set("key", StoryFilterEnum.UPDATE.filter.toString())
                radioButtonRate.id -> saveSate?.set("key", StoryFilterEnum.RATE.filter.toString())
                radioButtonView.id -> saveSate?.set("key", StoryFilterEnum.VIEW.filter.toString())
                radioButtonChapterTotal.id -> saveSate?.set("key", StoryFilterEnum.CHAPTER.filter.toString())
                radioButtonName.id -> saveSate?.set("key", StoryFilterEnum.NAME.filter.toString())
            }
            dismiss()
        }
/*        share_textview.setOnClickListener(this)
        upload_textview.setOnClickListener(this)
        copy_textview.setOnClickListener(this)
        print_textview.setOnClickListener(this)*/
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
/*        if (context is ItemClickListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement ItemClickListener.")
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(view: View?) {


        val tvSelected: TextView = view as TextView
        mListener?.onItemClick(tvSelected.text.toString())
    }
}

private fun RadioGroup.setOnCheckedChangeListener() {
    TODO("Not yet implemented")
}
