package il.co.quana.model


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

data class MetaDataModel(
    val settings: ArrayList<AppSetting>? = null,
    val symptomCategories: List<SymptomCategory>? = null,
    val treatments: List<Treatment>? = null,
    val products: List<Product>? = null,
    val side_effects: List<SideEffect>? = null
)

data class AppSetting(
    val id: Long,
    val key: String,
    val dataType: String? = null,
    val value: Any? = null
)

data class SymptomCategory(
    val id: Int,
    val name: String?,
    val symptoms: List<Symptom>?
)

data class Symptom(
    val id: Int,
    val name: String
)

data class Treatment(
    val id: Int,
    val name: String
)

data class Product(
    val id: Int,
    val name: String?,
    val pros: String?,
    val cons: String?,
    val hasMold: Boolean?
)

data class SideEffect(
    val id: Int,
    val name: String
)