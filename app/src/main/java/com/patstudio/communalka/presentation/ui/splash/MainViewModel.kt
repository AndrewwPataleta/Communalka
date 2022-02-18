package com.patstudio.communalka.presentation.ui.splash

import androidx.lifecycle.*
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.repository.user.UserRepository

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