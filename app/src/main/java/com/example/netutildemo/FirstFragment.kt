package com.example.netutildemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.netutildemo.api.api
import com.example.netutildemo.api.entity.HomeIndexInfo
import com.example.netutildemo.api.requestData
import com.example.netutildemo.databinding.FragmentFirstBinding
import com.example.netutildemo.util.SLog
import com.lxj.xpopup.XPopup

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!
    private val loading by lazy {
        XPopup.Builder(context).asLoading()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.btnGet.setOnClickListener {
            requestData<HomeIndexInfo>("/app/home/index",loading){
                SLog.info("it.toString()")

                SLog.info(it.toString())
                binding.etData.setText(it.toString())
            }
        }
        binding.btnPost.setOnClickListener {
            XPopup.Builder(context).asLoading().show()
            Toast.makeText(context, "还没做", Toast.LENGTH_SHORT).show()
            binding.etData.clearAnimation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}