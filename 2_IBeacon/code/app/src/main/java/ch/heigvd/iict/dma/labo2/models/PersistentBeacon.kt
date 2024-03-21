package ch.heigvd.iict.dma.labo2.models

import androidx.recyclerview.widget.DiffUtil
import java.util.*

/*
 *  N'hésitez pas à ajouter des attributs ou des méthodes à cette classe
 */
data class PersistentBeacon(
    var id : Long = nextId++,
    var major: Int,
    var minor: Int,
    var uuid: UUID,
    var rssi : Int,
    var txPower : Int,
    var distance : Double) {

    companion object {
        private var nextId = 0L
    }

}

class PersistentBeaconDiffCallback(private val oldList : List<PersistentBeacon>,
                                   private val newList : List<PersistentBeacon> ) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return  old.major == new.major &&
                old.minor == new.minor &&
                old.uuid  == new.uuid &&
                old.rssi  == new.rssi &&
                old.txPower  == new.txPower &&
                old.distance == new.distance
    }

}
