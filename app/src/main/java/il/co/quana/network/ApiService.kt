package il.co.quana.network

import il.co.quana.model.MetaDataModel
import il.co.quana.model.ServerResponse
import retrofit2.http.GET



interface ApiService {

    //Utils
    @GET("utils/meta")
    suspend fun fetchMeta(): ServerResponse<MetaDataModel>

}