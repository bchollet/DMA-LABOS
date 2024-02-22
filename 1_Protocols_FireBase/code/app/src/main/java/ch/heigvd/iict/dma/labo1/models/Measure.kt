package ch.heigvd.iict.dma.labo1.models

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Random

data class Measure(
    val id : Int = counter++,
    var status : Status = Status.NEW,
    val type : Type,
    val value : Double,
    val date : Calendar = GregorianCalendar.getInstance()
) {

    enum class Type {
        TEMPERATURE,
        PRECIPITATION,
        HUMIDITY,
        PRESSURE
    }

    enum class Status {
        NEW,
        OK,
        ERROR
    }

    companion object {

        var counter = 0

        private val RAND = Random()

        fun getRandomMeasures(number: Int = 100) : List<Measure> {
            val measures = mutableListOf<Measure>()

            for(i in 1..number) {
                val type = Type.values()[RAND.nextInt(Type.values().size)]
                val value : Double = when(type) {
                    Type.TEMPERATURE -> -10 + 40 * RAND.nextDouble() // °C
                    Type.PRECIPITATION -> RAND.nextDouble() * 25 // mm
                    Type.HUMIDITY ->  50 + 20 * (RAND.nextDouble()-0.5) // %
                    Type.PRESSURE -> 1013 + 20 * (RAND.nextDouble()-0.5) // hPa
                }
                val date = GregorianCalendar.getInstance().apply {
                    add(Calendar.MINUTE, -1 * RAND.nextInt(60*24))
                }
                measures.add(Measure(type = type, value = value, date = date))
            }

            return measures
        }
    }

}
