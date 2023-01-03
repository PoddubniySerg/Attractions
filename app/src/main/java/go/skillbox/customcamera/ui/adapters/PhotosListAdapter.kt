package go.skillbox.customcamera.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import go.skillbox.customcamera.databinding.ItemPhotoBinding
import go.skillbox.customcamera.models.PhotoUi
import go.skillbox.domain.models.Photo

class PhotosListAdapter(
    val onItemClick: (Photo) -> Unit,
    val remove: (Photo) -> Unit
) : ListAdapter<PhotoUi, PhotoViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {

            Glide.with(root).load(item.url).centerCrop().into(photoImageView)

            dateTextView.text = item.date

            root.setOnClickListener { onItemClick(item) }

            removeImageView.setOnClickListener { remove(item) }
        }
    }
}

class PhotoViewHolder(val binding: ItemPhotoBinding) : ViewHolder(binding.root)

class DiffUtilCallback : DiffUtil.ItemCallback<PhotoUi>() {
    override fun areItemsTheSame(oldItem: PhotoUi, newItem: PhotoUi): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PhotoUi, newItem: PhotoUi): Boolean {
        return oldItem == newItem
    }
}