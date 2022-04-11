package com.communalka.app.presentation.ui.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.communalka.app.common.contextprovider.DispatcherProvider
import com.communalka.app.common.utils.Event
import com.communalka.app.data.model.VersionApp
import com.communalka.app.data.repository.user.UserRepository

class HistoryVersionViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private val listVersionAppInfo: MutableLiveData<Event<List<VersionApp>>> = MutableLiveData()
    private val updateByPosition: MutableLiveData<Event<Int>> = MutableLiveData()
    var version: MutableList<VersionApp> = ArrayList()

    fun initVersionAppInfo() {

        version.add(VersionApp("1.0","21 мая 2021г", "Теперь можно оставлять заявки на жилищные, коммунальные и коммерческие услуги.", false))
        version.add(VersionApp("2.0","22 мая 2021г", "Теперь можно оставлять заявки на жилищные, коммунальные и коммерческие услуги.", false))
        version.add(VersionApp("3.0","23 мая 2021г", "Теперь можно оставлять заявки на жилищные, коммунальные и коммерческие услуги.", false))
        version.add(VersionApp("4.0","24 мая 2021г", "Теперь можно оставлять заявки на жилищные, коммунальные и коммерческие услуги.", false))
        listVersionAppInfo.postValue(Event(version))
    }

    public fun arrowClick(position: Int) {
        version[position].opened = !version[position].opened
        listVersionAppInfo.postValue(Event(version))
    }

    fun getListVersionAppInfo(): MutableLiveData<Event<List<VersionApp>>> {
        return listVersionAppInfo
    }
}