package hamidov.sardor.stsa.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import hamidov.sardor.stsa.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private  val TAG = "PharmyFragment"
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


        setup()
        return root
    }

    
    private fun setup(){
        Log.d(TAG, "setup: ")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}