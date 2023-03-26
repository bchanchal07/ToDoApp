package com.example.demoapp.practice1.model

data class ToDoData(
    var titleId:String,
    var title: String,
    var details:List<Details> = ArrayList()
    ){
    data class Details(
        val desc : String,
    )

}
