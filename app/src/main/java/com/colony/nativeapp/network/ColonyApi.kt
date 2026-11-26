package com.colony.nativeapp.network

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class ColonyApiResponse(
    val ok: Boolean,
    val statusCode: Int,
    val data: JSONObject? = null,
    val error: String? = null
)

class ColonyApi(context: Context) {

    private val cookieJar = ColonyCookieJar(context.applicationContext)

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseUrl = ColonyNetworkConfig.apiBaseUrl.trimEnd('/')

    private val jsonMediaType =
        "application/json; charset=utf-8".toMediaType()

    suspend fun login(
        username: String,
        password: String
    ): ColonyApiResponse = post(
        "/api/auth/login",
        JSONObject().apply {
            put("username", username)
            put("password", password)
        }
    )

    suspend fun register(
        username: String,
        password: String
    ): ColonyApiResponse = post(
        "/api/auth/register",
        JSONObject().apply {
            put("username", username)
            put("password", password)
        }
    )

    suspend fun checkSession(): ColonyApiResponse =
        execute("/api/auth/me")

    suspend fun logout(): ColonyApiResponse {
        return try {
            post(
                "/api/auth/logout",
                JSONObject()
            )
        } finally {
            cookieJar.clear()
        }
    }

    private suspend fun post(
        path: String,
        json: JSONObject
    ): ColonyApiResponse = withContext(Dispatchers.IO) {
        val body = json.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("$baseUrl$path")
            .post(body)
            .header("Accept", "application/json")
            .build()

        performRequest(request)
    }

    private suspend fun execute(
        path: String
    ): ColonyApiResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl$path")
            .get()
            .header("Accept", "application/json")
            .build()

        performRequest(request)
    }

    private fun performRequest(
        request: Request
    ): ColonyApiResponse {
        return try {
            client.newCall(request).execute().use { response ->
                val responseText = response.body?.string().orEmpty()

                val json = try {
                    if (responseText.isNotBlank()) {
                        JSONObject(responseText)
                    } else {
                        JSONObject()
                    }
                } catch (_: Exception) {
                    JSONObject()
                }

                val errorMessage = json.optString("error")
                    .takeIf { it.isNotBlank() }

                ColonyApiResponse(
                    ok = response.isSuccessful,
                    statusCode = response.code,
                    data = json,
                    error = if (response.isSuccessful) {
                        null
                    } else {
                        errorMessage ?: "Server request failed (${response.code})."
                    }
                )
            }
        } catch (e: IOException) {
            ColonyApiResponse(
                ok = false,
                statusCode = 0,
                error = "Cannot connect to Colony server: ${e.message ?: "Network error"}"
            )
        } catch (e: Exception) {
            ColonyApiResponse(
                ok = false,
                statusCode = 0,
                error = "Unexpected error: ${e.message ?: "Unknown error"}"
            )
        }
    }
}
