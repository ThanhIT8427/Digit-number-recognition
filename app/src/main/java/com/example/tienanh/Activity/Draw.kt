package com.example.tienanh.Activity

import android.R.attr.max
import android.R.attr.src
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.tienanh.R
import com.example.tienanh.databinding.FragmentDrawBinding
import com.example.tienanh.ml.Mnist
import com.example.tienanh.ml.MnistModel
import com.example.tienanh.ml.Model
import org.checkerframework.checker.units.qual.A
import org.tensorflow.lite.DataType
//import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
//import org.tensorflow.lite.task.core.BaseOptions
//import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


class Draw:Fragment() {
    private lateinit var binding:FragmentDrawBinding
    var canvas: Canvas?= null
    var paint: Paint?=null
    var bitmap: Bitmap?=null
    var newBitmap:Bitmap?=null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View=inflater.inflate(com.example.tienanh.R.layout.fragment_draw,container,false)
        binding= FragmentDrawBinding.bind(view)
        initImageView()
      //  setupDigitClassifier()
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    fun initImageView(){
        val bitmapDrawable:BitmapDrawable= resources.getDrawable(com.example.tienanh.R.drawable.black_image) as BitmapDrawable
        bitmap=bitmapDrawable.bitmap
        binding.btnClean.setOnClickListener{
            bitmap=bitmapDrawable.bitmap
            binding.imgDraw.setImageBitmap(bitmap)
            newBitmap=binding.imgDraw.drawable.toBitmap()
            initDraw(newBitmap!!)
            binding.txtResult.text=""
        }
        binding.imgDraw.adjustViewBounds=true
        val width=convertPixelsToDp(bitmap!!.width.toFloat(),requireContext())
        val height=convertPixelsToDp(bitmap!!.height.toFloat(),requireContext())
        binding.imgDraw.setImageBitmap(bitmap)
        newBitmap=binding.imgDraw.drawable.toBitmap()
        initDraw(newBitmap!!)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    fun initDraw(newBitmap: Bitmap){
        canvas= Canvas(newBitmap!!)
        paint=Paint()
        paint!!.isAntiAlias=true
        paint!!.strokeJoin=Paint.Join.ROUND
        paint!!.color= android.graphics.Color.WHITE
        paint!!.style=Paint.Style.STROKE
        paint!!.strokeWidth=100f
        var path=Path()
        binding.imgDraw.setOnTouchListener(View.OnTouchListener{
                view, motionEvent ->
            val x=motionEvent.x
            val y=motionEvent.y
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    path.moveTo(x,y)
                }
                MotionEvent.ACTION_MOVE -> {
                    path.lineTo(x,y)
                }
            }
            canvas!!.drawPath(path,paint!!)
            Log.d("MotionEvenXY","True")
            binding.imgDraw.setImageBitmap(newBitmap)
            outputModel(newBitmap!!)
            return@OnTouchListener true
        })
    }
    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.getResources()
            .getDisplayMetrics().densityDpi.toFloat()/ DisplayMetrics.DENSITY_DEFAULT)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun outputModel(bitmap:Bitmap){
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
    }

}