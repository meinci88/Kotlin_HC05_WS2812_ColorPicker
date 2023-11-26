package com.mehmet_inci.kotlin_hc05_ws2812.ui.lichteffekte // ktlint-disable package-name

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mehmet_inci.kotlin_hc05_ws2812.BluetoothHolder
import com.mehmet_inci.kotlin_hc05_ws2812.ColorPickerViewModel
import com.mehmet_inci.kotlin_hc05_ws2812.MainViewModel
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
    var prgb: IntArray = intArrayOf(0, 0, 0, 0, 0)
    var seekbar1: IntArray = intArrayOf(0, 0)
    var rgb: IntArray = intArrayOf(0, 0, 0, 0)
    private var globalProgress: Int = 0
    private var globalBrightness: Int = 0
    private var globalColor: Int = 0
    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(
            requireContext(),
        )
    }
    private val allowPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            it?.let {
                if (it) {
                    Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        colorPickerViewModel =
            ViewModelProvider(requireActivity())[ColorPickerViewModel::class.java]

        saveUserSettingsManager = UserSettingsManager(requireContext())
        getUserSettingsManager = UserSettingsManager(requireContext())
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        _binding = FragmentLichteffekteBinding.inflate(inflater, container, false)

        binding.btnListen.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_DOWN -> {
                    getPermissionOverO(requireContext()) {
                        startListen()
                    }
                    return@setOnTouchListener true
                }

                else -> {
                    return@setOnTouchListener true
                }
            }
        }

        binding.seekBarEffect1.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                binding.seekBarEffect2.setProgress(0)
                binding.seekBarBrightness.setProgress(0)
                binding.seekBarScroll.setProgress(0)
                val prefixElement = 65 // ('A')
                seekbar1 = intArrayOf(prefixElement) + progress
                val byteArray = seekbar1.map { it.toByte() }.toByteArray()
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
                binding.seekBarScroll.setProgress(0)
                val prefixElement = 66 // ('B')
                seekbar1 = intArrayOf(prefixElement) + progress
                val byteArray = seekbar1.map { it.toByte() }.toByteArray()
                outputStream.write(byteArray)
                outputStream.flush()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

