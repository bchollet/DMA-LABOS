package ch.heigvd.iict.dma.labo4.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.ktx.getCharacteristic
import java.util.*

class DMABleManager(applicationContext: Context, private val dmaServiceListener: DMAServiceListener? = null) : BleManager(applicationContext) {

    private var clickCount: Int = 0;
    //Services and Characteristics of the SYM Pixl
    private var timeService: BluetoothGattService? = null
    private var symService: BluetoothGattService? = null
    private var currentTimeChar: BluetoothGattCharacteristic? = null
    private var integerChar: BluetoothGattCharacteristic? = null
    private var temperatureChar: BluetoothGattCharacteristic? = null
    private var buttonClickChar: BluetoothGattCharacteristic? = null

    private val timeServiceGuid: UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb")
    private val currentTimeCharUuid: UUID = UUID.fromString("00002A2B-0000-1000-8000-00805f9b34fb")
    private val symServiceGuid: UUID = getCustomServiceUuid(0)
    private val integerCharUuid: UUID = getCustomServiceUuid(1)
    private val temperatureCharUuid: UUID = getCustomServiceUuid(2)
    private val buttonClickCharUuid: UUID = getCustomServiceUuid(3)

    private fun getCustomServiceUuid(discriminator: Int): UUID {
        return UUID.fromString("3c0a100$discriminator-281d-4b48-b2a7-f15579a1c38f")
    }

    fun requestDisconnection() {
        this.disconnect().enqueue()
    }

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        for (service in gatt.services) {
            if (service.uuid == symServiceGuid) {
                symService = service
            }
            if (service.uuid == timeServiceGuid) {
                timeService = service
            }
        }

        if (symService == null || timeService == null) {
            return  false;
        }

        currentTimeChar = timeService!!.getCharacteristic(currentTimeCharUuid) ?: return false
        temperatureChar = symService!!.getCharacteristic(temperatureCharUuid) ?: return false
        integerChar = symService!!.getCharacteristic(integerCharUuid) ?: return false
        buttonClickChar = symService!!.getCharacteristic(buttonClickCharUuid) ?: return false


        return true
    }

    override fun initialize() {
        super.initialize()

        setNotificationCallback(buttonClickChar).with { device, data ->
            clickCount++
            dmaServiceListener?.clickCountUpdate(clickCount)
        }
        enableNotifications(buttonClickChar).enqueue()

        /* TODO
            Ici nous somme sûr que le périphérique possède bien tous les services et caractéristiques
            attendus et que nous y sommes connectés. Nous pouvous effectuer les premiers échanges BLE.
            Dans notre cas il s'agit de s'enregistrer pour recevoir les notifications proposées par certaines
            caractéristiques, on en profitera aussi pour mettre en place les callbacks correspondants.
            CF. méthodes setNotificationCallback().with{} et enableNotifications().enqueue()
         */
    }

    override fun onServicesInvalidated() {
        super.onServicesInvalidated()
        //we reset services and characteristics
        timeService = null
        currentTimeChar = null
        symService = null
        integerChar = null
        temperatureChar = null
        buttonClickChar = null
    }

    fun sendTime(time: Date): Boolean {
        // TODO
        return true
    }

    fun sendNumber(n: Int) {
        writeCharacteristic(integerChar, n.toBigInteger().toByteArray(),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT).enqueue()
    }

    fun readTemperature(): Boolean {
        readCharacteristic(temperatureChar).with { device, data -> Log.d(TAG, "readTemp") }.enqueue()
        /* TODO
            on peut effectuer ici la lecture de la caractéristique température
            la valeur récupérée sera envoyée à au ViewModel en utilisant le mécanisme
            du DMAServiceListener: Cf. temperatureUpdate()
                Cf. méthode readCharacteristic().with{}.enqueue()
            On placera des méthodes similaires pour les autres opérations
                Cf. méthode writeCharacteristic().enqueue()
        */

        return false //FIXME
    }

    companion object {
        private val TAG = DMABleManager::class.java.simpleName
    }

}

interface DMAServiceListener {
    fun dateUpdate(date : Calendar)
    fun temperatureUpdate(temperature : Float)
    fun clickCountUpdate(clickCount : Int)
}
