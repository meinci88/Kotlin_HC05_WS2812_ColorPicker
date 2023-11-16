package com.mehmet_inci.kotlin_hc05_ws2812.ui.settings // ktlint-disable package-name

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mehmet_inci.kotlin_hc05_ws2812.BluetoothHolder
import com.mehmet_inci.kotlin_hc05_ws2812.MainActivity
import com.mehmet_inci.kotlin_hc05_ws2812.R
import com.mehmet_inci.kotlin_hc05_ws2812.SharedViewModel
import com.mehmet_inci.kotlin_hc05_ws2812.UserSettingsManager
import com.mehmet_inci.kotlin_hc05_ws2812.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var saveUserSettingsManager: UserSettingsManager
    private lateinit var getUserSettingsManager: UserSettingsManager
    private var _binding: FragmentSettingsBinding? = null
    val BLUETOOTH_PERMISSION_REQUEST_CODE = 1 // You can choose any value for the request code.
    val spinner_selectedItem = ArrayList<String>()
    var mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private lateinit var adapter: ArrayAdapter<String>
    var itemAddress: String? = null
    var itemName: String? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // get BluetoothAdapter from MainActivity
        mBluetoothAdapter = BluetoothHolder.getbluetoothAdapter()
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        saveUserSettingsManager = UserSettingsManager(requireContext())
        getUserSettingsManager = UserSettingsManager(requireContext())
        devList()

        sharedViewModel.getBonded_DeviceName().observe(
            viewLifecycleOwner,
        ) { val1 ->
            if (val1 != "Disconnected") {
                binding.textViewSelectedItem.text = val1.toString()
                binding.spinner.visibility = View.INVISIBLE
            } else {
                // Show that observe is not true
                binding.textViewSelectedItem.text = "Disconnected"
                binding.spinner.visibility = View.VISIBLE
            }
        }

        binding.textInputLayout.setEndIconOnClickListener {
            Toast.makeText(requireContext(), binding.textInputEditText.text.toString() + "LEDs gespeichert", Toast.LENGTH_SHORT).show()

            // nach Eingabe Tastaturfeld ausblenden
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)

            saveUserSettingsManager.saveUserSetting("numLEDs", binding.textInputEditText.text.toString())
        }

        val userValue = getUserSettingsManager.getUserSetting("numLEDs", "8") as String
        binding.textInputEditText.setText(userValue)

        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        val spinner: Spinner = binding.spinner

        return binding.root
    }



    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        devList()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun devList() {
        // val devices = mBluetoothAdapter.bondedDevices
        adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_expandable_list_item_1, spinner_selectedItem)
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        spinner_selectedItem.clear()
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.BLUETOOTH_CONNECT,
            ) != PackageManager.PERMISSION_GRANTED
        ) { ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH_CONNECT), BLUETOOTH_PERMISSION_REQUEST_CODE)
        } else {
            // to get
            val devices = BluetoothHolder.getbluetoothAdapter()?.bondedDevices

            // val devices = sharedViewModel.getdevicesListValue().value
            if (devices != null) {
                for (device in devices) {
                    // binding.spinner.adapter = adapter
                    spinner_selectedItem.add(device.address + ":  " + device.name.toString())
                    adapter.notifyDataSetChanged()
                }
            }

            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_selectedItem.add(0, "Bitte Bluetoothgerät wählen")
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    positionAddressName: Int,
                    id: Long,
                ) {
                    var item = spinner_selectedItem[positionAddressName]
                    if (item != "Bitte Bluetoothgerät wählen") {
                        itemAddress = item.substring(0, 17)
                        sharedViewModel.set_itemaddressValue(itemAddress!!)
                        itemName = item.substring(18, item.length)

                        // binding.textViewSelectedItem.text = itemName
                        alertDialog_BluetoothVerbinden()
                    }
                    binding.spinner.setSelection(0)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle nothing selected if needed
                }
            }
        } }

    fun alertDialog_BluetoothVerbinden() {
        // builder.setTitle("Alert Dialog")
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Mit dem Bluetoothgerät verbinden.")
        builder.setPositiveButton("OK") { dialog, which ->
            // Do something when the user clicks the positive button

            val mainActivity = activity as? MainActivity
            mainActivity?.bluetooth_bound()

            dialog.dismiss()
        }
        // Optionally, you can add a negative button
        builder.setNegativeButton("Cancel") { dialog, which ->
            // Do something when the user clicks the negative button
            dialog.dismiss()
            Toast.makeText(requireContext(), "wird nicht gebunden", Toast.LENGTH_SHORT).show()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private inner class BluetoothReceiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            // val BT_Device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(itemAddress)
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                binding.textViewSelectedItem.text = "Disconnected"
                itemName = "Disconnected"
                sharedViewModel.setBonded_DeviceName(itemName!!)
                binding.spinner.visibility = View.VISIBLE
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                // Device bonded
                Toast.makeText(context, "Bluetooth Connected", Toast.LENGTH_SHORT).show()
                binding.textViewSelectedItem.text = itemName

                sharedViewModel.setBonded_DeviceName(itemName!!)
                binding.spinner.visibility = View.INVISIBLE
                binding.textViewSelectedItem.setOnLongClickListener {
                    true
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        devList()

        // Register the BroadcastReceiver to listen for Bluetooth events
        val filter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        val filter1 = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        requireActivity().registerReceiver(BluetoothReceiver(), filter)
        requireActivity().registerReceiver(BluetoothReceiver(), filter1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
