package ch.heigvd.iict.dma.wifirtt.ui.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.dma.wifirtt.R
import ch.heigvd.iict.dma.wifirtt.models.RangedAccessPoint
import kotlin.math.round

class RangedAccessPointsAdapter(rangedAccessPoints: List<RangedAccessPoint> = emptyList()) : RecyclerView.Adapter<RangedAccessPointsAdapter.ViewHolder>() {

    var rangedAccessPoints : List<RangedAccessPoint> = rangedAccessPoints
        @SuppressLint("NotifyDataSetChanged") //the list changes integrally at each update
        set(value) {
            val sortedList = value.sortedBy { it.distanceMm }
            field = sortedList
            notifyDataSetChanged()
        }

    override fun getItemCount() = rangedAccessPoints.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_item_ranged_access_point, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rangedAccessPoints[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val macAddress = view.findViewById<TextView>(R.id.row_item_ranged_access_point_bssid)
        val distance = view.findViewById<TextView>(R.id.row_item_ranged_access_point_distance)
        val age = view.findViewById<TextView>(R.id.row_item_ranged_access_point_age)

        fun bind(ap: RangedAccessPoint) {
            macAddress.text = ap.bssid
            distance.apply {
                text = context.getString(R.string.display_distance, ap.distanceMm / 1000.0)
            }
            age.apply {
                text = context.getString(R.string.display_age, System.currentTimeMillis() - ap.age)
            }
        }

    }

}