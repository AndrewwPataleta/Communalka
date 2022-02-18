package com.patstudio.communalka.presentation.ui.main.profile

import androidx.lifecycle.*
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserNotificationViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

   private lateinit var user: User
   private lateinit var consumerInfo: Consumer
   private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()

    private var _showProgress: MutableStateFlow<Event<Boolean>> = MutableStateFlow(Event(true))
    val showProgress: LiveData<Event<Boolean>> = _showProgress.asLiveData(dispatcherProvider.io)

    private var _consumer: MutableLiveData<Event<Consumer>> = MutableLiveData()
    val consumer: LiveData<Event<Consumer>> = _consumer

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(Event(user))
   }

    fun initCurrentUser() {

        viewModelScope.launch(dispatcherProvider.io) {
            val user = userRepository.getLastAuthUser()
            userRepository.getConsumer()
                .onStart { _showProgress.emit(Event(true)) }
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            var consumer = gson.fromJson(it.data.data!!.asJsonObject.get("consumer"), Consumer::class.java)
                            consumerInfo = consumer
                            userMutable.postValue(Event(user))
                            _consumer.postValue(Event(consumer))
                            _showProgress.emit(Event(false))
                        }
                    }
                }
        }
    }

    fun changePushEnable(enable: Boolean, type: String) {

        when (type) {
            "remindIndication" -> { consumerInfo.remindIndication = enable }
            "remindPay" -> { consumerInfo.remindPay = enable }
            "messageRSO" -> { consumerInfo.messageRSO = enable}
            "personal" -> { consumerInfo.personal = enable}
            "ad" -> { consumerInfo.ad = enable }
        }

        viewModelScope.launch(dispatcherProvider.io) {

            userRepository.updateConsumer(consumerInfo)
                .catch { }
                .collect {
                    when (it) {
                        is Result.Success -> { }
                    }
                }
        }
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }



}