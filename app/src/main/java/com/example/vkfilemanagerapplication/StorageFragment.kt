package com.example.vkfilemanagerapplication

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StorageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StorageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var filesAndFolders: RecyclerView
    private lateinit var fileList: ArrayList<File>
    private lateinit var thePath: TextView
    private lateinit var storage: File

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

        val internalPath = Environment.getExternalStorageDirectory().absolutePath
        storage = File(internalPath)

        thePath.text = storage.absolutePath

        filesAndFolders = view.findViewById(R.id.files_and_folders)
        fileList = findFiles(storage)

        filesAndFolders.adapter = FileAdapter(requireContext(), fileList)
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
        val files = file.listFiles()
        for(currentFile in files!!)
        {
            if(currentFile.isDirectory and !currentFile.isHidden)
            {
                arrayList.add(currentFile)
            }
        }

        for(currentFile in files)
        {
            val currentFileName = currentFile.name.lowercase()
            if(currentFileName.endsWith(".png") or currentFileName.endsWith(".jpg")
                or currentFileName.endsWith(".mp4")
                or currentFileName.endsWith(".pdf")
                or currentFileName.endsWith(".txt"))
            {
                arrayList.add(currentFile)
            }
        }
        return arrayList
    }

}