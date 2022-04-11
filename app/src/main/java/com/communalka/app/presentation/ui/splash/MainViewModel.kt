package com.communalka.app.presentation.ui.splash

import androidx.lifecycle.*
import com.communalka.app.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.communalka.app.common.utils.Event
import com.communalka.app.data.repository.user.UserRepository

class MainViewModel (private val userRepository: UserRepository, private val gson: Gson, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private val _needShadow: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val toolbarName: MutableLiveData<Event<String>> = MutableLiveData()

    private val _receipt: MutableLiveData<Event<Boolean>> = MutableLiveData()
    var receipt: LiveData<Event<Boolean>> = _receipt



     var _toolbarWithTitle: MutableLiveData<Event<Pair<String, String>>> = MutableLiveData()
    val toolbarWithTitle: LiveData<Event<Pair<String, String>>> = _toolbarWithTitle

    public fun needBackgroundShadow(needShadow: Boolean) {
        _needShadow.postValue(Event(needShadow))
    }

    public fun getNeedShadow():  MutableLiveData<Event<Boolean>> {
        return _needShadow
    }

    public fun showReceipt(show: Boolean) {
        _receipt.postValue(Event(show))
    }

    public fun currentPersonalAccountName(name: String) {
        toolbarName.postValue(Event(name))
    }

    fun getToolbarName(): MutableLiveData<Event<String>> {
        return toolbarName
    }

}