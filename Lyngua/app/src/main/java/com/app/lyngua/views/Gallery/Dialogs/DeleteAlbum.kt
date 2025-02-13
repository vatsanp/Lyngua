package com.app.lyngua.views.Gallery.Dialogs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.lyngua.R
import com.app.lyngua.controllers.GalleryController
import kotlinx.android.synthetic.main.fragment_delete_album.*

class DeleteAlbum(private val albumName: String) : DialogFragment() {

    private lateinit var galleryController: GalleryController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryController = GalleryController(requireContext(), requireActivity(), resources.getString(R.string.app_name))

        textView_text.text = String.format(resources.getString(R.string.delete_album_text), albumName)

        val bundle = Bundle()

        //Cancel button - Send back false boolean to parent
        button_cancel.setOnClickListener {
            bundle.putBoolean("deleteAlbum", false)

            val intent = Intent().putExtras(bundle)

            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

            dismiss()
        }

        //Confirm button - Send back true boolean to parent
        button_confirm.setOnClickListener {
            bundle.putBoolean("deleteAlbum", true)

            val intent = Intent().putExtras(bundle)

            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

            dismiss()
        }
    }
}