package ch.heigvd.iict.dma.wifirtt

import android.content.res.TypedArray
import android.net.wifi.rtt.RangingResult
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import ch.heigvd.iict.dma.wifirtt.config.MapConfig
import ch.heigvd.iict.dma.wifirtt.config.MapConfigs
import ch.heigvd.iict.dma.wifirtt.models.RangedAccessPoint
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver
import com.lemmingapex.trilateration.TrilaterationFunction
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer


class WifiRttViewModel : ViewModel() {

    private val maxAgeMs = 15000;
    // PERMISSIONS MANAGEMENT
    private val _wifiRttPermissionsGranted = MutableLiveData<Boolean>(null)
    val wifiRttPermissionsGranted: LiveData<Boolean> get() = _wifiRttPermissionsGranted

    fun wifiRttPermissionsGrantedUpdate(granted: Boolean) {
        _wifiRttPermissionsGranted.postValue(granted)
    }

    // WIFI RTT AVAILABILITY MANAGEMENT
    private val _wifiRttEnabled = MutableLiveData<Boolean>(null)
    val wifiRttEnabled: LiveData<Boolean> get() = _wifiRttEnabled

    fun wifiRttEnabledUpdate(enabled: Boolean) {
        _wifiRttEnabled.postValue(enabled)
    }

    // WIFI RTT MEASURES MANAGEMENT
    private val _rangedAccessPoints = MutableLiveData(emptyList<RangedAccessPoint>())
    val rangedAccessPoints: LiveData<List<RangedAccessPoint>> =
        _rangedAccessPoints.map { l -> l.toList().map { el -> el.copy() } }

    // CONFIGURATION MANAGEMENT
    // TODO change map here
    private val _mapConfig = MutableLiveData(MapConfigs.levelB)
    val mapConfig: LiveData<MapConfig> get() = _mapConfig

    fun onNewRangingResults(newResults: List<RangingResult>) {
        val newState = mutableListOf<RangedAccessPoint>()
        // existing ones
        newResults.forEach { rangingResult ->

            val existingAp = _rangedAccessPoints.value!!
                .find { it.bssid == rangingResult.macAddress.toString() }

            if (existingAp == null) {
                newState.add(RangedAccessPoint.newInstance(rangingResult))
            } else {
                existingAp.update(rangingResult)
                newState.add(existingAp)

            }
        }

        // remove old access points and add missing ones
        _rangedAccessPoints.value!!
            .filter { ap -> newResults.find { it.macAddress.toString() == ap.bssid } == null} // keep only those who haven't been updated
            .forEach {
                if (System.currentTimeMillis() - it.age <= maxAgeMs) {
                    // Keep only the youngest ones
                    newState.add(it)
                }
        }
        _rangedAccessPoints.postValue(newState)

        // when the list is updated, we also want to update estimated location
        estimateLocation()
    }

    // WIFI RTT ACCESS POINT LOCATIONS

    private val _estimatedPosition = MutableLiveData<DoubleArray>(null)
    val estimatedPosition: LiveData<DoubleArray> get() = _estimatedPosition

    private val _estimatedDistances = MutableLiveData<MutableMap<String, Double>>(mutableMapOf())
    val estimatedDistances: LiveData<Map<String, Double>> =
        _estimatedDistances.map { m -> m.toMap() }

    private val _debug = MutableLiveData(false)
    val debug: LiveData<Boolean> get() = _debug

    fun debugMode(debug: Boolean) {
        _debug.postValue(debug)
    }

    private fun estimateLocation() {

        val rangedAccessPoints = _rangedAccessPoints.value!!

        val distances = rangedAccessPoints
            .filter { it.bssid in mapConfig.value!!.accessPointKnownLocations.keys}
            .map { it.distanceMm }
            .toDoubleArray()

        val positions =
            rangedAccessPoints.mapNotNull { mapConfig.value!!.accessPointKnownLocations[it.bssid] }
            .map { doubleArrayOf(it.xMm.toDouble(), it.yMm.toDouble()) }
            .toTypedArray()

        if(distances.size < 3)
            return

        val solver = NonLinearLeastSquaresSolver(
            TrilaterationFunction(positions, distances),
            LevenbergMarquardtOptimizer()
        )
        val optimum = solver.solve()

        // the answer
        val centroid = optimum.point.toArray()

        // you should post the coordinates [x, y, height] of the estimated position in _estimatedPosition
        // in the second experiment, you can hardcode the height as 0.0
        _estimatedPosition.postValue(doubleArrayOf( centroid[0], centroid[1], 0.0))

        //as well as the distances with each access point as a MutableMap<String, Double>
        val estimatedDistances = rangedAccessPoints
            .associateBy({ it.bssid }, { it.distanceMm })
            .toMutableMap()

        _estimatedDistances.postValue(estimatedDistances)
    }

    companion object {
        private val TAG = WifiRttViewModel::class.simpleName
    }

}