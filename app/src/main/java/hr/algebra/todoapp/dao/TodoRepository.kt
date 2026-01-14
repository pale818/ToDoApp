package hr.algebra.todoapp.dao

import android.content.ContentValues
import android.database.Cursor

interface TodoRepository {
    fun delete(
        selection: String?,
        selectionArgs: Array<String>?
    ): Int
    fun insert(values: ContentValues?): Long
    fun query(
        projection: Array<String>?,
        selection: String?, //"name = '%?' and price > ?",
        selectionArgs: Array<String>?, // [0] = 'Z', [1] = 100
        sortOrder: String?
    ): Cursor
    fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int

}