package il.co.quana.ui

import android.app.Application
import androidx.lifecycle.*
import il.co.quana.QuanaApplication
import il.co.quana.QuanaDeviceCommunicator
import il.co.quana.QuanaDeviceCommunicatorFactory
import il.co.quana.common.LiveEventData
import il.co.quana.common.ProgressData
import il.co.quana.model.TagInfo
import il.co.quana.model.MetaDataModel
import il.co.quana.model.Product
import il.co.quana.network.ApiService
import il.co.quana.protocol.DeviceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestDeviceViewModel(private val apiService: ApiService, application: Application) : AndroidViewModel(application) {

    sealed class NavigationEvent{
        data class RequestResult(val resultType: ResultType, val resultData: Any? = null): NavigationEvent()
    }

    enum class ResultType{
        SUCCESS,
        ERROR
    }

    enum class ScreenState{
        DEVICE_IN_PROGRESS,
        DEVICE_SCAN_SUCCESS,
        DEVICE_ERROR,
    }

    private var tagInfoList  = mutableSetOf<TagInfo>()
    private var note: String? = null
    private var brand: String? = null
    private var product: String? = null

    private lateinit var quanaDeviceCommunicator: QuanaDeviceCommunicator

    val navigationEvent = LiveEventData<NavigationEvent>()
    val progressData = ProgressData()
    val screenState = MutableLiveData<ScreenState>()


    fun initDevice(deviceAddress: String) {
        //TODO == init quanaDeviceCommunicator
        /*
        quanaDeviceCommunicator = QuanaDeviceCommunicatorFactory.createQuanaDeviceCommunicator(
            getApplication(),
            deviceAddress
        )
        startScan()

         */
    }

    private fun startScan() {
        screenState.postValue(ScreenState.DEVICE_IN_PROGRESS)
        progressData.startProgress()
        viewModelScope.launch {
            launch(Dispatchers.IO){
                try {
                    delay(1_000)
                    quanaDeviceCommunicator.startScan {
                        if (!it) {
                            screenState.postValue(ScreenState.DEVICE_ERROR)
                            progressData.endProgress()
                        }else{
                            checkDeviceStatus()
                        }
                    }
                }catch (ex: Exception) {
                    ex.printStackTrace()
                    screenState.postValue(ScreenState.DEVICE_ERROR)
                    progressData.endProgress()
                }
            }
        }
    }

    fun sendData(){
        progressData.startProgress()
        viewModelScope.launch {
            launch(Dispatchers.IO){
                try {
                    val meta = fetchMeta()
                    if (meta == null){
                        navigationEvent.postRawValue(NavigationEvent.RequestResult(ResultType.ERROR))
                    }else{
                        navigationEvent.postRawValue(NavigationEvent.RequestResult(ResultType.SUCCESS, ""))
                    }
                }catch (ex: Exception){
                    ex.printStackTrace()
                    navigationEvent.postRawValue(NavigationEvent.RequestResult(ResultType.ERROR))
                }finally {
                    progressData.endProgress()
                }
            }
        }
    }

    private fun checkDeviceStatus(){
        progressData.startProgress()
        screenState.postValue(ScreenState.DEVICE_IN_PROGRESS)
        viewModelScope.launch {
            launch(Dispatchers.IO){
                try {
                    val scanStatus =

                    //TODO == need to check about device status avery 10 sec
                    delay(10_000)
                    screenState.postValue(ScreenState.DEVICE_SCAN_SUCCESS)
                }catch (ex: Exception){
                    ex.printStackTrace()
                    screenState.postValue(ScreenState.DEVICE_ERROR)
                }finally {
                    progressData.endProgress()
                }
            }
        }

    }


    private suspend fun fetchMeta(): MetaDataModel? = withContext(Dispatchers.IO){
        apiService.fetchMeta().data
    }

    fun addNewTagInfo(tagInfo: TagInfo) {
        tagInfoList.add(tagInfo)
    }

    fun removeTagInfo(tagInfo: TagInfo) {
        if (tagInfoList.contains(tagInfo)){
            tagInfoList.remove(tagInfo)
        }
    }

    fun updateNote(note: String) {
        this.note = note
    }

    fun updateBrand(brand: String){
        this.brand = brand
    }

    fun updateProduct(product: String){
        this.product = product
    }

}