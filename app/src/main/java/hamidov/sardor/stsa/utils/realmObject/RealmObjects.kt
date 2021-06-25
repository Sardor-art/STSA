package hamidov.sardor.stsa.utils.realmObject

import android.graphics.Bitmap
import io.realm.RealmObject

open class CategoryObject(
    var name: String="",
    var image: ByteArray?=null,
    var link: String="",
    var page: Int=0
) : RealmObject()

open class DoriObject(
    var page:Int=0,
    var off: String="",
    var image: ByteArray?=null,
    var name: String="",
    var value: String="",
    var price: String=""
//    val rate: Int,
//    val valueImage: String,
) : RealmObject(){
    
}