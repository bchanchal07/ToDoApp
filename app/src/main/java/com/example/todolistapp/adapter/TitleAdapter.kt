package com.example.demoapp.practice1.adapter

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R
import com.example.demoapp.practice1.model.ToDoData

class TitleAdapter(private val context : Context ,list: MutableList<ToDoData>) : RecyclerView.Adapter<TitleAdapter.TaskViewHolder>()  {

    private var list:MutableList<ToDoData> = arrayListOf()
    init{
        this.list = list
    }

    private  val TAG = "TaskAdapter"
    private var listener:TaskAdapterInterface? = null
    private var addListener: AddDescriptionInterface? = null
    private var descListener: DescInterface? = null

    interface TaskAdapterInterface{
        fun onEditItemClicked(toDoData: ToDoData , position: Int)
    }

    interface AddDescriptionInterface {
        fun onAddDetailsClicked(toDoData: ToDoData, position: Int, titleId: String)
    }

    interface DescInterface {
        fun onDescDetailClicked(toDoData: ToDoData, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_todo_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        var list :ToDoData = list[position]

        holder.todoTask.text = list.title

        Log.d(TAG, "onBindViewHolder: "+this)

        //todo click listeners for edit and delete task
        holder.editTask.setOnClickListener {
            listener?.onEditItemClicked(list, position)
        }

        //todo strike off text on check box
        holder.checkBox.setOnClickListener {
            if (!holder.todoTask.paint.isStrikeThruText()) {
                holder.todoTask.paintFlags = holder.todoTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.todoTask.paintFlags = holder.todoTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        //todo add desc and image..
        holder.addDetail.setOnClickListener {
            addListener?.onAddDetailsClicked(list, position, list.titleId)
        }

        //todo show details...
        holder.constraint_layout.setOnClickListener {
            descListener?.onDescDetailClicked(list, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class TaskViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        var todoTask : TextView = itemView.findViewById(R.id.todoTask)
        var editTask : ImageView = itemView.findViewById(R.id.editTask)
        var checkBox : CheckBox = itemView.findViewById(R.id.checkBox)
        var addDetail : ImageView = itemView.findViewById(R.id.addDetail)
        var constraint_layout : ConstraintLayout = itemView.findViewById(R.id.constraint_layout)
    }

    fun setListener(listener:TaskAdapterInterface){
        this.listener = listener
    }

    fun setAddListener(addListener: AddDescriptionInterface){
        this.addListener = addListener
    }

    fun setDescListener(descListener: DescInterface){
        this.descListener = descListener
    }
}