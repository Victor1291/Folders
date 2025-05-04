package com.shu.folders.ui.files

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class FilesViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

   // private var imagesList: List<Long> = emptyList()

    private var _images = MutableStateFlow<List<Long>>(emptyList())
    val images: StateFlow<List<Long>> = _images.asStateFlow()


    fun setList(list: List<Long>) {
        _images.value = list.reversed()
    }

    /* fun loadUri() {
         viewModelScope.launch {
             val uriList = mutableListOf<Uri>()

             imagesList.forEachIndexed { index, id ->
                 val contentUri = ContentUris.withAppendedId(
                     MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                     id
                 )
                 uriList.add(contentUri)
             }
             _images.value= uriList
         }
     }*/
}