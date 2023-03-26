package com.example.todolistapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.todolistapp.R
import com.example.todolistapp.model.ToDoData
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView

class DetailsFragment : DialogFragment() {

    private val TAG = "ToDoFragment"
    lateinit var titleName: TextView
    lateinit var titleDesc: TextView
    lateinit var iv_set_profile: CircleImageView
    lateinit var detailClose: ImageView

    private var listener : DetailsFragment.OnDialogNextBtnClickListener? = null
    private var toDoData: ToDoData? = null

    interface OnDialogNextBtnClickListener{
        fun saveTask(todoTask: String, todoEdit: TextInputEditText)
    }

    companion object {
        const val TAG = "DetailsFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("titleId", taskId)
                    putString("title", task)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        if (arguments != null){
//            detailData = DetailsModel(arguments?.getString("desc").toString() ,arguments?.getString("image").toString())
            toDoData = ToDoData(arguments?.getString("titleId").toString() ,arguments?.getString("title").toString())
            titleName.text = toDoData?.title
            titleDesc.setText(toDoData?.titleId)
        }

        detailClose.setOnClickListener {
            dismiss()
        }

    }

    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    private fun init(view: View) {
        titleName = view.findViewById(R.id.titleName)
        titleDesc = view.findViewById(R.id.titleDesc)
        iv_set_profile = view.findViewById(R.id.iv_set_profile)
        detailClose = view.findViewById(R.id.detailClose)
    }
}