package com.mehmet_inci.kotlin_hc05_ws2812.ui.settings

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.abs

 class MicrophoneUtil {

    object MicrophoneUtil: ViewModel() {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private val BUFFER_SIZE =
            AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        interface MicrophoneListener {
            fun onAmplitudeChanged(amplitude: Int)
        }
        private var listener: MicrophoneListener? = null
        fun setMicrophoneListener(context: Context) {
            val microphoneListener = null
            listener = microphoneListener
        }

        fun stopMicrophoneCapture() {
            listener = null
        }

       fun getMicrophoneAmplitude(context: Context): Int {
           // Check for RECORD_AUDIO permission
           if (ContextCompat.checkSelfPermission(
                   context,
                   android.Manifest.permission.RECORD_AUDIO
               ) != PackageManager.PERMISSION_GRANTED
           ) {
               // Handle the case where the permission is not granted
               return 0 // You can return a default value or handle it accordingly
           }
           val audioRecord = AudioRecord(
               MediaRecorder.AudioSource.MIC,
               SAMPLE_RATE,
               CHANNEL_CONFIG,
               AUDIO_FORMAT,
               BUFFER_SIZE)

            if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
                val buffer = ShortArray(BUFFER_SIZE)
                audioRecord.startRecording()
                // Read microphone data into the buffer
                val bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE)

                if (bytesRead > 0) {
                    // Calculate amplitude from the buffer data
                    var amplitude = 0
                    for (i in 0 until bytesRead) {
                        amplitude += abs(buffer[i].toInt())
                    }
                    // Calculate the average amplitude
                    amplitude /= bytesRead
                    // You now have the microphone amplitude in the 'amplitude' variable
                    return amplitude
                }
            }
            return 0
        }
    }
}


