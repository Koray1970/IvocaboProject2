package ivo.example.ivocaboproject.database.localdb

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivo.example.ivocaboproject.AppHelpers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor (
    private val repo: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val appHelpers=AppHelpers()
    var user by mutableStateOf(User(0,appHelpers.getNOWasSQLDate(),"","","",null))
        private set
    var count =repo.count()

    var getUserDetail=repo.findUser()

    suspend fun getUserByEmailPass(email:String,password:String)=viewModelScope.launch {
        user=repo.findbyEmailPass(email,password)
    }

    fun getUser(email:String)=viewModelScope.launch {
        user= repo.findbyEmail(email)
    }
    fun addUser(user:User)=viewModelScope.launch {
        repo.insert(user)
    }
   /* companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val myRepository =
                    (this[APPLICATION_KEY] as ivocaboprojectApplication).container.userRepository
                UserViewModel(
                    repo = myRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }*/
}