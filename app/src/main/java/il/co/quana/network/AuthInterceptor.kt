package il.co.quana.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.locks.ReentrantReadWriteLock

class AuthInterceptor(private val settings: ApiSettings) : Interceptor {

    private val TAG = "AuthInterceptor"
    private var apiService: ApiService? = null
    private var storedAuthToken: String? = null

    private val lock = ReentrantReadWriteLock(true)

    fun setApiService(apiService: ApiService) {
        this.apiService = apiService
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var authToken: String?

        var request = chain.request()
        lock.readLock().lock()
        try {
            authToken = getStoredAuthToken()
        } finally {
            lock.readLock().unlock()
        }

        request = addTokenToRequest(request, authToken)
        var response = chain.proceed(request)

        if (!response.header("X-Auth-Token").isNullOrEmpty()) {
            val newToken = response.header("X-Auth-Token") as String
            if (!newToken.isEmpty()) {
                updateToken(newToken)
            }
        }

        return response
    }

    private fun updateToken(newToken: String) {
        Log.d("wowAuth", "updateD Token: $newToken")
        storedAuthToken = newToken
        settings.token = newToken
    }

    private fun getStoredAuthToken(): String? {
        return settings.token
    }

    private fun addTokenToRequest(request: Request, authToken: String?): Request {
        var request = request
        if (authToken != null) {
            Log.d(TAG, "addTokenToRequest: $authToken")
            val requestBuilder = request.newBuilder().addHeader("x-api-key", authToken)
            request = requestBuilder.build()
        }
        return request
    }

}
