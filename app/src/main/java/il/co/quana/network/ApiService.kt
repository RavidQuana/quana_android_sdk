package il.co.quana.network

import il.co.quana.model.SampleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface ApiService {

    /**
     * @param note
     * @param List<TagInfo>
     * @param product
     * @param brand
     * @param file
     */
    @Multipart
    @PUT("upload_white_sample")
    suspend fun uploadSample(
        @Part file : MultipartBody.Part,
        @Part(value = "brand") brand: RequestBody,
        @Part(value = "product") product: RequestBody,
        @Part(value = "note") note: RequestBody,
        @Part(value = "tags") tags: RequestBody): SampleResponse

}

