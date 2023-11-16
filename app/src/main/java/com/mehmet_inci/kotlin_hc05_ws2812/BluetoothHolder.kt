package com.mehmet_inci.kotlin_hc05_ws2812

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket

class BluetoothHolder {
    companion object {
        private var bluetoothSocket: BluetoothSocket? = null
        private var bluetoothAdapter: BluetoothAdapter? = null

        fun getbluetoothAdapter(): BluetoothAdapter? {
            return bluetoothAdapter
        }
        fun setbluetoothAdapter(bluetoothAdapter: BluetoothAdapter) {
            this.bluetoothAdapter = bluetoothAdapter
        }

        fun getBluetoothSocket(): BluetoothSocket? {
            return bluetoothSocket
        }

        fun setBluetoothSocket(bluetoothSocket: BluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket
        }
    }
}
