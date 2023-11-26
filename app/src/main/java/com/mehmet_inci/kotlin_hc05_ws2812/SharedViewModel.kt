package com.mehmet_inci.kotlin_hc05_ws2812 // ktlint-disable package-name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {


    // Bluetooth bonded Devicesform SettingsFragment to MainActivity
    private val devicesList = MutableLiveData<List<String>>()
    fun setdevicesListValue(val_DevicesList: List<String>) { devicesList.value = val_DevicesList }
    fun getdevicesListValue(): LiveData<List<String>> { return devicesList }

    // Colorpicker Color
    private val colorpicker_Color = MutableLiveData<Int>()
    fun setColorpickerColor(val_Color: Int) { colorpicker_Color.value = val_Color }
    fun getColorpickerColor(): LiveData<Int> { return colorpicker_Color }

    // Bluetooth Device Address
    private val itemAddress = MutableLiveData<String>()
    fun set_itemaddressValue(val_Itemaddress: String) { itemAddress.value = val_Itemaddress }
    fun get_itemaddressValue(): LiveData<String> { return itemAddress }

    // Seekbar1 value
    private val string_valueSeekbar1 = MutableLiveData<String>()
    fun setValueSeekbar1(val1: String) { string_valueSeekbar1.value = val1 }
    fun getValueSeekbar1(): LiveData<String> { return string_valueSeekbar1 }

    // Seekbar2 value
    private val string_valueSeekbar2 = MutableLiveData<String>()
    fun setValueSeekbar2(val2: String) { string_valueSeekbar2.value = val2 }
    fun getValueSeekbar2(): LiveData<String> { return string_valueSeekbar2 }

    // Seekbar3 value
    private val string_valueSeekbar3 = MutableLiveData<String>()
    fun setValueSeekbar3(val3: String) { string_valueSeekbar3.value = val3 }
    fun getValueSeekbar3(): LiveData<String> { return string_valueSeekbar3 }

    //private val valBrightness = MutableLiveData<Int>()
    private val valBrightness = MutableLiveData<Int>().apply { value = DEFAULT_VALUE }
    fun setvalBrigtness(BrightnessVal: Int) { valBrightness.value = BrightnessVal}
    companion object { const val DEFAULT_VALUE = 0 }// Hier setzen Sie den gewünschten Standardwert
    fun getvalBrightness(): LiveData<Int> { return valBrightness}



    // Colorpicker Color
    private val ColorpickerColor = MutableLiveData<Int>()
    fun setColor(colorVal: Int){ColorpickerColor.value = colorVal}
    fun getColor(): LiveData<Int> { return ColorpickerColor}


    private val ScrollProgress = MutableLiveData<Int>()
    fun setScrollProgressVal(ProgressVal: Int){ScrollProgress.value = ProgressVal}
    fun getScrollProgressVal(): LiveData<Int>{return ScrollProgress}



    // Bonded Device
    private val Bonded_DeviceName = MutableLiveData<String>()
    fun setBonded_DeviceName(val1: String) { Bonded_DeviceName.value = val1 }
    fun getBonded_DeviceName(): LiveData<String> { return Bonded_DeviceName }



    private val prgb = MutableLiveData<IntArray>()
    fun setprgb(val5: IntArray) {prgb.value = val5}
    fun getprgb(): LiveData<IntArray> {return prgb}

    // number of used LEDs
    private val numLEDliveData = MutableLiveData<String>()
    fun getnumLED(): LiveData<String> { return numLEDliveData }
    fun setnumLED(valnumLED: String) { numLEDliveData.value = valnumLED
    }
}
