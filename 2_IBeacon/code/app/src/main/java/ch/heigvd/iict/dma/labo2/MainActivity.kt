package ch.heigvd.iict.dma.labo2

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import ch.heigvd.iict.dma.labo2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val beaconsViewModel : BeaconsViewModel by viewModels()

    private val permissionsGranted = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // check if bluetooth is enabled
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        try {
            if(!bluetoothManager.adapter.isEnabled) {
                Toast.makeText(this, R.string.ble_unavailable, Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (_: java.lang.Exception) { /* getAdapter can launch exception on some smartphone models if permission are not yet granted */ }

        // we request permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestBeaconsPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN))
        }
        else {
            requestBeaconsPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        // init views
        val beaconAdapter = BeaconsAdapter()
        binding.beaconsList.adapter = beaconAdapter
        binding.beaconsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // update views
        beaconsViewModel.closestBeacon.observe(this) {beacon ->
            if(beacon != null) {
                binding.location.text = "TODO"
            } else {
                binding.location.text = getString(R.string.no_beacons)
            }
        }

        beaconsViewModel.nearbyBeacons.observe(this) { nearbyBeacons ->
            if(nearbyBeacons.isNotEmpty()) {
                binding.beaconsList.visibility = View.VISIBLE
                binding.beaconsListEmpty.visibility = View.INVISIBLE
            } else {
                binding.beaconsList.visibility = View.INVISIBLE
                binding.beaconsListEmpty.visibility = View.VISIBLE
            }
            beaconAdapter.items = nearbyBeacons
        }

    }

    private val requestBeaconsPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isBLEScanGranted =  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                        permissions.getOrDefault(Manifest.permission.BLUETOOTH_SCAN, false)
                                    else
                                        true
            val isFineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
            val isCoarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

            if (isBLEScanGranted && (isFineLocationGranted || isCoarseLocationGranted) ) {
                // Permission is granted. Continue the action
                permissionsGranted.postValue(true)
            }
            else {
                // Explain to the user that the feature is unavailable
                Toast.makeText(this, R.string.ibeacon_feature_unavailable, Toast.LENGTH_SHORT).show()
                permissionsGranted.postValue(false)
            }
        }

}