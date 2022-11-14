package it.post.app.ui.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun ImageView.bindImage(urlToImage: String?) {
    if (urlToImage != null) {
        Glide.with(context).load(urlToImage).into(this)
    }
}
