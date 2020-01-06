package il.co.quana.features

import android.app.Application
import androidx.lifecycle.*
import il.co.quana.CoroutineQuanaDeviceCommunicator
import il.co.quana.QuanaDeviceCommunicatorCallback
import il.co.quana.QuanaDeviceInfo
import il.co.quana.common.ProgressData
import il.co.quana.data.SampleRepository
import il.co.quana.model.SampleStatus
import il.co.quana.model.TagInfo
import il.co.quana.protocol.DeviceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


class TestDeviceViewModel(private val sampleRepository: SampleRepository, application: Application) : AndroidViewModel(application) {

    sealed class ScreenState{
        data class UpdateScreenState(val screenStateId: ScreenStatusID, val resultData: Any? = null): ScreenState()
    }

    enum class ScreenStatusID{
        DEVICE_IDLE,
        DEVICE_CONNECTING,
        DEVICE_CONNECTED,
        DEVICE_START_SCAN_ERROR,
        DEVICE_SCANNING,
        DEVICE_SCANNING_ERROR,
        DEVICE_SCAN_COMPLETE,
        DEVICE_DISCONNECTED,
        SERVER_REQUEST_SUCCESS,
        SERVER_REQUEST_FAIL
    }

    enum class TimerState{
        VISIBLE,
        GONE
    }

    private var tagInfoList  = mutableSetOf<TagInfo>()
    private var note: String? = null
    private var brand: String? = null
    private var product: String? = null
    private val samples = mutableListOf<CoroutineQuanaDeviceCommunicator.Sample>()

    private lateinit var quanaDeviceCommunicator: CoroutineQuanaDeviceCommunicator

    val progressData = ProgressData()
    val deviceInfo = MutableLiveData<QuanaDeviceInfo>()
    val screenState = MutableLiveData<ScreenState>()
    val sampleCount = MutableLiveData<Int>()
    val sampleCollectIndex = MutableLiveData<Int>()
    val timerState = MutableLiveData<TimerState>()

    init {
        screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_IDLE))
        timerState.postValue(TimerState.GONE)
    }

    fun initDevice(deviceAddress: String) {
        progressData.startProgress()
        viewModelScope.launch {
            launch(Dispatchers.IO){
                screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_CONNECTING))
                quanaDeviceCommunicator =  CoroutineQuanaDeviceCommunicator(
                    deviceAddress = deviceAddress,
                    applicationContext = getApplication(),
                    listener = object : QuanaDeviceCommunicatorCallback {
                        override fun deviceConnected() {
                            Timber.d("deviceConnected")
                            screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_CONNECTED))
                            progressData.endProgress()
                        }

                        override fun deviceDisconnected() {
                            Timber.d("deviceDisconnected")
                            screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_DISCONNECTED))
                            progressData.endProgress()
                        }

                        override fun deviceInfoReceived(info: QuanaDeviceInfo) {
                            Timber.d("deviceInfoReceived")
                            deviceInfo.postValue(info)
                        }
                    })
            }
        }

    }

    fun startScan() {
        screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_SCANNING))
        progressData.startProgress()
        viewModelScope.launch {
            launch(Dispatchers.IO){
                try {
                    timerState.postValue(TimerState.VISIBLE)
                    Timber.i("startScan")
                    val success = quanaDeviceCommunicator.startScan()
                    Timber.i("Scan is success : $success")
                    if (success){
                        checkDeviceStatus()
                    }else{
                        screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_START_SCAN_ERROR))
                        progressData.endProgress()
                        timerState.postValue(TimerState.GONE)
                    }
                }catch (ex: Exception) {
                    ex.printStackTrace()
                    Timber.i("Error in scanning")
                    screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_START_SCAN_ERROR))
                    progressData.endProgress()
                    timerState.postValue(TimerState.GONE)
                }
            }
        }
    }

    private fun checkDeviceStatus(){
//        progressData.startProgress()
        screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_SCANNING))
        viewModelScope.launch {
            launch(Dispatchers.IO){
                try {
                    Timber.i("checkDeviceStatus")
                    var deviceStatus = quanaDeviceCommunicator.getDeviceStatus()
                    Timber.i("deviceStatus: ${deviceStatus.name}")
                    while (deviceStatus != DeviceStatus.scanningComplete){
                        if (deviceStatus == DeviceStatus.failure){
                            screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_SCANNING_ERROR))
                            break
                        }
                        delay(10_000)
                        deviceStatus = quanaDeviceCommunicator.getDeviceStatus()
                        Timber.i("deviceStatus: ${deviceStatus.name}")
                    }
                    val scanResult = quanaDeviceCommunicator.getScanResults()
                    Timber.d("scanResult amountOfSamples: ${scanResult.amountOfSamples}")
                    progressData.endProgress()
                    timerState.postValue(TimerState.GONE)
                    screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_SCAN_COMPLETE))
                }catch (ex: Exception){
                    ex.printStackTrace()
                    Timber.i("Error on checkDeviceStatus")
                    screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.DEVICE_SCANNING_ERROR))
                    timerState.postValue(TimerState.GONE)
                    progressData.endProgress()
                }
            }
        }

    }

    fun sendData(){
        timerState.postValue(TimerState.VISIBLE)
        progressData.startProgress()
        viewModelScope.launch {
            launch(Dispatchers.IO){
                try {
                    val scanResult = quanaDeviceCommunicator.getScanResults()
                    val samples = getAllScans(scanResult.amountOfSamples.toInt())
                    val serverResult  = sampleRepository.sendSample(samples, tagInfoList, note, brand, product)
                    if(serverResult.status == SampleStatus.SUCCESS){
                        screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.SERVER_REQUEST_SUCCESS, serverResult.data))
                    }else{
                        screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.SERVER_REQUEST_FAIL, serverResult.message))
                    }
                    progressData.endProgress()
                    timerState.postValue(TimerState.GONE)
                }catch (ex: Exception) {
                    ex.printStackTrace()
                    screenState.postValue(ScreenState.UpdateScreenState(ScreenStatusID.SERVER_REQUEST_FAIL, "SERVER REQUEST FAIL"))
                    progressData.endProgress()
                    timerState.postValue(TimerState.GONE)
                }
            }
        }
    }


    private suspend fun getAllScans(amountOfScans: Int) : List<CoroutineQuanaDeviceCommunicator.Sample> = withContext(Dispatchers.IO){
        samples.clear()
        sampleCount.postValue(amountOfScans)
        val startTime = Calendar.getInstance().timeInMillis
        (1..amountOfScans).forEach { index ->
//            val countDownLatch = CountDownLatch(1)
            Timber.i("Getting sample $index/$amountOfScans")
            sampleCollectIndex.postValue(index)
            val sample = quanaDeviceCommunicator.getSample(index)
            samples.add(sample)
            Timber.i("Ready ${sample.sensorCode}/${sample.sensorCode}, sampleData=${sample.sampleData.size} bytes")
//            delay(500)
        }

        Timber.i("--- Done getting samples ---")
        Timber.d("Time to getAllScans: ${Calendar.getInstance().timeInMillis - startTime}")
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