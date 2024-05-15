package ch.heigvd.iict.dma.labo4.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.data.Data
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

        setNotificationCallback(currentTimeChar).with { device, data ->
            val hour = data.getIntValue(Data.FORMAT_UINT8, 4)!!
            val minute = data.getIntValue(Data.FORMAT_UINT8, 5)!!
            val second = data.getIntValue(Data.FORMAT_UINT8, 6)!!
            val calendar = with(Calendar.getInstance()) {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, second)
                this
            }
            dmaServiceListener?.dateUpdate(calendar)
        }
        enableNotifications(currentTimeChar).enqueue()
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
        val calendar = Calendar.getInstance()
        calendar.time = time

        //Seperate the year in two bytes
        val year = calendar.get(Calendar.YEAR)
        val yearLower = year.toByte()
        val yearHigher = (year shr 8).toByte()

        //correct month value, Calendar starts at 0, but peripheral starts at 1
        val month = calendar.get(Calendar.MONTH) + 1

        //get fractions256 => in doc "1/256th of a second"
        val fractions256 = (calendar.get(Calendar.MILLISECOND) * 256) / 1000

        val timeBytes = byteArrayOf(
            yearLower, //Year lower bits
            yearHigher, //Year higher bits
            month.toByte(),
            calendar.get(Calendar.DAY_OF_MONTH).toByte(),
            calendar.get(Calendar.HOUR_OF_DAY).toByte(),
            calendar.get(Calendar.MINUTE).toByte(),
            calendar.get(Calendar.SECOND).toByte(),
            calendar.get(Calendar.DAY_OF_WEEK).toByte(),
            fractions256.toByte(), //Fractions256
            0x03 //Adjust reason ("manual time update = 0x01" + "external reference time update = 0x02")
        )


        writeCharacteristic(currentTimeChar, timeBytes,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT).enqueue()
        return true
    }

    fun sendNumber(n: Int) {
        writeCharacteristic(integerChar, n.toBigInteger().toByteArray(),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT).enqueue()
    }

    fun readTemperature(): Boolean {
        readCharacteristic(temperatureChar).with { device, data ->
            val temperature = data.getIntValue(Data.FORMAT_UINT16_LE, 0)!! / 10f
            dmaServiceListener?.temperatureUpdate(temperature)
        }.enqueue()
        return true
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
