package com.example.ivocaboproject.database.localdb

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ivocaboproject.AppHelpers
import com.example.ivocaboproject.ivocaboprojectApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import okhttp3.internal.wait
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