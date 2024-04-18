package ch.heigvd.iict.dma.wifirtt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResult.STATUS_SUCCESS
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ch.heigvd.iict.dma.wifirtt.config.MapConfigs
import ch.heigvd.iict.dma.wifirtt.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val wifiRttViewModel : WifiRttViewModel by viewModels()

    private lateinit var wifiManager: WifiManager
    private lateinit var wifiRttManager : WifiRttManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_list, R.id.navigation_map)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 1. we request necessary permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestWifiRTTPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.NEARBY_WIFI_DEVICES))
        }
        else {
            requestWifiRTTPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                                                            Manifest.permission.ACCESS_FINE_LOCATION))
        }

        // 2.  check if Wifi RTT is available (when permissions are set)
        wifiRttViewModel.wifiRttPermissionsGranted.observe(this) { granted ->
            if(granted == null) return@observe
            if(granted && packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)) {
                wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                wifiRttManager = getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager

                if(wifiRttManager.isAvailable)
                    wifiRttViewModel.wifiRttEnabledUpdate(true)
                else
                    Toast.makeText(this@MainActivity, R.string.wifi_rtt_disabled, Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@MainActivity, R.string.wifi_rtt_unavailable, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private var rangingTask : Timer? = null

     fun x(mesCouilles: String) {

    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        // 3. we start ranging
        wifiRttViewModel.wifiRttEnabled.observe(this) {isEnabled ->
            if(isEnabled == null) return@observe
            if(isEnabled) {
                rangingTask?.cancel() // we cancel eventual previous task
                rangingTask =
                    timer("ranging_timer", daemon = false, initialDelay = 500, period = 250) {
                        val apsInRange = wifiManager.scanResults.take(RangingRequest.getMaxPeers())

                        val req = RangingRequest.Builder().addAccessPoints(apsInRange).build()
                        wifiRttManager.startRanging(req, mainExecutor, object : RangingResultCallback() {
                            override fun onRangingResults(results: List<RangingResult>) {
                                wifiRttViewModel.onNewRangingResults(results.filter { it.status == STATUS_SUCCESS })
                            }
                            override fun onRangingFailure(code: Int) { }
                        })

                    }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        rangingTask?.cancel()
    }

    private val requestWifiRTTPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        val isWifiRttPermissionGranted =  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) &&
            permissions.getOrDefault(Manifest.permission.NEARBY_WIFI_DEVICES, false)
        else
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)

        wifiRttViewModel.wifiRttPermissionsGrantedUpdate(isWifiRttPermissionGranted)
    }

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

}