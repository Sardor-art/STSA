package hamidov.sardor.stsa.ui.home.shopping

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hamidov.sardor.stsa.R
import hamidov.sardor.stsa.utils.models.Category
import hamidov.sardor.stsa.utils.models.Dori
import org.w3c.dom.Text

class ShoppingAdapter(
    val context: Context, var list: ArrayList<Dori>,
    val ClickListener: ClickLister
) :
    RecyclerView.Adapter<ShoppingAdapter.ViewHolder>() {
    private val TAG = "ShoppingAdapter"

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var off :TextView = view.findViewById(R.id.tv_off)
        var imageView: ImageView = view.findViewById(R.id.iv_dori)
        var nameDori: TextView = view.findViewById(R.id.tv_dori)
        var typeShopping: TextView = view.findViewById(R.id.tv_shopping_type)
        var priceDori: TextView = view.findViewById(R.id.tv_narxi)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.shopping_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dori = list[position]
        holder.off.text = dori.off
        holder.imageView.setImageBitmap(dori.image)
        holder.nameDori.text = dori.name
        holder.priceDori.text = dori.price
        if(dori.off.equals("Специальная цена"))
            holder.typeShopping.setBackgroundColor(Color.red(500))
        if(dori.off.equals("Рекомендуем"))
            holder.typeShopping.setBackgroundColor(Color.blue(900))
        if(dori.off.equals(""))
            holder.typeShopping.visibility = View.GONE
        else
            holder.typeShopping.visibility = View.VISIBLE
        holder.typeShopping.text = dori.value
        holder.imageView.setOnClickListener { (ClickListener.clickLister(dori)) }
    }

    override fun getItemCount() = list.size
}

class ClickLister(val clickLister: (category: Dori) -> Unit) {
    fun onClick(dori: Dori) = clickLister(dori)
}


