package com.example.todolistapp.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.adapter.TitleAdapter
import com.example.todolistapp.R
import com.example.todolistapp.model.ToDoData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class HomeFragment : Fragment(),ToDoDialogFragment.OnDialogNextBtnClickListener, TitleAdapter.TaskAdapterInterface,TitleAdapter.DescInterface{

    //todo declaration...
    private val TAG = "HomeFragment"
    lateinit var addTaskBtn: FloatingActionButton
    lateinit var mainRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private var frag: ToDoDialogFragment? = null
    private var detailFragment: ToDoDetailsFragment? = null
    private lateinit var authId: String
    private lateinit var titleAdapter: TitleAdapter
    private lateinit var toDoItemList: MutableList<ToDoData>
    private var toDoSubItems : MutableList<ToDoData.Details> = ArrayList()
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        //todo get data from firebase
        getTaskFromFirebase()

        //todo add new title listener..
        addTaskBtn.setOnClickListener {
            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            frag = ToDoDialogFragment()
            frag!!.setListener(this)

            frag!!.show(
                childFragmentManager, ToDoDialogFragment.TAG
            )

        }

        //todo swipe right to delete...
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                return false // true if moved, false otherwise
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                //todo when we swipe our item to right direction. we are getting the item at a particular position.
                val todoList: ToDoData = toDoItemList[viewHolder.adapterPosition]

                //todo dialog popup for delete each items from list...
                alertDialog(todoList)

            }
        }).attachToRecyclerView(mainRecyclerView)

    }

    var key: String = ""

    //todo get data from firebase..
    private fun getTaskFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoItemList.clear()
                for (taskSnapshot in snapshot.children) {
                    Log.d("key------", key)
                    val todoTask = taskSnapshot.key?.let {
                        ToDoData(it, taskSnapshot.value.toString())
                    }
                    if (todoTask != null) {
                        toDoItemList.add(todoTask)
                    }
                }
                Log.d(TAG, "onDataChange: " + toDoItemList)
                titleAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    //todo initializer...
    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        addTaskBtn = view.findViewById(R.id.addTaskBtn)
        mainRecyclerView = view.findViewById(R.id.mainRecyclerView)

        //todo create title database reference with one child node...
        databaseReference = FirebaseDatabase.getInstance().reference.child("Titles")

        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.layoutManager = LinearLayoutManager(context)

        toDoItemList = mutableListOf()
        titleAdapter = TitleAdapter(requireContext(), toDoItemList)
        titleAdapter.setListener(this)
        titleAdapter.setDescListener(this)
        mainRecyclerView.adapter = titleAdapter

    }

    //todo title save..
    override fun saveTask(todoTask: String, todoEdit: TextInputEditText) {
        databaseReference.push().setValue(todoTask)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Task Added Successfully", Toast.LENGTH_SHORT)
                        .show()
                    todoEdit.text = null
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }

    //todo update title name..
    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.titleId] = toDoData.title
        databaseReference.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    //todo alert dialog after swipe items for delete items from list...
    private fun alertDialog(todoList: ToDoData) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialog.setMessage("Please Select any option")
        dialog.setTitle("Dialog Box")
        dialog.setPositiveButton("YES",
            DialogInterface.OnClickListener { dialog, which ->
                //todo remove items from firebase db adapter
                onDeleteItemClicked(todoList)
            })
        dialog.setNegativeButton("NO",
            DialogInterface.OnClickListener { dialog, which ->
                getTaskFromFirebase()
                Toast.makeText(
                    requireContext().applicationContext,
                    "cancel is clicked",
                    Toast.LENGTH_LONG
                ).show()
            })
        val alertDialog: AlertDialog = dialog.create()
        alertDialog.show()
    }

    //todo click listeners for edit and delete task
    fun onDeleteItemClicked(toDoData: ToDoData) {
        Log.e("child_ID---", databaseReference.child(toDoData.titleId).toString())
        databaseReference.child(toDoData.titleId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //todo edit title name...
    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ToDoDialogFragment.newInstance(toDoData.titleId, toDoData.title)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }

    //todo show popup for task details..
    override fun onDescDetailClicked(toDoData: ToDoData, position: Int) {
        if (detailFragment != null)
            childFragmentManager.beginTransaction().remove(detailFragment!!).commit()

        detailFragment = ToDoDetailsFragment.newInstance(toDoData.titleId, toDoData.title)
        detailFragment!!.show(
            childFragmentManager,
            ToDoDetailsFragment.TAG
        )
    }
}