package hamidov.sardor.stsa.ui.home.shopping

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import hamidov.sardor.stsa.R
import hamidov.sardor.stsa.databinding.ShoppingFragmentBinding
import hamidov.sardor.stsa.utils.JsoupHelper
import hamidov.sardor.stsa.utils.models.Dori
import hamidov.sardor.stsa.utils.realmObject.CategoryObject
import hamidov.sardor.stsa.utils.realmObject.DoriObject
import io.realm.Realm
import org.jsoup.select.Elements
import java.lang.reflect.Field
import java.net.URL


class ShoppingFragment : Fragment() {

    companion object {
        fun newInstance() = ShoppingFragment()
    }

    private val TAG = "ShoppingFragment"

    private val realm:Realm = Realm.getDefaultInstance()
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var binding: ShoppingFragmentBinding
    private lateinit var adapter: ShoppingAdapter
    private lateinit var jsoupHelper: JsoupHelper
    private var page: HashMap<String, String> = HashMap()
    private var dories: ArrayList<Dori> = ArrayList()
    private var pages: ArrayList<Int> = ArrayList()
    private lateinit var baseUrl: String
    private lateinit var spinner:Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<Int>

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ShoppingFragmentBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        baseUrl = (arguments!!["baseUrl"] as String)
        Log.d(TAG, "base $baseUrl")
        jsoupHelper = JsoupHelper()

        if(realm.where(DoriObject::class.java).findAll().isNullOrEmpty()) {
            Log.d(TAG, "onCreateView: isNullOrEmpty:")
            Content().execute()
        }else{
            val result = realm.where(DoriObject::class.java).equalTo("page",1 as Int).findAll()
            result.forEach {
                dories.add(Dori(it.off,it.image!!,it.name,it.value,it.price))
            }
            Log.d(TAG, "onCreateView: \ndories: $dories")
            page["pagenum"]=realm.where(CategoryObject::class.java).equalTo("link",arguments!!["baseUrl"] as String).findFirst()!!.page.toString()
            initList()
            Log.d(TAG, "writeToRealm: ")
        }

