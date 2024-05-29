package com.example.easyutil

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.linear(linear: Boolean = true): RecyclerView {
    layoutManager =
        if (linear) LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        else LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    return this
}

fun RecyclerView.header(): RecyclerView{

    return this
}