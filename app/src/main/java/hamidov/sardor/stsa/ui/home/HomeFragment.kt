package hamidov.sardor.stsa.ui.home

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import hamidov.sardor.stsa.R
import hamidov.sardor.stsa.databinding.FragmentHomeBinding
import hamidov.sardor.stsa.utils.Constants
import hamidov.sardor.stsa.utils.JsoupHelper
import hamidov.sardor.stsa.utils.models.Category
import java.net.URL


class HomeFragment : Fragment() {

    private val TAG = "Home"
    private lateinit var pharmyViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var jsoupHelper: JsoupHelper
    private val binding get() = _binding!!
    private var pharmy: ArrayList<Category> = ArrayList()
    private lateinit var adapter: ImageAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pharmyViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        jsoupHelper = JsoupHelper()
        if (Constants.DATA_PHARMY.isNullOrEmpty()) {
            val content = Content()
            content.execute()
            Log.d(TAG, "onCreateView: isnull")
        } else {
            Log.d(TAG, "onCreateView: ${Constants.DATA_PHARMY}")
            pharmy = (Constants.DATA_PHARMY!!)

        }
        setup()

        return root
    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun setup() {
        Log.d(TAG, "setup: $pharmy")
        binding.rvHome.setHasFixedSize(true)
        adapter = ImageAdapter(context!!, pharmy, ImageClickLister { image ->
            Toast.makeText(context, "$image", Toast.LENGTH_SHORT).show()

            openFragment(image.link)
        })
        binding.rvHome.adapter = adapter
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun openFragment(url: String) {
        val bundle: Bundle = bundleOf("baseUrl" to url)
        findNavController().navigate(R.id.action_nav_home_to_nav_shopping, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    inner class Content : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            val link = jsoupHelper.getAttrs(
                Constants.BASE_URL,
                "a.opacity_block1.dark_block_animate",
                "href"
            )
            val name = jsoupHelper.getAttrs(
                Constants.BASE_URL,
                "a.opacity_block1.dark_block_animate",
                "title"
            )
            val image = jsoupHelper.getAttrs(
                Constants.BASE_URL,
                "div.scale_block_animate.img_block",
                "style"
            )


            for (i in 0 until link.size) {
                val bitmap = BitmapFactory.decodeStream(
                    URL(
                        "https://pharmaclick.uz/${
                            image[i].substring(
                                23,
                                image[i].length - 3
                            )
                        }"
                    ).openStream()
                )

                pharmy.add(
                    Category(
                        name[i],
                        bitmap,
                        link[i]
                    )
                )
            }
            Log.d(TAG, "doInBackground: pharmy\n${pharmy}")
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "onPreExecute: ")
            binding.pbHome.visibility = View.VISIBLE
            binding.pbHome.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    android.R.anim.fade_in
                )
            )
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            binding.pbHome.visibility = View.GONE
            binding.pbHome.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    android.R.anim.fade_out
                )
            )
            if (pharmy.isNotEmpty()) {
                Constants.DATA_PHARMY = pharmy
            }

            adapter.notifyDataSetChanged()
            Log.d(TAG, "onPostExecute: ")

        }
    }
}