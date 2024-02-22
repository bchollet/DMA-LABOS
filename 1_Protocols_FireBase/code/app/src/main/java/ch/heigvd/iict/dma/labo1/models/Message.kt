package ch.heigvd.iict.dma.labo1.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Message(@PrimaryKey(autoGenerate = true) var id: Long? = null,
                   var sentDate : Calendar,
                   var receptionDate : Calendar,
                   var message : String?,
                   var command : String?)