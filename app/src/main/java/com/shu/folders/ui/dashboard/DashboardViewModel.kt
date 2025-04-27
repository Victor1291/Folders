package com.shu.folders.ui.dashboard

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shu.domain.util.PathUtils
import com.shu.folders.models.Folder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {


    private val _folders = MutableLiveData<List<Folder>>()
    val folders: LiveData<List<Folder>> get() = _folders

    init {
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            try {
                _folders.postValue(queryFolders())
            } catch (e: CancellationException) {
                Log.d("dash", " Error -CancellationException- $e")
                throw e
            } catch (e: Exception) {
                Log.d("dash", " Error $e")
            }

        }
    }

    private suspend fun queryFolders(): List<Folder> {
        //val folders = mutableListOf<Folder>()
        val list = ArrayList<Folder>()
        /**
         * Working with [ContentResolver]s can be slow, so we'll do this off the main
         * thread inside a coroutine.
         */
        withContext(Dispatchers.IO) {


            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_MODIFIED,
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val args2 = Bundle().apply {
                // Set the limit and offset for pagination
                /* putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                 putInt(ContentResolver.QUERY_ARG_OFFSET, offset)

                 // Set the sort order
                 putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(order))*/
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    // if (ascending)
                    ContentResolver.QUERY_SORT_DIRECTION_ASCENDING
                    // else
                    //     ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
                // Set the selection arguments and selection string
                //  if (args != null) putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, args)
                // Add the selection string.
                // TODO: Consider adding support for group by.
                // Currently, using group by on Android 10results in errors,
                // and the argument for group by is only supported on Android 11 and above.
                //TODO  putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                // Include trashed files in the result for devices
                // running Android 11 and above
                // The presence of trashed files will be controlled by the 'isTrashed'
                // column in the result.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    putInt(MediaStore.QUERY_ARG_MATCH_TRASHED, 1)
                }
            }
            getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                args2,
                null,
            )?.use { c ->

                val idColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val pathColumn =
                    c.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
                val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val lastModifiedColumn =
                    c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

                Log.i(TAG, "Found ${c.count} folders")

                while (c.moveToNext()) {

                    // Here we'll use the column indexs that we found above.
                    val id = c.getLong(idColumn)
                    val path = c.getString(pathColumn)
                    val size = c.getInt(sizeColumn)
                    val lastModified = c.getLong(lastModifiedColumn) * 1000

                    val parent = PathUtils.parent(path).let {
                        if (PathUtils.name(it).startsWith("img", true)) PathUtils.parent(it)
                        else it
                    }
                    val index = list.indexOfFirst { it.path == parent }
                    if (index == -1) {
                        list += Folder(id, parent, 1, size, lastModified)
                        continue
                    }
                    val old = list[index]
                    val artwork = if (old.lastModified > lastModified) old.artworkID else id

                    list[index] = Folder(
                        artwork,
                        parent,
                        old.count + 1,
                        old.size + size,
                        maxOf(old.lastModified, lastModified)
                    )
                }
            }
        }

        Log.v(TAG, "Found ${list.size} images")
        return list
    }

    /**
     * Convenience method to convert a day/month/year date into a UNIX timestamp.
     *
     * We're suppressing the lint warning because we're not actually using the date formatter
     * to format the date to display, just to specify a format to use to parse it, and so the
     * locale warning doesn't apply.
     */
    @Suppress("SameParameterValue")
    @SuppressLint("SimpleDateFormat")
    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            TimeUnit.MICROSECONDS.toSeconds(formatter.parse("$day.$month.$year")?.time ?: 0)
        }


}

private const val TAG = "viewDashBoardVM"

/**
 * Convenience extension method to register a [ContentObserver] given a lambda.
 */
private fun ContentResolver.registerObserver(
    uri: Uri,
    observer: (selfChange: Boolean) -> Unit
): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}
