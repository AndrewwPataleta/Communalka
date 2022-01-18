package com.patstudio.communalka.presentation.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.ConsumptionHistory
import com.patstudio.communalka.data.model.Faq
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.Supplier
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FaqViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private var _progress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val progress: LiveData<Event<Boolean>> = _progress

    private var _faq: MutableLiveData<Event<ArrayList<Faq>>> = MutableLiveData()
    val faq: LiveData<Event<ArrayList<Faq>>> = _faq

    private  var faqList: ArrayList<Faq> = ArrayList()
    private  var stableFaqList: ArrayList<Faq> = ArrayList()

    private var searchStr = ""

    fun initFaq() {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.getFaq()
                .onStart { _progress.postValue(Event(true)) }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            val turnsType = object : TypeToken<List<Faq>>() {}.type
                            stableFaqList = gson.fromJson(it.data.data!!.asJsonObject.get("faq"), turnsType)
                            faqList.addAll(stableFaqList)
                            _faq.postValue(Event(faqList))
                            _progress.postValue(Event(false))
                        }
                    }
                }
        }
    }

    private fun search() {
        faqList.clear()
        stableFaqList.map {
            if (it.answer.toLowerCase().contains(searchStr.toLowerCase()) || it.question.toLowerCase().contains(searchStr.toLowerCase())) {
                faqList.add(it)
            }
        }
        _faq.postValue(Event(faqList))
    }


    fun searchByStr(searchStr: String) {
        if (!searchStr.isNullOrEmpty()) {
            this.searchStr = searchStr
            search()
        } else {
            faqList.clear()
            faqList.addAll(stableFaqList)
            _faq.postValue(Event(faqList))
        }

    }

    public fun arrowClick(position: Int) {
        faqList[position].opened = !faqList[position].opened
        _faq.postValue(Event(faqList))
    }

}