package com.app.lyngua.views.Gallery

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.lyngua.MainNavigationDirections
import com.app.lyngua.R
import com.app.lyngua.models.photos.Album
import kotlinx.android.synthetic.main.custom_gallery_album.view.*

class AlbumAdapter(private var albumList:MutableList<Album>, private var moreCallback:(albumName: String) -> Unit, private var playCallback:(albumName: String) -> Unit): RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_gallery_album, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAlbum = albumList[position]
        if (currentAlbum.coverPhoto != null)
            holder.itemView.imageView_gallery.setImageURI(Uri.parse(currentAlbum.coverPhoto))
        else
            holder.itemView.imageView_gallery.setImageResource(R.drawable.empty_album_shape)

        holder.itemView.textView_album_title.text = currentAlbum.name

        //On click - Open album and show photos
        holder.itemView.imageView_gallery.setOnClickListener {
            val action = MainNavigationDirections.actionGlobalAlbumPhotos(currentAlbum.name)
            holder.itemView.findNavController().navigate(action)
        }

        //More button - Run the callback function to open the bottom sheet
        holder.itemView.button_album_more.setOnClickListener {
            moreCallback(currentAlbum.name)
            notifyDataSetChanged()
        }

        //Play button - Start practicing album
        holder.itemView.button_album_play.setOnClickListener {
            if (currentAlbum.coverPhoto != null) playCallback(currentAlbum.name)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    /*
     * Purpose: Update the data and the recyclerview
     * Input:   albumList   - List of album objects
     * Output:  None
     */
    fun setData(albumList: MutableList<Album>) {
        this.albumList = albumList
        notifyDataSetChanged()
    }
}