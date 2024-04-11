package ch.heigvd.iict.dma.wifirtt.config

import ch.heigvd.iict.dma.wifirtt.R
import kotlin.math.roundToInt

object MapConfigs {

    private const val b30mmPerPx = 4.223
    private val originShift = Pair(198, 1173)

    val b30 = MapConfig(
        R.drawable.b30,
        b30mmPerPx,
        (3087 * b30mmPerPx).roundToInt(),
        (1912 * b30mmPerPx).roundToInt(),
        mapOf(
            // FIXME set the correct position of AP <24:e5:0f:08:17:a9> - B30 (fenêtres)
            "bc:df:58:f2:f7:b4" to  AccessPointLocation("bc:df:58:f2:f7:b4", originShift.first +  470, originShift.second + 11330), // B30 (armoire)
            "24:e5:0f:08:17:a9" to  AccessPointLocation("24:e5:0f:08:17:a9", 4500, 6500), // B30 (fenêtres)
            "24:e5:0f:08:5c:19" to  AccessPointLocation("24:e5:0f:08:5c:19", originShift.first + 7080, originShift.second + 11620), // B30 (arrière)
        )
    )


    private const val levelBmmPerPx = 10.3938
    val levelB = MapConfig(
        R.drawable.plan_etage_b,
        levelBmmPerPx,
        (2661 * levelBmmPerPx).roundToInt(),
        (4589 * levelBmmPerPx).roundToInt(),
        mapOf(
            "bc:df:58:f3:08:df" to AccessPointLocation("bc:df:58:f3:08:df", 18148, 16088, 1058), // B29
            "90:ca:fa:2f:34:77" to  AccessPointLocation("90:ca:fa:2f:34:77", 9373, 526, 1219), // B26
            "90:ca:fa:2f:18:cf" to  AccessPointLocation("90:ca:fa:2f:18:cf", 7444, 11646, 2220), // B24
            "bc:df:58:f2:f9:ee" to  AccessPointLocation("bc:df:58:f2:f9:ee", 13869, 19365, -210), // B23
            "bc:df:58:f2:f7:b4" to  AccessPointLocation("bc:df:58:f2:f7:b4", 23165, 455, 2220), // B30 (armoire)
            "24:e5:0f:08:17:a9" to  AccessPointLocation("24:e5:0f:08:17:a9", 22709, 9386, 800), // B30 (fenêtres)
            "24:e5:0f:08:5c:15" to  AccessPointLocation("24:e5:0f:08:5c:15", 16680, 802, 1219), // B30 (arrière)
            "24:e5:0f:08:05:f7" to  AccessPointLocation("24:e5:0f:08:05:f7", 31412, 4784, 0), // B34
        )
    )
}

data class MapConfig(
    val imageRes : Int,
    val mmPerPx : Double,
    val mapHeightMm : Int,
    val mapWidthMm : Int,
    val accessPointKnownLocations : Map<String, AccessPointLocation>)

data class AccessPointLocation(val macAddress: String, val xMm : Int, val yMm : Int, val heightMm: Int = 0)