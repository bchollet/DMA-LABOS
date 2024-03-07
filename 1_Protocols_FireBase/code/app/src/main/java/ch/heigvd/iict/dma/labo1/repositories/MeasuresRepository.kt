package ch.heigvd.iict.dma.labo1.repositories

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
import org.jdom2.*
import org.jdom2.input.SAXBuilder
import org.jdom2.output.XMLOutputter
import java.io.StringReader
import java.io.StringWriter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
data class Acknowledgment(val id: Int, val status: Measure.Status)

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

    private fun xmlToMeasures(xml: String): Array<Acknowledgment> {
        val acks = mutableListOf<Acknowledgment>()

        val builder = SAXBuilder().apply {
            setFeature("http://xml.org/sax/features/external-general-entities", false)
        }

        val doc = builder.build(StringReader(xml))
        for (element in doc.rootElement.getChildren("measure")) {
            val id = element.getAttributeValue("id").toInt()
            val status = element.getAttributeValue("status")
            acks.add(Acknowledgment(id, Measure.Status.valueOf(status)))
        }

        return acks.toTypedArray()
    }

    private fun measuresToXml(measures: List<Measure>): String {
        val root = Element("measures")
        measures.forEach { m ->
            val element = Element("measure")
            element.setAttribute("id", m.id.toString())
            element.setAttribute("status", m.status.toString())

            val type = Element("type")
            type.text = m.type.toString()

            val value = Element("value")
            value.text = DecimalFormat("#.${"#"}").format(m.value)

            val date = Element("date")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            date.text = dateFormat.format(m.date.time)

            element.addContent(type)
            element.addContent(value)
            element.addContent(date)

            root.addContent(element)
        }

        val document = Document(root)
        val stringWriter = StringWriter()
        val outputter = XMLOutputter()
        document.docType = DocType("measures", dtd)

        outputter.output(document, stringWriter)
        return stringWriter.toString()
    }

    fun sendMeasureToServer(encryption : Encryption, compression : Compression, networkType : NetworkType, serialisation : Serialisation) {
        scope.launch(Dispatchers.Default) {

            val url = when (encryption) {
                Encryption.DISABLED -> httpUrl
                Encryption.SSL -> httpsUrl
            }

            val elapsed = measureTimeMillis {
                val gson = Gson()
                val requestBody = when (serialisation) {
                    Serialisation.JSON -> gson.toJson(_measures.value)
                    Serialisation.XML -> measuresToXml(_measures.value!!)
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
                val acknowledgments = when(serialisation) {
                    Serialisation.JSON -> gson.fromJson(responseBody, Array<Acknowledgment>::class.java)
                    Serialisation.XML -> xmlToMeasures(responseBody)
                    Serialisation.PROTOBUF -> TODO()
                }

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