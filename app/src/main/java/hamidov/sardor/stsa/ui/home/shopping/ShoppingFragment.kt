package hamidov.sardor.stsa.ui.home.shopping

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import hamidov.sardor.stsa.utils.Constants
import hamidov.sardor.stsa.utils.JsoupHelper
import hamidov.sardor.stsa.utils.models.Dori
import org.jsoup.select.Elements
import java.lang.reflect.Field
import java.net.URL


class ShoppingFragment : Fragment() {

    companion object {
        fun newInstance() = ShoppingFragment()
    }

    private lateinit var viewModel: ShoppingViewModel
    private lateinit var binding: ShoppingFragmentBinding
    private lateinit var adapter: ShoppingAdapter
    private lateinit var jsoupHelper: JsoupHelper
    private var page: HashMap<String, String> = HashMap()
    private var dories: ArrayList<Dori> = ArrayList()
    private var pages: ArrayList<Int> = ArrayList()
    private val TAG = "ShoppingFragment"
    private lateinit var baseUrl: String
    private lateinit var spinnerAdapter: ArrayAdapter<Int>

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ShoppingFragmentBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        setup()
        baseUrl = (arguments!!["baseUrl"] as String)
        Log.d(TAG, "base $baseUrl")
        jsoupHelper = JsoupHelper()
        Content().execute()
        spinnerAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            pages
        )
        return binding.root
    }

    private fun initList(): ArrayList<Int> {
        for (i in 0 until page["pagenum"]!!.toInt()) {
            pages.add(i + 1)
        }

        return pages
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.page_spinner, menu)
        val item = menu.findItem(R.id.spinner)
        val spinner = MenuItemCompat.getActionView(item) as Spinner
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
                    Toast.makeText(
                        context,
                        "${
                            page["value"]!!.substring(
                                0,
                                page["value"]!!.length - 1
                            )
                        }${position + 1}",
                        Toast.LENGTH_SHORT
                    ).show()
                    baseUrl =
                        "https://pharmaclick.uz/${
                            page["value"]!!.substring(
                                0,
                                page["value"]!!.length - 1
                            )
                        }${position + 1}"
                    Content().execute()
                    Log.d(TAG, "onItemSelected: ${page.values}")
                } else {
                    if (!baseUrl.equals(arguments?.get("baseUrl") as String)) {
                        baseUrl = arguments?.get("baseUrl") as String
                        Content().execute()
                    }
                }
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun setup() {
        binding.rvShopping.setHasFixedSize(true)
        adapter = ShoppingAdapter(context!!, dories, ClickLister { image ->
            Toast.makeText(context, "$image", Toast.LENGTH_SHORT).show()
        })
        binding.rvShopping.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProvider(this).get(ShoppingViewModel::class.java)

    }


    private fun getDori(baseUrl: String) {
        val elements: Elements =
            jsoupHelper.getElements(baseUrl, "div.catalog_block.items.block_list")
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
                val bitmap: Bitmap = if (imageUrl[i].attr("src").contains("base64")) {
                    val encodeImage = imageUrl[i].attr("src")
                        .substring(imageUrl[i].attr("src").indexOf(",") + 1)
                    val byteArray: ByteArray = Base64.decode(encodeImage, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                } else {
                    BitmapFactory.decodeStream(
                        URL(
                            "https://pharmaclick.uz/${
                                imageUrl[i].attr("src")
                            }"
                        ).openStream()
                    )
                }

                dories.add(
                    Dori(
                        off[i].text(),
                        bitmap,
                        name[i].text(),
                        value[i].text(),
                        price[i].text(),

                        )
                )

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
//        dories.add(Dori(
//
//        ))
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
            binding.pbShopping.visibility = View.GONE
            binding.pbShopping.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    android.R.anim.fade_out
                )
            )
            if (dories.isNotEmpty()) {
                Constants.DATA_DORI = dories
            }

            spinnerAdapter.notifyDataSetChanged()
            adapter.notifyDataSetChanged()
            Log.d(TAG, "onPostExecute: ")

        }
    }
}

