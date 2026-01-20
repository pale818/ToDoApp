package hr.algebra.todoapp.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import hr.algebra.todoapp.R
import hr.algebra.todoapp.model.Task
import kotlin.coroutines.coroutineContext

private const val DB_NAME = "tasks.db"

// INCREASE IN ORDER TO GO INSIDE THE FUNCTION onUpgrade
private const val DB_VERSION = 6
private const val TABLE_NAME = "tasks"
private val CREATE_TABLE = "create table $TABLE_NAME( " +
        "${Task::_id.name} integer primary key autoincrement, " +
        "${Task::title.name} text not null, " +
        "${Task::notes.name} text, " +
        "${Task::dueDate.name} long, " +
        "${Task::done.name} integer not null default 0, " +
        "${Task::priority.name} integer not null default 0 " +
        //"${Task::category.name} text not null default 'personal' " +
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
        if (db == null) return

        Log.e("paola: DB_CREATE", "onCreate")
        val c = db.rawQuery("PRAGMA table_info($TABLE_NAME)", null)
        c.use {
            while (it.moveToNext()) {
                Log.e("DB_COL", it.getString(1)) // column name
            }
        }

        db?.execSQL(CREATE_TABLE)

    }

    /*override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }*/

    fun logColumns(db: SQLiteDatabase) {
        val c = db.rawQuery("PRAGMA table_info($TABLE_NAME)", null)
        c.use {
            while (it.moveToNext()) {
                Log.e("paola: DB_COL", it.getString(1))
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.e("paola: DB_UPGRADE", "onUpgrade $oldVersion -> $newVersion")

        // MIGRATION
        // 1) add category
        logColumns(db)
        if (oldVersion < DB_VERSION) {
            db.execSQL(
                "ALTER TABLE $TABLE_NAME ADD COLUMN category TEXT NOT NULL DEFAULT 'personal'"
            )
        }
        logColumns(db)

        // 2) return to original table(remove category),data loss
        /*
        logColumns(db)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL(CREATE_TABLE)
        logColumns(db)

         */
    }
}