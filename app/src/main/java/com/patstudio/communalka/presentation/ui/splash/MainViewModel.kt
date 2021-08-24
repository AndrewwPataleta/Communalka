package com.patstudio.communalka.presentation.ui.splash

import android.util.Log
import androidx.lifecycle.*
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import okhttp3.Response

class MainViewModel (private val userRepository: UserRepository): ViewModel() {

    private val _needShadow: MutableLiveData<Event<Boolean>> = MutableLiveData()

    public fun needBackgroundShadow(needShadow: Boolean) {
        _needShadow.postValue(Event(needShadow))
    }

    public fun getNeedShadow():  MutableLiveData<Event<Boolean>> {
        return _needShadow
    }
}