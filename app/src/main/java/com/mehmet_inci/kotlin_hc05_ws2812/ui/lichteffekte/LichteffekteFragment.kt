package com.mehmet_inci.kotlin_hc05_ws2812.ui.lichteffekte // ktlint-disable package-name

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mehmet_inci.kotlin_hc05_ws2812.BluetoothHolder
import com.mehmet_inci.kotlin_hc05_ws2812.MainViewModel
import com.mehmet_inci.kotlin_hc05_ws2812.SharedViewModel
import com.mehmet_inci.kotlin_hc05_ws2812.UserSettingsManager
import com.mehmet_inci.kotlin_hc05_ws2812.databinding.FragmentLichteffekteBinding
import com.skydoves.colorpickerview.listeners.ColorListener
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager
import java.io.OutputStream

class LichteffekteFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var saveUserSettingsManager: UserSettingsManager
    private lateinit var getUserSettingsManager: UserSettingsManager
    private var _binding: FragmentLichteffekteBinding? = null
    private val binding get() = _binding!!
    val btSocket = BluetoothHolder.getBluetoothSocket()
    val outputStream: OutputStream = btSocket!!.outputStream
    lateinit var color1: String
    var prgb: IntArray = intArrayOf(0,0,0,0,0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        color1 = 0.toString()
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
                val value1 = "A" + progress
                outputStream.write(value1.toByteArray())
                //outputStream.flush()
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
                val value2 = "B" + progress
                outputStream.write(value2.toByteArray())
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

        //binding.colorPickerView.attachBrightnessSlider(binding.brightnessSlide);
        binding.colorPickerView.setColorListener(object : ColorListener {
            override fun onColorSelected(color: Int, fromUser: Boolean) {
                val manager = ColorPickerPreferenceManager.getInstance(requireContext())
                binding.colorPickerView.setLifecycleOwner(requireActivity());
                //binding.colorPickerView.setPreferenceName("MyColorPicker")

                manager.restoreColorPickerData(binding.colorPickerView); // restores the


                val existingArray = intArrayOf(68, 0, 0, 0, 10)
                val byteArray1 = existingArray.map {it.toByte() }.toByteArray()
                outputStream.write(byteArray1)
                outputStream.flush()
                val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.OVERLAY)
                // Extrahiere den ARGB-Farbwert aus dem ColorEnvelope-Objekt des colorPickerView
                val argb = binding.colorPickerView.colorEnvelope.argb
                // Use sliceArray to create a new array 'rgb' excluding the first element (alpha channel)
                var rgb = argb.sliceArray(1 until argb.size)
                // 'rgb' now contains only the RGB values from the original ARGB array
                sharedViewModel.getvalBrightness().observe(
                    viewLifecycleOwner,
                ){val1 ->
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
                sharedViewModel.setvalBrigtness(progress)
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
    /*fun OutputStream(IntArray:  ) {

    }*/
}




