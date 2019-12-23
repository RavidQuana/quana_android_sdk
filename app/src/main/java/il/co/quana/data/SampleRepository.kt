package il.co.quana.data

import android.provider.ContactsContract
import il.co.quana.CoroutineQuanaDeviceCommunicator
import il.co.quana.model.SampleRequest
import il.co.quana.model.TagInfo
import il.co.quana.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class SampleRepository(private val apiService: ApiService) {

    suspend fun sendSample(
        samples: List<CoroutineQuanaDeviceCommunicator.Sample>,
        tagInfoList: MutableSet<TagInfo>,
        note: String?,
        brand: String?,
        product: String?
    ) = withContext(Dispatchers.IO){

        val outputStream = ByteArrayOutputStream()
        samples.forEach {
            outputStream.write(it.sampleData)
        }

//        val file = File(fileUri.path)
//        val inVal = FileInputStream(file)
        //val buf: ByteArray
        //buf = ByteArray(inVal.available())
//        while (inVal.read(buf) !== -1){}


        //MediaType.parse("application/octet-stream")
        val requestBody = RequestBody.create(MultipartBody.FORM, outputStream.toByteArray())

        val _body = MultipartBody.Part.createFormData("file", "test", requestBody)
        val _brand = RequestBody.create(MultipartBody.FORM, "test")
        val _product = RequestBody.create(MultipartBody.FORM, "test")
        val _tags = RequestBody.create(MultipartBody.FORM, "[]")
        val _note = RequestBody.create(MultipartBody.FORM, "test")

//        val sample: File,
//        val brand: String ="test",
//        val product:String = "test",
//        val tags: List<String> = emptyList()

        apiService.uploadSample(file = requestBody,brand =  _brand, product = _product,tegs = _tags, note = _note)

    }
}