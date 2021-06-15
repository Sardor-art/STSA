package hamidov.sardor.stsa.utils.models

import android.graphics.Bitmap
import java.io.InputStream
import java.net.URL

data class Category(
    val name: String,
    val image: Bitmap,
    val link: String
)

data class Dori(
    val off: String,
    val image: Bitmap,
    val name: String,
//    val rate: Int,
    val value: String,
//    val valueImage: String,
    val price: String
)