package com.example.tienanh.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.tienanh.R
import com.example.tienanh.databinding.FragmentGrandleBinding
import com.example.tienanh.ml.Mnist
import org.tensorflow.lite.support.image.TensorImage
import java.io.IOException

class Gradle:Fragment() {
    private lateinit var binding: FragmentGrandleBinding
    private var bitmap: Bitmap? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_grandle, container, false)
        binding = FragmentGrandleBinding.bind(view)
        binding.imgDraw.setOnClickListener {
            openGallery()
        }
        binding.btnDraw.setOnClickListener{
            if(bitmap!=null){
                outputModel(bitmap!!)
            }
        }
        return binding.root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode === Activity.RESULT_OK) {
            val intent = result.data ?: return@registerForActivityResult
            val uri = intent.data
            binding.imgDraw.setImageURI(uri)
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri);
            binding.txtResult.text=""
        }
    }

    fun outputModel(bitmap: Bitmap){
        binding.btnDraw.setOnClickListener{
            val model = Mnist.newInstance(requireContext())
            val inputImage = TensorImage.fromBitmap(bitmap)
            val outputs = model.process(inputImage)
            val probability = outputs.probabilityAsCategoryList
            probability.get(0).label
            var maxScore=probability.get(0).score
            var dem=0
            for (i in 0 until probability.size){
                if(probability.get(i).score>=maxScore)
                {
                    if(maxScore<=probability.get(i).score){
                        maxScore=probability.get(i).score
                        dem=i
                    }
                    Log.d("Result_class",probability.get(i).label+"_"+probability.get(i).score)
                }
            }
            binding.txtResult.text=probability.get(dem).label
            model.close()
        }
}}