        spinnerAdapter = ArrayAdapter(context!!,android.R.layout.simple_spinner_dropdown_item,pages)
        setup()
        return binding.root
    }

    private fun initList(): ArrayList<Int> {

        for (i in 0 until page["pagenum"]!!.toInt()) {
            pages.add(i + 1)
        }
        Log.d(TAG, "initList: \nlist: ${pages}")
        return pages
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(TAG, "onCreateOptionsMenu: ")
        inflater.inflate(R.menu.page_spinner, menu)
        val item = menu.findItem(R.id.spinner)
       spinner = MenuItemCompat.getActionView(item) as Spinner
        spinner.dropDownWidth = 120
        spinner.dropDownVerticalOffset = 100
        try {
            val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val popupWindow = popup.get(spinner) as ListPopupWindow
            popupWindow.height = 500
            popupWindow.width = 500
        } catch (e: NoClassDefFoundError) {
            // silently fail...
        } catch (e: ClassCastException) {
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        }
        spinnerAdapter.setDropDownViewResource(R.layout.spinner)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    Toast.makeText(context,"${page["value"]!!.substring(0,page["value"]!!.length - 1)}${position + 1}",Toast.LENGTH_SHORT).show()
                    if(realm.where(DoriObject::class.java).equalTo("page",position+1).findAll().isEmpty()) {
                        baseUrl ="https://pharmaclick.uz/${page["value"]!!.substring(0,page["value"]!!.length - 1)}${position + 1}"
                        Content().execute()
                    }else{
                        dories.clear()
                        realm.where(DoriObject::class.java).equalTo("page",position+1).findAll().forEach {
                            dories.add(Dori(it.off,it.image!!,it.name,it.value,it.price))
                            adapter.notifyDataSetChanged()
                        }
                    }
                    Log.d(TAG, "onItemSelected: ${page.values}")
                } else {
                    if (!baseUrl.equals(arguments?.get("baseUrl") as String)) {
                        if(realm.where(DoriObject::class.java).equalTo("page",position+1).findAll().isEmpty()) {
                            baseUrl = arguments?.get("baseUrl") as String
                            Content().execute()
                            Log.d(TAG, "onItemSelected: page 1")
                        }else{
                            realm.where(DoriObject::class.java).equalTo("page",position+1).findAll().forEach {
                                dories.add(Dori(it.off,it.image!!,it.name,it.value,it.price))
                                adapter.notifyDataSetChanged()
                        }
                    }
                }
            } // to close the onItemSelected
                }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected: ")
            }
        }
    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun setup() {
        binding.rvShopping.setHasFixedSize(true)
        adapter = ShoppingAdapter(context!!, dories, ClickLister { image ->
            Toast.makeText(context, "$image", Toast.LENGTH_SHORT).show()
        })
        binding.rvShopping.adapter = adapter
        Log.d(TAG, "setup: ")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProvider(this).get(ShoppingViewModel::class.java)
        Log.d(TAG, "onActivityCreated: ")
    }


    private fun getDori(baseUrl: String) {
        val elements: Elements =
            jsoupHelper.getElements(baseUrl, "div.catalog_block.items.block_list")
        Log.d(TAG, "getDori: ")
        if (page.values.isEmpty()) {
            Log.d(TAG, "value: ${page.values}")
            val pageElements = jsoupHelper.getElements(baseUrl, "div.nums").select("a.dark_link")
            page["value"] = pageElements.first().attr("href")
            page["pagenum"] = pageElements.last().text()
            initList()
        }
        val off = elements.select("div.stickers").select("div")
        val imageUrl = elements.select("a.thumb.shine").select("img")
        val name = elements.select("a.dark_link").select("span")
        val link = elements.select("a.dark_link")
        val rate = elements.select("div.rating").select("table")
        val value = elements.select("span.value")
        val price = elements.select("div.js_price_wrapper.price").select("span")

        try {
            dories.clear()
            for (i in 0 until imageUrl.size) {
                val byteArray =
                    if (imageUrl[i].attr("src").contains("base64")) {
                        Log.d(TAG, "getDori: base64")
                    val encodeImage = imageUrl[i].attr("src").substring(imageUrl[i].attr("src").indexOf(",") + 1)
                    Base64.decode(encodeImage, Base64.DEFAULT)
                } else {
                        Log.d(TAG, "getDori: not Base64")
                        URL("https://pharmaclick.uz/${imageUrl[i].attr("src")}").openStream().readBytes()
                }


                dories.add(Dori(off[i].text(),byteArray,name[i].text(),value[i].text(),price[i].text()))

                Log.d(TAG, "\n\n\n\n------------------------------------")
                Log.d(TAG, "off : \n ${off[i].text()}")
                Log.d(TAG, "image : \n ${imageUrl[i].attr("src")}")
                Log.d(TAG, "name : \n ${name[i].text()}")
                Log.d(TAG, "link : \n ${link[i].attr("href")}")
                Log.d(TAG, "rate : \n ${rate[i].select("div").attr("class")}")
                Log.d(TAG, "price : \n ${price[i].text()}")
                Log.d(TAG, "\n\n\n\n------------------------------------")
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    private fun writeToRealm( selectedItemPosition: Int) {


        Log.d(TAG, "writeToRealm:\n pharmy for write :$dories")
        if (!realm.isInTransaction)
            realm.beginTransaction()
        Log.d(TAG, "writeToRealm: page \n$selectedItemPosition")
        if (realm.where(CategoryObject::class.java).equalTo("link",arguments!!["baseUrl"] as String).findFirst()!!.page ==0 ) {
            realm.where(CategoryObject::class.java)
                .equalTo("link", arguments!!["baseUrl"] as String).findFirst()!!.page =
                page["pagenum"]!!.toInt()
            Log.d(TAG, "writeToRealm: page")
        }



        this.dories.forEach {
            Log.d(TAG, "writeToRealm: for \n$it")
            if (selectedItemPosition==-1)
                realm.copyToRealm(DoriObject(selectedItemPosition+2,it.off,it.image,it.name,it.value,it.price))
            else
                realm.copyToRealm(DoriObject(selectedItemPosition+1,it.off,it.image,it.name,it.value,it.price))

        }

        realm.commitTransaction()

       /* if (!realm.isClosed) {
            realm.close()
            Log.d(TAG, "writeToRealm:close ")
        }*/
    }
    @SuppressLint("StaticFieldLeak")
    inner class Content : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {

            getDori(baseUrl)
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "onPreExecute: ")
            binding.pbShopping.visibility = View.VISIBLE
            binding.rvShopping.alpha = 0.2f
            binding.pbShopping.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    android.R.anim.fade_in
                )
            )
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            binding.rvShopping.alpha = 1f
            binding.pbShopping.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    android.R.anim.fade_out
                )
            )
            binding.pbShopping.visibility = View.GONE
//            if (dories.isNotEmpty()) {
//                Constants.DATA_DORI = dories
//            }
            writeToRealm(spinner.selectedItemPosition)
            spinnerAdapter.notifyDataSetChanged()
            adapter.notifyDataSetChanged()
            Log.d(TAG, "onPostExecute: ")

        }
    }
}

