package hr.algebra.todoapp

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import hr.algebra.todoapp.dao.TodoRepository
import hr.algebra.todoapp.dao.getToDoRepository
import hr.algebra.todoapp.model.Task

private const val AUTHORITY = "hr.algebra.todoapp.provider"
private const val PATH = "tasks"
val TODO_PROVIDER_CONTENT_URI: Uri = "content://$AUTHORITY/$PATH".toUri()

//content://hr.algebra.todoapp.api.provider/items => ITEMS - ALL
//content://hr.algebra.todoapp.api.provider/items/2 => SINGLE

private const val TASKS = 10
private const val TASK_ID = 20
private val URI_MATCHER = with(UriMatcher(UriMatcher.NO_MATCH)){2
    //content://hr.algebra.todoapp.api.provider/items => ITEMS - ALL
    addURI(AUTHORITY, PATH, TASKS)
    //content://hr.algebra.todoapp.api.provider/items/2 => SINGLE
    addURI(AUTHORITY, "$PATH/#", TASK_ID)
    this
}
class TodoProvider : ContentProvider() {

    private lateinit var repository: TodoRepository

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when(URI_MATCHER.match(uri)) {
            TASKS -> return repository.delete(selection, selectionArgs)
            TASK_ID -> {
                val id = uri.lastPathSegment
                if(id != null)
                    return repository.delete("${Task::_id.name}=?", arrayOf(id))
            }
        }
        throw IllegalArgumentException("Wrong uri")
    }

    override fun getType(uri: Uri): String? =
        when (URI_MATCHER.match(uri)) {
            TASKS -> "vnd.android.cursor.dir/vnd.${AUTHORITY}.tasks"
            TASK_ID -> "vnd.android.cursor.item/vnd.${AUTHORITY}.tasks"
            else -> null
        }



    //content://hr.algebra.todoapp.api.provider/items/22

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = repository.insert(values) // 22
        return ContentUris.withAppendedId(TODO_PROVIDER_CONTENT_URI, id)
    }

    override fun onCreate(): Boolean {
        repository = getToDoRepository(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor = repository.query(
        projection,
        selection,
        selectionArgs,
        sortOrder
    )

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when(URI_MATCHER.match(uri)) {
            TASKS -> return repository.update(values, selection, selectionArgs)
            TASK_ID -> {
                val id = uri.lastPathSegment
                if(id != null)
                    return repository.update(values, "${Task::_id.name}=?", arrayOf(id))
            }
        }
        throw IllegalArgumentException("Wrong uri")

    }
}