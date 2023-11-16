package com.mehmet_inci.kotlin_hc05_ws2812 // ktlint-disable package-name

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mehmet_inci.kotlin_hc05_ws2812.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var btSocket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BluetoothHolder.setbluetoothAdapter(mBluetoothAdapter)

        val mainViewModel =
            ViewModelProvider(this).get(MainViewModel::class.java)


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_settings,
                R.id.navigation_lichteffekte,
            ),
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        val filter1 = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        val bluetoothReceiver = null
        this.registerReceiver(bluetoothReceiver, filter)
        this.registerReceiver(bluetoothReceiver, filter1)
    }

    fun bluetooth_bound() {
        //region Starte eine Coroutine, um die Bluetooth-Verbindung herzustellen
        GlobalScope.launch {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val connectedDevices = bluetoothAdapter.bondedDevices

            // Code generated from ChatCPT(withContext(Dispatchers.Main)
            withContext(Dispatchers.Main) {
                sharedViewModel.get_itemaddressValue().observe(
                    this@MainActivity,
                    Observer { itemAddress ->
                        val btDevice: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(itemAddress)
                        if (ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.BLUETOOTH_CONNECT,
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return@Observer
                        }
                        btSocket = btDevice.createRfcommSocketToServiceRecord(this@MainActivity.MY_UUID)
                    },
                )
            }
            try {
                btSocket?.connect()
                // set btSocket to BluetoothSocketHolder Class to share any other Fragments or Activities
                btSocket?.let { BluetoothHolder.setBluetoothSocket(it) }
            } catch (e: Exception) {
                println("Fail Bluetooth connect: $e")
            }
        }
    }

    private fun disconnect() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Bluetoothverbindung trennen.")
        builder.setPositiveButton("OK") { dialog, which ->
            // Do something when the user clicks the positive button
            dialog.dismiss()
            try {
                btSocket?.close()
            } catch (e: Exception) {
                println(e)
            }
        }
        // Optionally, you can add a negative button
        builder.setNegativeButton("Cancel") { dialog, which ->
            // Do something when the user clicks the negative button
            dialog.dismiss()
            Toast.makeText(this, "wird nicht getrennt", Toast.LENGTH_SHORT).show()
        }
        val dialog = builder.create()
        dialog.show()
    }
}
