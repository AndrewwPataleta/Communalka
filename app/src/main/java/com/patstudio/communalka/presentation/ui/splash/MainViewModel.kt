package com.patstudio.communalka.presentation.ui.splash

import android.util.Log
import androidx.lifecycle.*
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.Gcm
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel (private val userRepository: UserRepository, private val gson: Gson, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private val _needShadow: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val toolbarName: MutableLiveData<Event<String>> = MutableLiveData()

     var _toolbarWithTitle: MutableLiveData<Event<Pair<String, String>>> = MutableLiveData()
    val toolbarWithTitle: LiveData<Event<Pair<String, String>>> = _toolbarWithTitle

    public fun needBackgroundShadow(needShadow: Boolean) {
        _needShadow.postValue(Event(needShadow))
    }

    public fun getNeedShadow():  MutableLiveData<Event<Boolean>> {
        return _needShadow
    }

    public fun currentPersonalAccountName(name: String) {
        toolbarName.postValue(Event(name))
    }

    fun getToolbarName(): MutableLiveData<Event<String>> {
        return toolbarName
    }

}