package com.example.vkfilemanagerapplication

import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.Arrays
import java.util.function.Function


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StorageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StorageFragment : Fragment(), OnFileClickedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var filesAndFolders: RecyclerView
    private lateinit var fileList: ArrayList<File>
    private lateinit var thePath: TextView
    private lateinit var storage: File
    private lateinit var data: String
    private lateinit var trashCan: ImageButton
    private lateinit var adapter: FileAdapter

    private lateinit var sortButton: ImageButton
    private lateinit var reverseSort: ImageButton

    private var listOfSelectedFiles = mutableListOf<File>()
    private var descending = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_storage, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thePath = view.findViewById(R.id.path)
        trashCan = view.findViewById(R.id.delete_button)
        reverseSort = view.findViewById(R.id.reverse_sort)
        sortButton = view.findViewById(R.id.sort_button)

        val internalPath = Environment.getExternalStorageDirectory().absolutePath
        storage = File(internalPath)

        try {
            data = requireArguments().getString("path").toString()
            storage = File(data)
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

        thePath.text = storage.absolutePath

        filesAndFolders = view.findViewById(R.id.files_and_folders)
        fileList = findFiles(storage)

        fileList.sort()

        trashCan.setOnClickListener {
            onTrashCanShortClicked()
        }

        reverseSort.setOnClickListener {
            onReverseSortClicked()
            adapter.notifyDataSetChanged()
        }

        val popUpView = view.findViewById<View>(R.id.menu_space)

        val popupMenu = PopupMenu(context, popUpView)
        popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)

        sortButton.setOnClickListener {
            onSortClicked(popupMenu)
        }

        adapter = FileAdapter(requireContext(), fileList, this)

        filesAndFolders.adapter = adapter
        filesAndFolders.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StorageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StorageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun findFiles(file: File): ArrayList<File>
    {
        file.setReadable(true)
        val arrayList = ArrayList<File>()
        val files = file.listFiles()!!
        for(currentFile in files) {
            if (!currentFile.isHidden) {
                arrayList.add(currentFile)
            }
        }
        return arrayList
    }

    private fun sortReverseOrder()
    {
        fileList.reverse()
    }

    override fun onFileShortClicked(file: File) {
        if(file.isDirectory)
        {
            val bundle= Bundle()
            bundle.putString("path", file.absolutePath)
            val storageFragment = StorageFragment()
            storageFragment.arguments = bundle
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, storageFragment).addToBackStack(null)
            transaction.commit()
        } else {
            try {
                val fileOpener = FileOpener()
                fileOpener.openFile(requireContext(), file)
            } catch (e: IOException)
            {
                e.printStackTrace()
            }
        }
    }

    private fun onSortClicked(popupMenu: PopupMenu)
    {
        popupMenu.setOnMenuItemClickListener {
            onPopUpMenuItemClicked(it)
        }
        popupMenu.show()
    }

    private fun onPopUpMenuItemClicked(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.date_created_sort -> {
                sortByDate()
                adapter.notifyDataSetChanged()
                return true
            }
            R.id.name_sort -> {
                sortByName()
                adapter.notifyDataSetChanged()
                return true
            }
            R.id.extension_sort -> {
                sortByFileType()
                adapter.notifyDataSetChanged()
                return true
            }
            else -> return false
        }
    }

    private fun sortByFileType() {
        reverseSort.setImageResource(R.drawable.ic_round_arrow_circle_down_24)
        descending = true
        fileList.sortWith(compareBy { it.extension } )

    }

    private fun sortByName() {
        reverseSort.setImageResource(R.drawable.ic_round_arrow_circle_down_24)
        descending = true
        fileList.sort()
    }

    private fun sortByDate()
    {
        reverseSort.setImageResource(R.drawable.ic_round_arrow_circle_down_24)
        descending = true
        fileList.sortWith(compareBy { it.lastModified() } )
    }


    private fun onReverseSortClicked() {
        descending = if (descending) {
            reverseSort.setImageResource(R.drawable.ic_round_arrow_circle_up_24)
            false
        } else {
            reverseSort.setImageResource(R.drawable.ic_round_arrow_circle_down_24)
            true
        }
        sortReverseOrder()
    }

//    override fun onFileLongClicked(show: Boolean) {
//        showTrashCan(show)
//
//    }

    override fun onTrashCanShortClicked() {
        showDeleteAlertDialogue()
    }

//    private fun showTrashCan(show: Boolean)
//    {
//        trashCan.isVisible = show
//    }

    private fun showDeleteAlertDialogue()
    {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Удаление")
        alertDialog.setMessage("Вы уверены?")
        alertDialog.setPositiveButton("Удалить") { _,_ ->
            for(file in listOfSelectedFiles)
            {
                file.delete()
            }
        }
        alertDialog.setNegativeButton("Отмена") {_,_ -> }
        alertDialog.show()
    }

}