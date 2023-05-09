package com.example.vkfilemanagerapplication

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileAdapter(
    private var context: Context,
    private var files: ArrayList<File>,
    private val listener: OnFileClickedListener
) :
    RecyclerView.Adapter<FileAdapter.FileContainer>() {

    private var isEnabled = false
    private val itemSelectedList = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileContainer {
        return FileContainer(LayoutInflater.from(parent.context).inflate(R.layout.file_container, parent, false))
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: FileContainer, position: Int) {
        holder.fileName.text = files[position].name
        holder.fileName.isSelected = true
                    var items = 0

                    if(files[position].isDirectory) {
                        val currentFilesInDirectory = files[position].listFiles()
                        for (currentFile in currentFilesInDirectory!!) {
                            if (!currentFile.isHidden) {
                    items++
                }
            }
            holder.fileSize.text = context.getString(R.string.sizeFile, items)
        } else {
            holder.fileSize.text = Formatter.formatShortFileSize(context, files[position].length())
        }

        val mapOfFormats = mutableMapOf(".png" to R.drawable.ic_round_picture_24,
            ".jpg" to R.drawable.ic_round_picture_24,
            ".jpeg" to R.drawable.ic_round_picture_24,
            ".pdf" to R.drawable.ic_round_pdf_24,
            ".mp4" to R.drawable.ic_round_video_file_24,
            ".txt" to R.drawable.ic_round_text_24,
            ".wav" to R.drawable.ic_round_library_music_24,
            ".mp3" to R.drawable.ic_round_library_music_24)

        var formatFound = false

        mapOfFormats.forEach{format ->
            if(files[position].name.lowercase().endsWith(format.key)) {
                holder.fileName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    format.value,
                    0,
                    0,
                    0
                )
                formatFound = true
            }
        }

        if(!formatFound)
        {
            if(files[position].isDirectory)
            {
                holder.fileName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_round_folder_24,
                    0,
                    0,
                    0
                )
            } else
            {
                holder.fileName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_round_question_mark_24,
                    0,
                    0,
                    0
                )
            }
        }



        holder.itemView.setOnClickListener (View.OnClickListener {
//            if(isEnabled)
//            {
//                if(!itemSelectedList.contains(position))
//                {
//                    selectItem(holder, position)
//                }
//                else {
//                    holder.checked.isVisible = false
//                    holder.fileSize.isVisible = true
//                    if(itemSelectedList.isEmpty())
//                    {
//                        listener.onFileLongClicked(files[position], false)
//                        isEnabled = false
//                    }
//                    itemSelectedList.remove(position)
//                }
//            } else {
                listener.onFileShortClicked(files[position])
//            }
        })

//        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
//            isEnabled = true
//            listener.onFileLongClicked(files[position], true)
//            selectItem(holder, position)
//            true
//        })

    }

    private fun selectItem(holder: FileContainer, position: Int)
    {
        holder.fileSize.isVisible = false
        holder.checked.isVisible = true
        itemSelectedList.add(position)
    }

    class FileContainer(itemView: View) : RecyclerView.ViewHolder(itemView){
        val checked: ImageView = itemView.findViewById(R.id.checked)
        val fileName: TextView = itemView.findViewById(R.id.file_name)
        val fileSize: TextView = itemView.findViewById(R.id.size)
    }



}
