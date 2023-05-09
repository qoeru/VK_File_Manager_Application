package com.example.vkfilemanagerapplication

import java.io.File

interface OnFileClickedListener {
    fun onFileShortClicked(file: File)
    // fun onFileLongClicked(fileList: Lis show: Boolean)

    fun onTrashCanShortClicked()
}