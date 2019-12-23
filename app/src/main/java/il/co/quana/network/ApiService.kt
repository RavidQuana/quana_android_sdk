package il.co.quana.network

import il.co.quana.model.MetaDataModel
import il.co.quana.model.SampleRequest
import il.co.quana.model.ServerResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


interface ApiService {

    //Utils
    @GET("utils/meta")
    suspend fun fetchMeta(): ServerResponse<MetaDataModel>


    /**
     * @param note
     * @param List<TagInfo>
     * @param product
     * @param brand
     * @param file
     */
//    @Multipart
//    @PUT("upload_white_sample")
//    suspend fun uploadSample(@Body sampleRequest : SampleRequest): ResponseBody


    @Multipart
    @PUT("upload_white_sample")
    suspend fun uploadSample(
        @Part(value = "file") file: RequestBody,//MultipartBody.Part,
        @Part(value = "brand") brand: RequestBody,
        @Part(value = "product") product: RequestBody,
        @Part(value = "note") note: RequestBody,
        @Part(value = "tegs") tegs: RequestBody): ResponseBody

}