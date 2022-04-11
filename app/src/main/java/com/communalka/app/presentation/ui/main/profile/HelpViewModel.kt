package com.communalka.app.presentation.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.communalka.app.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.communalka.app.common.utils.Event
import com.communalka.app.data.model.*
import com.communalka.app.data.repository.user.FaqRepository
import com.communalka.app.data.repository.user.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HelpViewModel(private val userRepository: UserRepository, private val faqRepository: FaqRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private var _progress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val progress: LiveData<Event<Boolean>> = _progress

    private var _video: MutableLiveData<Event<List<Item>>> = MutableLiveData()
    val video: LiveData<Event<List<Item>>> = _video

    private var _item: MutableLiveData<Event<Item>> = MutableLiveData()
    val item: LiveData<Event<Item>> = _item

    fun openYoutubeLink(item: Item) {
        _item.postValue(Event(item))
    }

    fun initVideo() {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.getVideoFaqKey()
                .onStart { _progress.postValue(Event(true)) }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            var key = gson.fromJson(it.data.data!!.asJsonObject.get("key"), String::class.java)
                            faqRepository.getVideo(key, "AIzaSyDgsjkMeyLRXkPPT-_yjfeqwmMqpuccQEc")
                                .onStart { _progress.postValue(Event(true)) }
                                .collect {
                                    when (it) {
                                        is Result.Success -> {
                                            _video.postValue(Event(it.data.items))
                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }



}