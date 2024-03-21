package ch.heigvd.iict.dma.labo2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.dma.labo2.models.PersistentBeacon
import ch.heigvd.iict.dma.labo2.models.PersistentBeaconDiffCallback


class BeaconsAdapter(_items : List<PersistentBeacon> = listOf())  :  RecyclerView.Adapter<BeaconsAdapter.ViewHolder>()  {

    var items = listOf<PersistentBeacon>()
        set(value) {
            val sortedNewItems = value.sortedByDescending { it.rssi }
            val diffItems = DiffUtil.calculateDiff(PersistentBeaconDiffCallback(items, sortedNewItems))
            field = sortedNewItems
            diffItems.dispatchUpdatesTo(this)
        }

    init {
        items = _items
    }

    override fun getItemCount() = items.size
    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_beacon, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val maj = view.findViewById<TextView>(R.id.major)
        private val min = view.findViewById<TextView>(R.id.minor)
        private val uuid = view.findViewById<TextView>(R.id.uuid)
        private val tx = view.findViewById<TextView>(R.id.txpower)
        private val rssi = view.findViewById<TextView>(R.id.rssi)
        private val dist = view.findViewById<TextView>(R.id.distance)

        fun bind(beacon : PersistentBeacon) {
            maj.text = "${beacon.major}"
            min.text = "${beacon.minor}"
            uuid.text = "${beacon.uuid}"
            tx.text = "${beacon.txPower}"
            rssi.text = "${beacon.rssi}"
            dist.text = view.context.getString(R.string.item_distance, beacon.distance)
        }
    }

}