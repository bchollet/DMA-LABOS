package ch.heigvd.iict.dma.wifirtt.models

import android.net.wifi.rtt.RangingResult
import kotlin.math.max

data class RangedAccessPoint(
    val bssid: String,
    var distanceMm : Double,
    var age : Long) {

    private var distanceHistory = mutableListOf<Int>()

    fun update(rangingResult: RangingResult) {
        distanceHistory.add(0, rangingResult.distanceMm)
        distanceHistory = distanceHistory.take(4).toMutableList()
        distanceMm = max(distanceHistory.average(), 0.0)
        age = System.currentTimeMillis()
    }

    companion object {
        fun newInstance(rangingResult: RangingResult) = RangedAccessPoint(rangingResult.macAddress.toString(), 0.0, System.currentTimeMillis())
    }

}
