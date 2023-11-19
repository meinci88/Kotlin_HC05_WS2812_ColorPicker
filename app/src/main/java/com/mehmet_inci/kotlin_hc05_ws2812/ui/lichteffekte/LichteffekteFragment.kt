package com.mehmet_inci.kotlin_hc05_ws2812.ui.lichteffekte // ktlint-disable package-name

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mehmet_inci.kotlin_hc05_ws2812.BluetoothHolder
import com.mehmet_inci.kotlin_hc05_ws2812.ColorPickerViewModel
import com.mehmet_inci.kotlin_hc05_ws2812.MainViewModel
import com.mehmet_inci.kotlin_hc05_ws2812.R
import com.mehmet_inci.kotlin_hc05_ws2812.SharedViewModel
import com.mehmet_inci.kotlin_hc05_ws2812.UserSettingsManager
import com.mehmet_inci.kotlin_hc05_ws2812.databinding.FragmentLichteffekteBinding
import com.skydoves.colorpickerview.listeners.ColorListener
import java.io.OutputStream

class LichteffekteFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var colorPickerViewModel: ColorPickerViewModel
    private lateinit var saveUserSettingsManager: UserSettingsManager
    private lateinit var getUserSettingsManager: UserSettingsManager
    private var _binding: FragmentLichteffekteBinding? = null
    private val binding get() = _binding!!

    private val btSocket = BluetoothHolder.getBluetoothSocket()
    val outputStream: OutputStream = btSocket!!.outputStream
    var prgb: IntArray = intArrayOf(0,0,0,0,0)
    var seekbar1: IntArray = intArrayOf(0,0)
    var rgb:IntArray = intArrayOf(0,0,0,0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        colorPickerViewModel = ViewModelProvider(requireActivity())[ColorPickerViewModel::class.java]

        saveUserSettingsManager = UserSettingsManager(requireContext())
        getUserSettingsManager = UserSettingsManager(requireContext())
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        _binding = FragmentLichteffekteBinding.inflate(inflater, container, false)

        binding.seekBarEffect1.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.seekBarEffect2.setProgress(0)
                binding.seekBarBrightness.setProgress(0)
                val prefixElement = 65 // ('A')
                seekbar1 = intArrayOf(prefixElement) + progress
                val byteArray = seekbar1.map {it.toByte() }.toByteArray()
                outputStream.write(byteArray)
                outputStream.flush()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.seekBarEffect2.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.seekBarEffect1.setProgress(0)
                binding.seekBarBrightness.setProgress(0)
                val prefixElement = 66 // ('B')
                seekbar1 = intArrayOf(prefixElement) + progress
                val byteArray = seekbar1.map {it.toByte() }.toByteArray()
                outputStream.write(byteArray)
                outputStream.flush()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        sharedViewModel.getBonded_DeviceName().observe(
            viewLifecycleOwner,
        ) { val1 ->
            if (val1 != "Disconnected") {
                binding.textViewSelectedItem.text = val1.toString()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorPickerViewModel = ViewModelProvider(requireActivity())[ColorPickerViewModel::class.java]

         binding.colorPickerView.setColorListener(object : ColorListener {
             override fun onColorSelected(color: Int, fromUser: Boolean) {
                 sharedViewModel.setColorpickerColor(color)

                 // Retrieve ARGB color values from the color picker view and store them in argbVal
                 val argbVal = binding.colorPickerView.colorEnvelope.argb
                 colorPickerViewModel.selectedColor = color
                 // Use sliceArray to create a new array 'rgb' excluding the first element (alpha channel)
                 rgb = argbVal.sliceArray(1 until argbVal.size)
                 // 'rgb' now contains only the RGB values from the original ARGB array
                 sharedViewModel.getvalBrightness().observe(
                     viewLifecycleOwner,
                 ){val1 ->
                     // Update the RGB array by adding the new brightness value
                     rgb = intArrayOf(val1) + rgb
                 }
                 val prefixElement = 68
                 prgb = intArrayOf(prefixElement) + rgb
                 sharedViewModel.setprgb(prgb)
                 val byteArray = prgb.map {it.toByte() }.toByteArray()
                 outputStream.write(byteArray)
                 outputStream.flush()
             }
         })

        binding.seekBarBrightness.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                sharedViewModel.getColorpickerColor().observe(
                    viewLifecycleOwner,
                ){val_Color->
                    binding.seekBarBrightness.thumb.setTint(val_Color)
                }
                binding.seekBarEffect1.setProgress(0)
                binding.seekBarEffect2.setProgress(0)
                sharedViewModel.setvalBrigtness(progress)
                binding.tvBrightness.text = progress.toString()
                prgb[1] = progress
                val byteArray = prgb.map {it.toByte() }.toByteArray()
                outputStream.write(byteArray)
                outputStream.flush()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }
    override fun onStart() {
        super.onStart()
        val selectedColor = colorPickerViewModel.selectedColor
        if (selectedColor != null) {
            // Set the color to the ColorPickerView
            binding.colorPickerView.setInitialColor(selectedColor)
    }
    }
}




