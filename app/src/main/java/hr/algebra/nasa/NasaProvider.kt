package hr.algebra.nasa

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import hr.algebra.nasa.dao.NasaRepository
import hr.algebra.nasa.dao.getNasaRepository
import hr.algebra.nasa.model.Item

private const val AUTHORITY = "hr.algebra.nasa.api.provider"
private const val PATH = "items"
val NASA_PROVIDER_CONTENT_URI: Uri = "content://$AUTHORITY/$PATH".toUri()

//content://hr.algebra.nasa.api.provider/items => ITEMS - ALL
//content://hr.algebra.nasa.api.provider/items/2 => SINGLE

private const val ITEMS = 10
private const val ITEM_ID = 20
private val URI_MATCHER = with(UriMatcher(UriMatcher.NO_MATCH)){2
    //content://hr.algebra.nasa.api.provider/items => ITEMS - ALL
    addURI(AUTHORITY, PATH, ITEMS)
    //content://hr.algebra.nasa.api.provider/items/2 => SINGLE
    addURI(AUTHORITY, "$PATH/#", ITEM_ID)
    this
}
class NasaProvider : ContentProvider() {

    private lateinit var repository: NasaRepository

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when(URI_MATCHER.match(uri)) {
            ITEMS -> return repository.delete(selection, selectionArgs)
            ITEM_ID -> {
                val id = uri.lastPathSegment
                if(id != null)
                    return repository.delete("${Item::_id.name}=?", arrayOf(id))
            }
        }
        throw IllegalArgumentException("Wrong uri")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }


    //content://hr.algebra.nasa.api.provider/items/22

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = repository.insert(values) // 22
        return ContentUris.withAppendedId(NASA_PROVIDER_CONTENT_URI, id)
    }

    override fun onCreate(): Boolean {
        repository = getNasaRepository(context)
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
            ITEMS -> return repository.update(values, selection, selectionArgs)
            ITEM_ID -> {
                val id = uri.lastPathSegment
                if(id != null)
                    return repository.update(values, "${Item::_id.name}=?", arrayOf(id))
            }
        }
        throw IllegalArgumentException("Wrong uri")

    }
}