//        binding.seekBarScroll.setOnSeekBarChangeListener(object :
//            SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(
//                seekBar: SeekBar?,
//                progress: Int,
//                fromUser: Boolean
//            ) {
//                sharedViewModel.setScrollProgressVal(progress)
//                //globalProgress = progress
//                Scroll()
//
//            }
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//            }
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//            }
//        })


        sharedViewModel.getBonded_DeviceName().observe(
            viewLifecycleOwner,
        ) { val1 ->
            if (val1 != "Disconnected") {
                binding.textViewSelectedItem.text = val1.toString()
            }
        }
        return binding.root
    }

    fun startListen() {
        //val targetWord = "hallo" // Replace with your target word
        val targetWords = listOf("licht an", "licht aus", "Licht rot", "Licht blau", "Licht grün")
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
        )
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "DE")
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
                binding.edVoiceInput.setText("ich höre zu")
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(p0: Int) {
            }

            override fun onResults(bundle: Bundle?) {
                bundle?.let {
                    val result = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    binding.edVoiceInput.setText(result?.get(0))

                    val recognizedText = result?.get(0)

                    if (recognizedText != null) {
                        if (targetWords.any { recognizedText.contains(it, ignoreCase = true) }) {
                            Toast.makeText(
                                requireContext(),
                                "At least one of the target words was found.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                    // Iterate through each target word
                    for (targetWord in targetWords) {
                        // Check if the current target word is present in the recognized text
                        if (recognizedText != null) {
                            if (recognizedText.contains(targetWord, ignoreCase = true)) {
                                // Handle each target word individually
                                when (targetWord) {
                                    "licht an" -> LichtAn(45)
                                    "licht aus" -> LichtAus()


                                    "Licht rot" -> LichtRot()
                                    // Add more cases as needed for additional target words
                                    else -> println("$targetWord found in the recognized text.")
                                }
                            }
                        }
                    }

                }
            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }
        })
        speechRecognizer.startListening(intent)
    }

    fun getPermissionOverO(context: Context, call: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.RECORD_AUDIO,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                call.invoke()
            } else {
                allowPermission.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorPickerViewModel =
            ViewModelProvider(requireActivity())[ColorPickerViewModel::class.java]

        binding.colorPickerView.setColorListener(object : ColorListener {
            override fun onColorSelected(color: Int, fromUser: Boolean) {
                globalColor = color

                sharedViewModel.setColorpickerColor(color)

                // Retrieve ARGB color values from the color picker view and store them in argbVal
                val argbVal = binding.colorPickerView.colorEnvelope.argb
                colorPickerViewModel.selectedColor = color
                // Use sliceArray to create a new array 'rgb' excluding the first element (alpha channel)
                rgb = argbVal.sliceArray(1 until argbVal.size)
                // 'rgb' now contains only the RGB values from the original ARGB array
                sharedViewModel.getvalBrightness().observe(
                    viewLifecycleOwner,
                ) { val1 ->
                    // Update the RGB array by adding the new brightness value
                    rgb = intArrayOf(val1) + rgb
                }
                val prefixElement = 68
                prgb = intArrayOf(prefixElement) + rgb
                sharedViewModel.setprgb(prgb)
                val byteArray = prgb.map { it.toByte() }.toByteArray()
                outputStream.write(byteArray)
                outputStream.flush()
                var Progg: Int = 0
                sharedViewModel.getScrollProgressVal().observe(
                    viewLifecycleOwner,
                ) { ProgressVal ->
                    Progg = ProgressVal
                }
                var Bright: Int = 0
                sharedViewModel.getvalBrightness().observe(
                    viewLifecycleOwner,
                ) { BrightnessVal ->
                    Bright = BrightnessVal
                }


                //val seekBarState = binding.seekBarScroll.progress
                /* if (Bright == 0){
                     if (Progg >= 0){
                         Scroll()
                     }
                 }*/


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
                ) { val_Color ->
                    binding.seekBarBrightness.thumb.setTint(val_Color)
                }
                binding.seekBarEffect1.setProgress(0)
                binding.seekBarEffect2.setProgress(0)
                binding.seekBarScroll.setProgress(0)
                sharedViewModel.setvalBrigtness(progress)
                binding.tvBrightness.text = progress.toString()
                prgb[1] = progress
                val byteArray = prgb.map { it.toByte() }.toByteArray()
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

    /*private fun Scroll(){
        binding.seekBarEffect1.setProgress(0)
        binding.seekBarEffect2.setProgress(0)
        binding.seekBarBrightness.setProgress(0)
        sharedViewModel.getScrollProgressVal().observe(
            viewLifecycleOwner,
        )
   {ProgressVal ->
            val prefixElement = 69 // ('E')
            seekbar1 = intArrayOf(prefixElement) + ProgressVal
            val byteArray = seekbar1.map {it.toByte() }.toByteArray()
            outputStream.write(byteArray)
            outputStream.flush()
        }

   }*/
    private fun LichtAn(i: Int) {
        binding.seekBarEffect1.setProgress(0)
        binding.seekBarEffect2.setProgress(0)
        binding.seekBarBrightness.setProgress(0)
        val prefixElement = 69 // ('E')
        seekbar1 = intArrayOf(prefixElement) + i
        val byteArray = seekbar1.map {it.toByte() }.toByteArray()
        outputStream.write(byteArray)
        outputStream.flush()


    }
    private fun LichtAus() {
        binding.seekBarEffect1.setProgress(0)
        binding.seekBarEffect2.setProgress(0)
        binding.seekBarBrightness.setProgress(0)
        val prefixElement = 69 // ('E')
        seekbar1 = intArrayOf(prefixElement) + 0
        val byteArray = seekbar1.map {it.toByte() }.toByteArray()
        outputStream.write(byteArray)
        outputStream.flush()
    }
    private fun LichtRot() {
        binding.seekBarEffect1.setProgress(0)
        binding.seekBarEffect2.setProgress(0)
        binding.seekBarBrightness.setProgress(0)
        val prefixElement = 68
        val rgb1:IntArray  = intArrayOf(45, 255, 0, 0)
        prgb = intArrayOf(prefixElement) + rgb1
        val byteArray = prgb.map { it.toByte() }.toByteArray()
        outputStream.write(byteArray)
        outputStream.flush()
    }

}


