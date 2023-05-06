package com.example.vkfilemanagerapplication

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileAdapter(
    private var context: Context,
    private var files: ArrayList<File>
) :
    RecyclerView.Adapter<FileAdapter.FileContainer>() {

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

        if (files[position].name.lowercase().endsWith(".png") or
            files[position].name.lowercase().endsWith(".jpg"))
        {
            holder.fileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_picture_24, 0, 0, 0)
        }
        else if(files[position].name.lowercase().endsWith(".pdf"))
        {
            holder.fileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_pdf_24, 0, 0, 0)
        }
        else if(files[position].name.lowercase().endsWith(".mp4"))
        {
            holder.fileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_video_file_24, 0, 0, 0)
        }
        else if (files[position].name.lowercase().endsWith(".txt"))
        {
            holder.fileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_text_24, 0, 0, 0)
        }
        else if(!files[position].isDirectory)
        {
            holder.fileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_question_mark_24, 0, 0, 0)
        }
        else {
            holder.fileName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_folder_24, 0, 0, 0)
        }
    }

    class FileContainer(itemView: View) : RecyclerView.ViewHolder(itemView){
        val fileName: TextView = itemView.findViewById(R.id.file_name)
        val fileSize: TextView = itemView.findViewById(R.id.size)
    }

}
