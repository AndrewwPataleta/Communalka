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
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddRoomViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val daDataRepository: DaDataRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private val userMutable: MutableLiveData<User> = MutableLiveData()
    private val nameRoomError: MutableLiveData<Event<String>> = MutableLiveData()
    private val fioOwnerError: MutableLiveData<Event<String>> = MutableLiveData()
    private val addressError: MutableLiveData<Event<String>> = MutableLiveData()
    private val staticAddressImage: MutableLiveData<Event<Pair<String,String>>> = MutableLiveData()
    private val progressCreateRoom: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val totalSpaceError: MutableLiveData<Event<String>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private val openMainPage: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val totalLivingError: MutableLiveData<Event<String>> = MutableLiveData()
    private val showAddressLocation: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val checkReadExternalPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val openExternalPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val openRegistration: MutableLiveData<Event<Boolean>> = MutableLiveData()
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
    private var selectedSuggestion: Suggestion? = null
    private var images: HashMap<Int, String> = hashMapOf(1 to "HOME", 2 to "ROOM", 3 to "OFFICE", 4 to "HOUSE")
    private var selectedImage = images.get(1)
    private lateinit var currentPath: Uri
    private var IMAGE_MODE = "DEFAULT"

    fun initApiKey() {
        imagesMutable.postValue(Event(images))
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getActualApiKey()
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
        showAddressLocation.postValue(Event(true))
        staticAddressImage.postValue(Event(Pair(selectedSuggestion!!.data.geoLat, selectedSuggestion!!.data.geoLon)))
    }

    private fun validateRoomForm(): Boolean {
        var isValidate = true
        if (roomName.trim().isNullOrEmpty()) {
            nameRoomError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
        if (addressRoom.trim().isNullOrEmpty()) {
            addressError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
        if (fioOwner.trim().isNullOrEmpty()) {
            fioOwnerError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
//        if (totalSpace.isNullOrEmpty()) {
//            totalSpaceError.postValue(Event("Поле не может быть пустым"))
//            isValidate = false
//        }
//        if (livingSpace.isNullOrEmpty()) {
//            totalLivingError.postValue(Event("Поле не может быть пустым"))
//            isValidate = false
//        }
        return isValidate
    }

    private suspend fun saveRoomLocal(room: Room) {
        try {
            withContext(dispatcherProvider.io) {
                val result = roomRepository.saveRoomLocal(room)
                Log.d("AddRoomViewModel", "result "+result)
                val firstInit = roomRepository.getFirstInitRoom()
                Log.d("AddRoomViewModel", "first "+firstInit)
                progressCreateRoom.postValue(Event(false))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun saveRoom() {
        if (validateRoomForm()) {
            viewModelScope.launch(dispatcherProvider.io) {
                val it =  userRepository.getLastAuthUser()
                var room: Room
                var value = ""
                when(IMAGE_MODE) {
                    "DEFAULT" -> value = selectedImage.toString()
                    "STORAGE" -> value = currentPath.toString()
                }
                if (selectedSuggestion != null) {
                    val detailAddressInfo = selectedSuggestion!!.data
                    val totalArea = totalSpace.toDoubleOrNull()
                    val livingArea = livingSpace.toDoubleOrNull()
                    room = Room("",roomName,totalArea = totalArea, livingArea = livingArea,selectedSuggestion!!.value,

                        detailAddressInfo.postalCode,detailAddressInfo.country,detailAddressInfo.countryIsoCode, fioOwner,
                        detailAddressInfo.federalDistrict,detailAddressInfo.regionFiasId,detailAddressInfo.regionKladrId,
                        detailAddressInfo.regionIsoCode,detailAddressInfo.regionWithType,detailAddressInfo.regionType,
                        detailAddressInfo.regionTypeFull,detailAddressInfo.region,detailAddressInfo.cityFiasId,
                        detailAddressInfo.cityKladrId,detailAddressInfo.cityWithType,detailAddressInfo.cityType,
                        detailAddressInfo.cityTypeFull,detailAddressInfo.city,detailAddressInfo.streetFiasId,detailAddressInfo.streetKladrId,
                        detailAddressInfo.streetWithType,detailAddressInfo.streetType,detailAddressInfo.streetTypeFull,detailAddressInfo.street,
                        detailAddressInfo.houseFiasId,detailAddressInfo.houseKladrId,detailAddressInfo.houseType,detailAddressInfo.houseTypeFull,
                        detailAddressInfo.house,detailAddressInfo.flatFiasId,detailAddressInfo.flatType,detailAddressInfo.flatTypeFull,detailAddressInfo.flat,
                        detailAddressInfo.fiasId,detailAddressInfo.fiasLevel,detailAddressInfo.kladrId,detailAddressInfo.timezone,detailAddressInfo.geoLat,
                        detailAddressInfo.geoLon, imageType = IMAGE_MODE, imagePath = value)
                } else {
                    val totalArea = totalSpace.toDoubleOrNull()
                    val livingArea = livingSpace.toDoubleOrNull()
                    room = Room("",roomName,totalArea = totalArea, livingArea = livingArea,  addressRoom, fio = fioOwner, imageType = IMAGE_MODE, imagePath = value)
                }

                if (it != null) {
                    val resp = roomRepository.sendPremises(room)
                    var placement = gson.fromJson(resp.data!!.asJsonObject.get("placement"), Placement::class.java)
                    val premisesLocal = Premises(placement.id, placement.name, placement.address, "", placement.consumer, placement.total_area.toFloat(), placement.living_area.toFloat(), IMAGE_MODE, value,false)
                    room.id = placement.id
                    room.firstSave = false
                    room.consumer = placement.consumer
                    saveRoomLocal(room)
                    openMainPage.postValue(Event(true))
                } else {
                    room.id = "firstInit"
                    room.firstSave = true
                    saveRoomLocal(room)
                    //  userMessage.postValue(Event("Помещение создано"))
                    openRegistration.postValue(Event(true))
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
        showAddressLocation.postValue(Event(false))
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

    fun getOpenRegistration() : MutableLiveData<Event<Boolean>> {
        return openRegistration
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

    fun getStaticAddressImage() : MutableLiveData<Event<Pair<String,String>>> {
        return staticAddressImage
    }

    fun getUserMessage() : MutableLiveData<Event<String>> {
        return userMessage
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

    fun getShowAddressLocation() : MutableLiveData<Event<Boolean>> {
        return showAddressLocation
    }

    fun getOpenMainPage() : MutableLiveData<Event<Boolean>> {
        return openMainPage
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