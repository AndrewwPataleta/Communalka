package com.patstudio.communalka.presentation.ui.main.room

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.repository.premises.DaDataRepository
import com.patstudio.communalka.data.repository.premises.PremisesRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AddRoomViewModel(private val userRepository: UserRepository, private val premisesRepository: PremisesRepository, private val daDataRepository: DaDataRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private val userMutable: MutableLiveData<User> = MutableLiveData()
    private val nameRoomError: MutableLiveData<Event<String>> = MutableLiveData()
    private val fioOwnerError: MutableLiveData<Event<String>> = MutableLiveData()
    private val addressError: MutableLiveData<Event<String>> = MutableLiveData()
    private val progressCreateRoom: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val totalSpaceError: MutableLiveData<Event<String>> = MutableLiveData()
    private val totalLivingError: MutableLiveData<Event<String>> = MutableLiveData()
    private val checkReadExternalPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val openExternalPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val imageURI: MutableLiveData<Event<Uri>> = MutableLiveData()
    private val progressSuggestions: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val listSuggestions: MutableLiveData<Event<List<Suggestion>>> = MutableLiveData()
    private val imagesMutable: MutableLiveData<Event<HashMap<Int, String>>> = MutableLiveData()
    private var roomName: String = ""
    private var addressRoom: String = ""
    private var fioOwner: String = ""
    private var totalSpace: String = ""
    private var livingSpace: String = ""
    private var actualApiKey: String = ""
    private var searchJob: Job? = null
    private lateinit var lastListSuggestions: List<Suggestion>
    private lateinit var selectedSuggestion: Suggestion
    private var images: HashMap<Int, String> = hashMapOf(1 to "HOME", 2 to "ROOM", 3 to "OFFICE", 4 to "HOUSE")
    private var selectedImage = images.get(1)
    private lateinit var currentPath: Uri
    private var IMAGE_MODE = "DEFAULT"

    fun initApiKey() {
        imagesMutable.postValue(Event(images))
        viewModelScope.launch(dispatcherProvider.io) {
            premisesRepository.getActualApiKey()
                .collect {
                    Log.d("AddRoomViewModel", "actual key resulr"+it.toString())
                    when (it) {
                        is Result.Success -> {
                            actualApiKey = it.data.data?.asJsonObject?.get("key")!!.asString
                            Log.d("AddRoomViewModel", "actual key "+actualApiKey)
                            daDataRepository.setCurrentDaDataToken(actualApiKey)

//                            when(it.data.status) {
//                                "success" -> {
//                                    actualApiKey = it.data.data?.asJsonObject?.get("key").toString()
//                                    Log.d("AddRoomViewModel", "actual key "+actualApiKey)
//                                    daDataRepository.setCurrentDaDataToken(actualApiKey)
//                                }
//                            }
                        }
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    }
                }
        }
    }

    fun selectSuggest(position: Int) {
        selectedSuggestion = lastListSuggestions.get(position)
    }

    private fun validateRoomForm(): Boolean {
        var isValidate = true
        if (roomName.isNullOrEmpty()) {
            nameRoomError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
        if (addressRoom.isNullOrEmpty()) {
            addressError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
        if (fioOwner.isNullOrEmpty()) {
            fioOwnerError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
        if (totalSpace.isNullOrEmpty()) {
            totalSpaceError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
        if (livingSpace.isNullOrEmpty()) {
            totalLivingError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
//        if (livingSpace.toLong() > totalSpace.toLong()) {
//            totalLivingError.postValue(Event("Не может быть больше чем общая площадь"))
//            isValidate = false
//        }
        return isValidate
    }

    fun saveRoom() {
        if (validateRoomForm()) {
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.getLastAuthUser()
                    .collect {
                        if (it != null) {
                            val detailAddressInfo = selectedSuggestion.data
                            val room = Room(roomName,totalSpace.toDouble(), livingSpace.toDouble(),selectedSuggestion.value,
                                detailAddressInfo.postalCode,detailAddressInfo.country,detailAddressInfo.countryIsoCode,
                                detailAddressInfo.federalDistrict,detailAddressInfo.regionFiasId,detailAddressInfo.regionKladrId,
                                detailAddressInfo.regionIsoCode,detailAddressInfo.regionWithType,detailAddressInfo.regionType,
                                detailAddressInfo.regionTypeFull,detailAddressInfo.region,detailAddressInfo.cityFiasId,
                                detailAddressInfo.cityKladrId,detailAddressInfo.cityWithType,detailAddressInfo.cityType,
                                detailAddressInfo.cityTypeFull,detailAddressInfo.city,detailAddressInfo.streetFiasId,detailAddressInfo.streetKladrId,
                                detailAddressInfo.streetWithType,detailAddressInfo.streetType,detailAddressInfo.streetTypeFull,detailAddressInfo.street,
                                detailAddressInfo.houseFiasId,detailAddressInfo.houseKladrId,detailAddressInfo.houseType,detailAddressInfo.houseTypeFull,
                                detailAddressInfo.house,detailAddressInfo.flatFiasId,detailAddressInfo.flatType,detailAddressInfo.flatTypeFull,detailAddressInfo.flat,
                                detailAddressInfo.fiasId,detailAddressInfo.fiasLevel,detailAddressInfo.kladrId,detailAddressInfo.timezone,detailAddressInfo.geoLat,
                                detailAddressInfo.geoLon)
                            premisesRepository.sendPremises(room)
                                .onStart {
                                    progressCreateRoom.postValue(Event(true))
                                }
                                .catch {
                                    it.printStackTrace()
                                }
                                .collect {
                                    progressCreateRoom.postValue(Event(false))
                                    it.toString()
                                }
                        }
                    }
            }
        }
    }

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(user)
   }

    fun setRoomName(roomName: String) {
        this.roomName = roomName
    }

    private fun searchAddress() {
        try {
            viewModelScope.launch(dispatcherProvider.io) {
                daDataRepository.getSuggestions(addressRoom)
                    .onStart {
                        progressSuggestions.postValue(Event(true))
                    }
                    .catch { it.printStackTrace() }
                    .collect {

                        when (it) {
                            is Result.Success -> {
                                var suggestions = gson.fromJson(it.data, SuggestionWrapper::class.java)
                                lastListSuggestions = suggestions.suggestions
                                listSuggestions.postValue(Event(lastListSuggestions))
                                progressSuggestions.postValue(Event(false))
                            }
                            is Result.Error -> {

                            }
                            is Result.ErrorResponse -> {

                            }
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAddressName(addressRoom: String) {
        this.addressRoom = addressRoom
        Log.d("AddRoomViewMode", addressRoom)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            Log.d("AddRoomViewMode", "search "+addressRoom)
            delay(500)
            searchAddress()
        }
    }

    fun setFioOwner(fioOwner: String) {
        this.fioOwner = fioOwner
    }

    fun setTotalSpace(totalSpace: String) {
        this.totalSpace = totalSpace
    }

    fun setLivingSpace(livingSpace: String) {
        this.livingSpace = livingSpace
    }

    fun getUser(): MutableLiveData<User> {
        return userMutable
    }

    fun getNameRoomError() : MutableLiveData<Event<String>> {
        return nameRoomError
    }

    fun getProgressSuggestions() : MutableLiveData<Event<Boolean>> {
        return progressSuggestions
    }

    fun getListSuggestions() : MutableLiveData<Event<List<Suggestion>>> {
        return listSuggestions
    }

    fun getAddressError() : MutableLiveData<Event<String>> {
        return addressError
    }

    fun getFioOwnerError() : MutableLiveData<Event<String>> {
        return fioOwnerError
    }

    fun getTotalSpaceError() : MutableLiveData<Event<String>> {
        return totalSpaceError
    }

    fun getOpenExternalPermission() : MutableLiveData<Event<Boolean>> {
        return openExternalPermission
    }

    fun getProgressCreateRoom() : MutableLiveData<Event<Boolean>> {
        return progressCreateRoom
    }

    fun getImages() : MutableLiveData<Event<HashMap<Int, String>>> {
        return imagesMutable
    }

    fun getCheckExternalPermission() : MutableLiveData<Event<Boolean>> {
        return checkReadExternalPermission
    }

    fun selectFirstImage() {
        checkReadExternalPermission.postValue(Event(true))
    }

    fun selectSecondImage() {
        IMAGE_MODE = "DEFAULT"
        images.put(1,images.get(2)!!)
        images.put(2,selectedImage!!)
        selectedImage = images.get(1)
        imagesMutable.postValue(Event(images))
    }

    fun selectThirdImage() {
        IMAGE_MODE = "DEFAULT"
        images.put(1,images.get(3)!!)
        images.put(3,selectedImage!!)
        selectedImage = images.get(1)
        imagesMutable.postValue(Event(images))
    }

    fun selectFourImage() {
        IMAGE_MODE = "DEFAULT"
        images.put(1,images.get(4)!!)
        images.put(4,selectedImage!!)
        selectedImage = images.get(1)
        imagesMutable.postValue(Event(images))
    }

    fun getTotalLivingError() : MutableLiveData<Event<String>> {
        return totalLivingError
    }

    fun getImageURI() : MutableLiveData<Event<Uri>> {
        return imageURI
    }

    fun setCurrentRoomImage(currentPath: Uri) {
        IMAGE_MODE = "STORAGE"
        this.currentPath = currentPath
        imageURI.postValue(Event(currentPath))
    }

    fun haveReadExternalPermission(havePermission: Boolean) {
        if (havePermission)
            openExternalPermission.postValue(Event(true))
    }
}