package ivo.example.ivocaboproject.database.localdb

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import ivo.example.ivocaboproject.AppHelpers
import ivo.example.ivocaboproject.ivocaboprojectApplication
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val appHelpers = AppHelpers()
    var user by mutableStateOf(User(0, appHelpers.getNOWasSQLDate(), "", "", "", null, null))
        private set
    var count = repo.count()
    var userNotification = MutableLiveData<Boolean?>(false)
    fun getUserNotification() = viewModelScope.launch {
        userNotification.value = getUserDetail.notification
    }

    var getUserDetail = repo.findUser()


    suspend fun getUserByEmailPass(email: String, password: String) = viewModelScope.launch {
        user = repo.findbyEmailPass(email, password)
    }

    fun getUser(email: String) = viewModelScope.launch {
        user = repo.findbyEmail(email)
    }

    fun addUser(user: User) = viewModelScope.launch {
        repo.insert(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        repo.update(user)
    }

    fun deleteUser(user: User) = viewModelScope.launch {
        repo.delete(user)
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                return UserViewModel(
                    (application as ivocaboprojectApplication) as UserRepository,
                    savedStateHandle
                ) as T
            }
        }
    }
}