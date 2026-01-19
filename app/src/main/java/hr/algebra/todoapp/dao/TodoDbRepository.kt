package hr.algebra.todoapp.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.algebra.todoapp.model.Task

private const val DB_NAME = "tasks.db"
private const val DB_VERSION = 2
private const val TABLE_NAME = "tasks"
private val CREATE_TABLE = "create table $TABLE_NAME( " +
        "${Task::_id.name} integer primary key autoincrement, " +
        "${Task::title.name} text not null, " +
        "${Task::notes.name} text, " +
        "${Task::dueDate.name} long, " +
        "${Task::done.name} integer not null default 0, " +
        "${Task::priority.name} integer not null default 0 " +
        ")"
private const val DROP_TABLE = "drop table if exists $TABLE_NAME"

class TodoDbRepository(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),
    TodoRepository {

    override fun delete(
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = writableDatabase.delete(
        TABLE_NAME,
        selection,
        selectionArgs
    )

    override fun insert(values: ContentValues?)
     = writableDatabase.insert(
         TABLE_NAME,
        null,
        values
    )

    override fun query(
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor = readableDatabase.query(
        TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
)

    override fun update(
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = writableDatabase.update(
        TABLE_NAME,
        values,
        selection,
        selectionArgs
    )

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }
}