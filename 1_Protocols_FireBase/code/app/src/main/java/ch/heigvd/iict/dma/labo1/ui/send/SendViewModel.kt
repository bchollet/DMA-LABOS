package ch.heigvd.iict.dma.labo1.ui.send

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import ch.heigvd.iict.dma.labo1.repositories.MeasuresRepository
import ch.heigvd.iict.dma.labo1.models.*

/*
 *  To use SharedPreferences we need a context. It's a *VERY* bad practice to keep a reference to
 *  an Activity or a Fragment in a ViewModel. When a context is needed, we should use the application's
 *  context, the framework offer the class AndroidViewModel which can be used to keep a reference
 *  to the application. Our ViewModel is a sub-class of AndroidViewModel with the application as single
 *  parameter
 */
class SendViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MeasuresRepository(viewModelScope)
    private val prefs = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)

    val measures = repository.measures
    val requestDuration = repository.requestDuration

    fun addMeasure(measure: Measure) {
        repository.addMeasure(measure)
    }

    fun addMeasures(measures: List<Measure>) {
        repository.addMeasures(measures)
    }

    fun clearAllMeasures() {
        repository.clearAllMeasures()
    }

    fun resetRequestDuration() {
        repository.resetRequestDuration()
    }

    fun sendMeasureToServer() {
        // we need all selected options
        val encryption = encryption.value!!
        val compression = compression.value!!
        val networkType = networkType.value!!
        val serialisation = serialisation.value!!

        repository.sendMeasureToServer(encryption, compression, networkType, serialisation)
    }

    /*
     *  Helpers for user's options selection
     */

    private val _encryption = MutableLiveData(Encryption.DISABLED)
    val encryption : LiveData<Encryption> get() = _encryption

    private val _compression = MutableLiveData(Compression.DISABLED)
    val compression : LiveData<Compression> get() = _compression

    private val _networkType = MutableLiveData(NetworkType.RANDOM)
    val networkType : LiveData<NetworkType> get() = _networkType

    private val _serialisation = MutableLiveData(Serialisation.JSON)
    val serialisation : LiveData<Serialisation> get() = _serialisation

    fun changeEncryption(encryption: Encryption) {
        _encryption.postValue(encryption)
        prefs.edit {
            putString(Encryption::class.simpleName, encryption.name)
        }
    }

    fun changeCompression(compression: Compression) {
        _compression.postValue(compression)
        prefs.edit {
            putString(Compression::class.simpleName, compression.name)
        }
    }

    fun changeNetworkType(networkType: NetworkType) {
        _networkType.postValue(networkType)
        prefs.edit {
            putString(NetworkType::class.simpleName, networkType.name)
        }
    }

    fun changeSerialisation(serialisation: Serialisation) {
        _serialisation.postValue(serialisation)
        prefs.edit {
            putString(Serialisation::class.simpleName, serialisation.name)
        }
    }

    /*
     * Constructor
     */
    init {
        // we ask repo to add some initial data
        repository.generateRandomMeasures()

        // we load SharedPreferences
        prefs.getString(Encryption::class.simpleName, Encryption.DISABLED.name)?.let {
            _encryption.postValue(Encryption.valueOf(it))
        }
        prefs.getString(Compression::class.simpleName, Compression.DISABLED.name)?.let {
            _compression.postValue(Compression.valueOf(it))
        }
        prefs.getString(NetworkType::class.simpleName, NetworkType.RANDOM.name)?.let {
            _networkType.postValue(NetworkType.valueOf(it))
        }
        prefs.getString(Serialisation::class.simpleName, Serialisation.JSON.name)?.let {
            _serialisation.postValue(Serialisation.valueOf(it))
        }
    }

}