package il.co.quana.model

import com.google.gson.annotations.SerializedName
import java.io.File


data class ServerResponse<T>(
    /**
     * At the moment @param(code) duplicates the HTTP_STATUS code.
     * To avoid possible misuse we will disable it.
     * @param(code) Should be used for application domain errors
     */
//    val code: Int? = null,
    val message: String? = null,
    val data: T? = null
)

enum class TagInfo{
    MOLD,
    PESTICIDE,
    SATIVA,
    INDICA
}

data class SampleRequest(
    val sample: File,
    val brand: String ="test",
    val product:String = "test",
    val tags: List<String> = emptyList()
)

data class SampleResponse(
    val message: String?,
    val status: SampleStatus,
    val data: List<SampleResponseData>?
)

data class SampleResponseData(
    val name: String?,
    val Percentage: Double?
)

enum class SampleStatus{
    @SerializedName("success") SUCCESS,
    @SerializedName("error") ERROR,
}
