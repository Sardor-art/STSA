package hamidov.sardor.stsa.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hamidov.sardor.stsa.R
import hamidov.sardor.stsa.utils.models.Category
import java.io.InputStream

class ImageAdapter(
    val context: Context, var list: ArrayList<Category>,
    val imageClickListener: ImageClickLister
) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    private val TAG = "ImageAdapter"

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.iv_home_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.home_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = list[position]
        holder.imageView.setImageBitmap(image.image)
        holder.imageView.setOnClickListener { (imageClickListener.clickLister(image)) }
    }

    override fun getItemCount() = list.size
}

class ImageClickLister(val clickLister: (category: Category) -> Unit) {
    fun onClick(category: Category) = clickLister(category)
}


