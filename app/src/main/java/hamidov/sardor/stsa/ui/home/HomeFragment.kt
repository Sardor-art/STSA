package hamidov.sardor.stsa.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import hamidov.sardor.stsa.databinding.FragmentHomeBinding
import hamidov.sardor.stsa.utils.Constants
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.InputStream
import java.net.URL

class HomeFragment : Fragment() {

    private val TAG = "Home"
    private lateinit var pharmyViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pharmyViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val content = Content()
        content.execute()
        setup()
        return root
    }


    private fun setup() {
//        Log.d(TAG, "setup: ")
//        val doc : Document = Jsoup.connect("https://pharmaclick.uz/ru/").get()
//        val data = doc.select("div.scale_block_animate.img_block")
//        Log.d(TAG, "setup: data\n$data")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    inner class Content : AsyncTask<Void, Void, Void>() {
//        lateinit var bitmap:Bitmap
        override fun doInBackground(vararg p0: Void?): Void? {

            val doc: Document = Jsoup.connect(Constants.BASE_URL).get()
            val data: Elements = doc.select("div.scale_block_animate.img_block")
                val data1 = data.attr("style")
    data.forEach { element ->
        Log.d(TAG, "doInBackground: image:${element}")
    }
//            val a =data1.substring(22,data1.length-3)
//            Log.d(TAG, "doInBackground: data\n$data")
//            Log.d(TAG, "doInBackground: data1\n$data1")
//            Log.d(TAG, "doInBackground: a\n$a")
//            val inputStream:InputStream = URL("${Constants.IMAGE_URL}$a").openStream()
//            bitmap = BitmapFactory.decodeStream(inputStream)

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "onPreExecute: ")
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            Log.d(TAG, "onPostExecute: ")
//            binding.image.setImageBitmap(bitmap)
        }

        override fun onCancelled() {
            super.onCancelled()
        }
    }
}