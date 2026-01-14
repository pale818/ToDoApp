package hr.algebra.todoapp.dao

import android.content.Context

fun getToDoRepository(context: Context?) = TodoDbRepository(context)