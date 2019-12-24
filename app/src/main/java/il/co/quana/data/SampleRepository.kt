package il.co.quana.data

import android.content.Context
import il.co.quana.CoroutineQuanaDeviceCommunicator
import il.co.quana.model.TagInfo
import il.co.quana.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import com.google.gson.Gson


class SampleRepository(val context: Context,  val apiService: ApiService) {

    suspend fun sendSample(
        samples: List<CoroutineQuanaDeviceCommunicator.Sample>,
        tagInfoList: MutableSet<TagInfo>,
        note: String?,
        brand: String?,
        product: String?
    ) = withContext(Dispatchers.IO){

        val outputStream = ByteArrayOutputStream()
        samples.forEach {
            outputStream.write(it.rawData)
        }

        /*
        //This for testing
        val am = context.assets
        val inputS = am.open("application.bin")
        val buf: ByteArray
        buf = ByteArray(inputS.available())
        while (inputS.read(buf) !== -1){}
        val body = RequestBody.create(MultipartBody.FORM, buf)
         */

        val s = Gson().toJson(tagInfoList).toString()

        val requestBody = RequestBody.create(MultipartBody.FORM, outputStream.toByteArray())

        val _body = MultipartBody.Part.createFormData("sample", "test", requestBody)
        val _brand = RequestBody.create(MultipartBody.FORM, brand)
        val _product = RequestBody.create(MultipartBody.FORM, product)
        val _tags = RequestBody.create(MultipartBody.FORM, s)
        val _note = RequestBody.create(MultipartBody.FORM, note)


        apiService.uploadSample(file = _body,brand =  _brand, product = _product,tags =  _tags, note = _note)

    }
}