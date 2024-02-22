package ch.heigvd.iict.dma.labo1.ui.send

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.dma.labo1.R
import ch.heigvd.iict.dma.labo1.models.*
import java.text.SimpleDateFormat
import java.util.*

class MeasuresAdapter(measures : List<Measure> = emptyList()) : RecyclerView.Adapter<MeasuresAdapter.ViewHolder>() {

    var measures : List<Measure> = measures
        set(value) {
            val sortedList = value.sortedByDescending { it.date }
            val diffCallBack = MeasuresDiffCallBack(measures, sortedList)
            val diffItems = DiffUtil.calculateDiff(diffCallBack)
            field = sortedList
            diffItems.dispatchUpdatesTo(this)
        }

    override fun getItemCount() = measures.size

    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_measure, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(measures[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val iconImg = view.findViewById<ImageView>(R.id.row_item_measure_icon)
        val valueTxt = view.findViewById<TextView>(R.id.row_item_measure_value)
        val dateTxt = view.findViewById<TextView>(R.id.row_item_measure_date)

        fun bind(measure: Measure) {
            iconImg.setImageResource(when(measure.type) {
                Measure.Type.TEMPERATURE -> R.drawable.temperature
                Measure.Type.HUMIDITY -> R.drawable.humidity
                Measure.Type.PRECIPITATION -> R.drawable.precipitation
                Measure.Type.PRESSURE -> R.drawable.pressure
            })
            iconImg.setColorFilter(ContextCompat.getColor(iconImg.context, when(measure.status) {
                Measure.Status.NEW -> R.color.send_status_new
                Measure.Status.ERROR -> R.color.send_status_error
                else -> R.color.send_status_ok
            }))

            val unit = when(measure.type) {
                Measure.Type.TEMPERATURE -> "°C"
                Measure.Type.HUMIDITY -> "%"
                Measure.Type.PRECIPITATION -> "mm"
                Measure.Type.PRESSURE -> "hpa"
            }
            valueTxt.text = String.format("%.2f %s", measure.value, unit)

            dateTxt.text = dateFormat.format(measure.date.time)
        }


    }

    companion object {
        private val dateFormat = SimpleDateFormat("HH:mm:ss - dd.MM.yyyy", Locale.ENGLISH)
    }

}

class MeasuresDiffCallBack(private val oldList : List<Measure>, private val newList: List<Measure>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                    = oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
    = oldList[oldItemPosition] == newList[newItemPosition]

}
