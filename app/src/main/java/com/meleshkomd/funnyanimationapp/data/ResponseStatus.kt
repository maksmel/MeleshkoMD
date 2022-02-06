package com.meleshkomd.funnyanimationapp.data

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.json.FuelJson
import com.github.kittinunf.result.Result

class ResponseStatus() {
    private lateinit var response : Result<FuelJson, FuelError>
    private var error : Exception? = null

    fun setResponse(result: Result<FuelJson, FuelError>) {
        response = result
    }

    fun setException(error : Exception) {
        this.error = error
    }

    fun getResponse() : Result<FuelJson, FuelError> {
        return response
    }

    fun getException() : Exception? {
        return error
    }
}