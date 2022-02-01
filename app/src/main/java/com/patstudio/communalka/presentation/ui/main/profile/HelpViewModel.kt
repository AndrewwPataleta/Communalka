package com.patstudio.communalka.presentation.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.repository.user.FaqRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HelpViewModel(private val userRepository: UserRepository, private val faqRepository: FaqRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private var _progress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val progress: LiveData<Event<Boolean>> = _progress


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

                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }



}