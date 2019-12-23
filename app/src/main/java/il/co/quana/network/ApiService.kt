package il.co.quana.network

import il.co.quana.model.MetaDataModel
import il.co.quana.model.ServerResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT


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
    @PUT("upload_white_sample")
    suspend fun uploadSample(@Body sampleRequest : RequestBody): ResponseBody

}