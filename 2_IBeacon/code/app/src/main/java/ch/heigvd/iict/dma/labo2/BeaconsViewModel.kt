package ch.heigvd.iict.dma.labo2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import ch.heigvd.iict.dma.labo2.models.PersistentBeacon
import java.time.Instant


class BeaconsViewModel : ViewModel() {

    private val _nearbyBeacons = MutableLiveData(mutableMapOf<Int, PersistentBeacon>())
    companion object {
        const val TIMEOUT_BEACONS_SECONDS: Long = 30
    }
    val nearbyBeacons : LiveData<List<PersistentBeacon>> = _nearbyBeacons.map { beaconsMap ->
        beaconsMap.values.map { it.copy()}
    }

    private val _closestBeacon = nearbyBeacons.map { nearbyBeacons ->
        nearbyBeacons.minByOrNull { it.distance }
    }
    val closestBeacon : LiveData<PersistentBeacon?> get() = _closestBeacon

    val myBeacons : Map<Int, String> = mapOf(45 to "Chambre", 15 to "Couloir")

    fun setNearbyBeacons(newBeacons: Iterable<PersistentBeacon>) {
        val beacons = _nearbyBeacons.value!!
        newBeacons.forEach {beacon ->
            if (beacons.containsKey(beacon.minor)) {
                beacons[beacon.minor]!!.apply {
                    this.distance = beacon.distance
                    this.rssi = beacon.rssi
                    this.txPower = beacon.txPower
                    this.lastAppeared = Instant.now()
                }
            } else {
                beacons[beacon.minor] = beacon
            }
        }
        _nearbyBeacons.postValue(beacons.filterValues {
            it.lastAppeared.plusSeconds(TIMEOUT_BEACONS_SECONDS).isAfter(Instant.now())
        }.toMutableMap())
    }
}
