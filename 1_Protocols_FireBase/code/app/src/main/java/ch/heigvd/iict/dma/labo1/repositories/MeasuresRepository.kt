package ch.heigvd.iict.dma.labo1.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ch.heigvd.iict.dma.labo1.models.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis

class MeasuresRepository(private val scope : CoroutineScope,
                         private val dtd : String = "https://mobile.iict.ch/measures.dtd",
                         private val httpUrl : String = "http://mobile.iict.ch/api",
                         private val httpsUrl : String = "https://mobile.iict.ch/api") {

    private val _measures = MutableLiveData(mutableListOf<Measure>())
    val measures = _measures.map { mList -> mList.toList().map { el -> el.copy() } }

    private val _requestDuration = MutableLiveData(-1L)
    val requestDuration : LiveData<Long> get() = _requestDuration

    fun generateRandomMeasures(nbr: Int = 3) {
        addMeasures(Measure.getRandomMeasures(nbr))
    }

    fun resetRequestDuration() {
        _requestDuration.postValue(-1L)
    }

    fun addMeasure(measure: Measure) {
        addMeasures(listOf(measure))
    }

    fun addMeasures(measures: List<Measure>) {
        val l = _measures.value!!
        l.addAll(measures)
        _measures.postValue(l)
    }

    fun clearAllMeasures() {
        _measures.postValue(mutableListOf())
    }

    fun sendMeasureToServer(encryption : Encryption, compression : Compression, networkType : NetworkType, serialisation : Serialisation) {
        scope.launch(Dispatchers.Default) {

            val url = when (encryption) {
                Encryption.DISABLED -> httpUrl
                Encryption.SSL -> httpsUrl
            }

            var t: MutableList<Measure>? = null;
            val elapsed = measureTimeMillis {
                val gson = Gson()
                val requestBody = when (serialisation) {
                    Serialisation.JSON -> gson.toJson(_measures.value)
                    Serialisation.XML -> TODO()
                    Serialisation.PROTOBUF -> TODO()
                }

                val request = URL(url)
                val connection = request.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/${serialisation.name.lowercase()}")
                connection.setRequestProperty("X-Network", networkType.name)
                connection.setRequestProperty("X-Content-Encoding", compression.name)
                connection.setRequestProperty("User-Agent", "Ferati-Bollet")
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use {
                    it.append(requestBody)
                }

                val responseBody = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
                Log.d("response", responseBody)
                val acknowledgments = gson.fromJson(responseBody, Array<Measure>::class.java)

                _measures.value!!.forEach {newMeasure ->
                    acknowledgments.find { it.id == newMeasure.id }?.let {
                        newMeasure.status = it.status
                    }
                }
            }
            _measures.postValue(_measures.value!!)
            _requestDuration.postValue(elapsed)
        }
    }

}