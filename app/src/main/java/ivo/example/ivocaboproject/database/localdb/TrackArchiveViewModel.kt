package ivo.example.ivocaboproject.database.localdb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackArchiveViewModel @Inject constructor(private val repo: TrackArchiveRepository) :
    ViewModel() {
    var getTrackDevicelist = MutableLiveData<List<TrackArchive>>()

    init {
        macAddress.observeForever {
            if (it.isNotEmpty())
                viewModelScope.launch {
                    var rrr = repo.findbyMacAddress(it).single()
                    if (rrr.isNotEmpty())
                        getTrackDevicelist.postValue(rrr)
                }
        }
    }


    fun insert(trackArchive: TrackArchive) = viewModelScope.launch {
        repo.insert(trackArchive)
    }

    companion object {
        var macAddress = MutableLiveData<String>()
    }
}