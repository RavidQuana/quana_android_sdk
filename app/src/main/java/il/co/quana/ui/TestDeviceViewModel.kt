package il.co.quana.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import il.co.quana.CoroutineQuanaDeviceCommunicator
import il.co.quana.common.LiveEventData
import il.co.quana.common.ProgressData
import il.co.quana.data.SampleRepository
import il.co.quana.model.TagInfo
import il.co.quana.protocol.DeviceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber



class TestDeviceViewModel(private val sampleRepository: SampleRepository, application: Application) : AndroidViewModel(application) {

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
    private val samples = mutableListOf<CoroutineQuanaDeviceCommunicator.Sample>()

    private lateinit var quanaDeviceCommunicator: CoroutineQuanaDeviceCommunicator

    val navigationEvent = LiveEventData<NavigationEvent>()
    val progressData = ProgressData()
    val screenState = MutableLiveData<ScreenState>()


    fun initDevice(deviceAddress: String) {
        viewModelScope.launch {
            launch(Dispatchers.IO){
                quanaDeviceCommunicator =  CoroutineQuanaDeviceCommunicator(deviceAddress,  applicationContext = getApplication())
                delay(10_000)
                startScan()
            }
        }

    }

    private fun startScan() {
        screenState.postValue(ScreenState.DEVICE_IN_PROGRESS)
        progressData.startProgress()
        viewModelScope.launch {
            launch(Dispatchers.IO){
                try {
                    Timber.i("startScan")
                    val success = quanaDeviceCommunicator.startScan()
                    Timber.i("Scan is success : $success")
                    if (success){

                        checkDeviceStatus()
                    }else{
                        screenState.postValue(ScreenState.DEVICE_ERROR)
                        progressData.endProgress()
                    }
                }catch (ex: Exception) {
                    ex.printStackTrace()
                    Timber.i("Error in scanning")
                    screenState.postValue(ScreenState.DEVICE_ERROR)
                    progressData.endProgress()
                }
            }
        }
    }

    private fun checkDeviceStatus(){
//        progressData.startProgress()
        screenState.postValue(ScreenState.DEVICE_IN_PROGRESS)
        viewModelScope.launch {
            launch(Dispatchers.IO){
                try {
                    Timber.i("checkDeviceStatus")
                    var deviceStatus = quanaDeviceCommunicator.getDeviceStatus()
                    Timber.i("deviceStatus: ${deviceStatus.name}")
                    while (deviceStatus != DeviceStatus.scanningComplete){
                        if (deviceStatus == DeviceStatus.failure){
                            screenState.postValue(ScreenState.DEVICE_ERROR)
                            break
                        }
                        delay(10_000)
                        deviceStatus = quanaDeviceCommunicator.getDeviceStatus()
                        Timber.i("deviceStatus: ${deviceStatus.name}")
                    }
                    progressData.endProgress()
                    screenState.postValue(ScreenState.DEVICE_SCAN_SUCCESS)
                }catch (ex: Exception){
                    ex.printStackTrace()
                    Timber.i("Error on checkDeviceStatus")
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
                    val scanResult = quanaDeviceCommunicator.getScanResults()
                    val samples = getAllScans(scanResult.amountOfSamples.toInt())
                    val serverResult  = sampleRepository.sendSample(samples, tagInfoList, note, brand, product)
                    //TODO == check the result

                    progressData.endProgress()
                }catch (ex: Exception) {
                    ex.printStackTrace()
                    navigationEvent.postRawValue(NavigationEvent.RequestResult(ResultType.ERROR))
                    progressData.endProgress()
                }
            }
        }
    }


    private suspend fun getAllScans(amountOfScans: Int) : List<CoroutineQuanaDeviceCommunicator.Sample> = withContext(Dispatchers.IO){
        samples.clear()
        (1..amountOfScans).forEach { index ->
//            val countDownLatch = CountDownLatch(1)
            Timber.i("Getting sample $index/$amountOfScans")
            val sample = quanaDeviceCommunicator.getSample(index)
            samples.add(sample)
            Timber.i("Ready ${sample.sensorCode}/${sample.sensorCode}, sampleData=${sample.sampleData.size} bytes")
            delay(500)
        }
        Timber.i("--- Done getting samples ---")
        samples
    }


    fun sendNow(){
        progressData.startProgress()
        viewModelScope.launch(Dispatchers.IO){
            launch(Dispatchers.IO){
                try {
                    sampleRepository.sendSample(samples = samples, brand = brand, product = product,tagInfoList = tagInfoList, note = note)
                    progressData.endProgress()
                }catch (ex: Exception){
                    ex.printStackTrace()
                    progressData.endProgress()
                }
            }
        }
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