package ivo.example.ivocaboproject.database.localdb

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackDeviceViewModel @Inject constructor(private val repo: TrackArchiveRepository) :
    ViewModel() {
    lateinit var getTrackDevicelist: MutableLiveData<List<TrackArchive>>

    init {
        macAddress.observeForever{
            if(it.isNotEmpty())
                getTrackDevicelist.postValue(repo.findbyMacAddress(it).value)
        }
    }


    fun insert(trackArchive: TrackArchive) = viewModelScope.launch {
        repo.insert(trackArchive)
    }

    companion object {
        lateinit var macAddress:MutableLiveData<String>
    }
}