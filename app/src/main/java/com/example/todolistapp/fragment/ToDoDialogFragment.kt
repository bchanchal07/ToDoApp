package com.example.demoapp.practice1.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.demoapp.R
import com.example.demoapp.practice1.model.ToDoData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class ToDoDialogFragment : DialogFragment() {
    private val TAG = "ToDoFragment"
    lateinit var todoClose: ImageView
    lateinit var todoEt: TextInputEditText
    lateinit var descEt: TextInputEditText
    lateinit var todoNextBtn: FloatingActionButton

    private var listener : OnDialogNextBtnClickListener? = null
    private var toDoData: ToDoData? = null

    interface OnDialogNextBtnClickListener{
        fun saveTask(todoTask: String, todoEdit: TextInputEditText, descText: String)
        fun updateTask(toDoData: ToDoData , todoEdit:TextInputEditText)
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) = ToDoDialogFragment().apply {
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
        return inflater.inflate(R.layout.fragment_to_do_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        if (arguments != null){
            toDoData = ToDoData(arguments?.getString("titleId").toString() ,arguments?.getString("title").toString())
            todoEt.setText(toDoData?.title)
        }

        todoClose.setOnClickListener {
            dismiss()
        }

        //todo update task or cancel..
        todoNextBtn.setOnClickListener {
            val todoTask = todoEt.text.toString()
            var descText = descEt.text.toString()
            if (todoTask.isNotEmpty()){
                if (toDoData == null){
                    listener?.saveTask(todoTask , todoEt, descText)
                }else{
                    toDoData!!.title = todoTask
                    listener?.updateTask(toDoData!!, todoEt)
                }
            }
        }

    }

    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    private fun init(view: View) {
        todoClose = view.findViewById(R.id.todoClose)
        todoEt = view.findViewById(R.id.todoEt)
        todoNextBtn = view.findViewById(R.id.todoNextBtn)
        descEt = view.findViewById(R.id.descEt)
    }
}