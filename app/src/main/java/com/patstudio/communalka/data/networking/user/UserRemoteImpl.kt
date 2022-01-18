package com.patstudio.communalka.data.networking.user

import android.util.Log
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Consumer
import com.patstudio.communalka.data.model.Gcm
import com.patstudio.communalka.data.model.OrderCreator
import kotlinx.coroutines.withContext

class UserRemoteImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val userService: UserService
): UserRemote  {

    override suspend fun login(phone: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("target",phone)
        userService.login(body)
    }

    override suspend fun sendCode(phone: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("target",phone)
        userService.sendCode(body)
    }

    override suspend fun getConsumer() = withContext(dispatcherProvider.default) {
        val body = JsonObject()

        userService.getConsumer()
    }

    override suspend fun getFaq() = withContext(dispatcherProvider.default) {
        userService.getFaq()
    }

    override suspend fun registration(fio: String, phone: String, email: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("fio",fio)
        body.addProperty("phone",phone)
        if (email.length > 0)
            body.addProperty("email",email)
        userService.registration(body)
    }

    override suspend fun updateFio(fio: String) {
        val body = JsonObject()
        body.addProperty("fio",fio)
        userService.updateFio(body)
    }

    override suspend fun confirmSmsCode(phone: String, smsCode: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("target",phone)
        body.addProperty("code",smsCode)
        userService.confirmSms(body)
    }

    override suspend fun updateEmail(email: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("email",email)
        userService.updateEmail(body)
    }

    override suspend fun createOrder(orderCreator: OrderCreator) = withContext(dispatcherProvider.default){
        userService.createOrderPayment(orderCreator)
    }

    override suspend fun updateGsm(gcm: Gcm)= withContext(dispatcherProvider.default) {
        userService.updateGcm(gcm)
    }

    override suspend fun registrationWithCode(
        fio: String,
        phone: String,
        email: String,
        smsCode: String
    ) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("fio",fio)
        body.addProperty("phone",phone)
        if (email.length > 0)
            body.addProperty("email",email)
        body.addProperty("code",smsCode)
        userService.registrationWithCode(body)
    }

    override suspend fun removePlacement(placementId: String) = withContext(dispatcherProvider.default) {
        userService.removePlacement(placementId)
    }

    override suspend fun getAccount(accountId: String) = withContext(dispatcherProvider.default) {
        userService.getAccount(accountId)
    }

    override suspend fun updateConsumer(consumer: Consumer) = withContext(dispatcherProvider.default) {
        userService.updateConsumer(consumer)
    }

    override suspend fun deleteAccount(accountId: String) = withContext(dispatcherProvider.default) {
        userService.deleteAccount(accountId)
    }

    override suspend fun createPersonalAccount(
        number: String,
        fio: String,
        supplier: String,
        service: String,
        placementId: String
    ) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("fio",fio)
        body.addProperty("number",number)
        body.addProperty("supplier",supplier)
        body.addProperty("service",service)
        userService.createAccount(body, placementId)
    }

    override suspend fun createMeterForAccount(
        title: String,
        serial_number: String,
        value: String,
        accountId: String
    )  = withContext(dispatcherProvider.default)  {
        val body = JsonObject()
        body.addProperty("title",title)
        body.addProperty("serial_number",serial_number)
        body.addProperty("value",value)
        userService.createMeter(body, accountId)
    }

    override suspend fun editMeterForAccount(
        title: String,
        serial_number: String,
        value: String?,
        accountId: String
    ) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("title",title)
        body.addProperty("value",value)
        userService.updateMeter(body, accountId)
    }

    override suspend fun getSuppliers() = withContext(dispatcherProvider.default)  {
        val body = JsonObject()

        body.addProperty("service","")
        userService.getSuppliers()
    }

    override suspend fun getMeters(accountId: String) = withContext(dispatcherProvider.default)  {
        userService.getListMeter(accountId)
    }

    override suspend fun getServices() = withContext(dispatcherProvider.default)  {
        userService.getServices()
    }

    override suspend fun removeMeter(meterId: String) = withContext(dispatcherProvider.default)  {
        userService.removeMeter(meterId)
    }

}