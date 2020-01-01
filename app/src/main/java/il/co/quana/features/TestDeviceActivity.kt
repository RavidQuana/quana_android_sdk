package il.co.quana.features

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.monkeytech.playform.common.AfterTextChangedWatcher
import il.co.quana.*
import il.co.quana.isLocationPermissionGranted
import il.co.quana.model.SampleResponseData
import il.co.quana.model.TagInfo
import kotlinx.android.synthetic.main.activity_test_device.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class TestDeviceActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DEVICE_MAC_ADDRESS = "EXTRA_DEVICE_MAC_ADDRESS"
    }

    private val viewModel by viewModel<TestDeviceViewModel>()

    private lateinit var adapter: SampleResponseDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_device)

        if (isLocationPermissionGranted()) {
            startFlow()
        } else {
            Timber.e("ACCESS_COARSE_LOCATION is not granted. Stopping...")
        }

        initObservers()
        initUi()
    }

    private fun startFlow() {
        val address = intent.getStringExtra(DeviceActivity.EXTRA_DEVICE_MAC_ADDRESS)
        if (TextUtils.isEmpty(address)) {
            Timber.e("EXTRA_DEVICE_MAC_ADDRESS is required. Stopping...")
        } else {
            connectToDevice(address)
        }
    }

    private fun connectToDevice(deviceAddress: String) {
        viewModel.initDevice(deviceAddress)
    }

    private fun initObservers() {
        viewModel.progressData.observe(this, Observer {
            isLoading -> handleProgressData(isLoading)
        })
        viewModel.screenState.observe(this, Observer {
            screenState -> handleScreenState(screenState)
        })
        viewModel.deviceInfo.observe(this, Observer {
            deviceFirmwareVersion -> handleDeviceFirmwareVersion(deviceFirmwareVersion)
        })
        viewModel.sampleCount.observe(this, Observer {
            sampleCount -> handleSampleCount(sampleCount)
        })
        viewModel.sampleCollectIndex.observe(this, Observer {
            sampleCollectIndex -> handleSampleCollectIndex(sampleCollectIndex)
        })
        viewModel.timerState.observe(this, Observer {
            timerState -> handleTimerState(timerState)
        })
    }

    private fun handleTimerState(timerState: TestDeviceViewModel.TimerState?) {
        timerState?.let {
            when(it){
                TestDeviceViewModel.TimerState.VISIBLE ->{
                    showTimer()
                }
                TestDeviceViewModel.TimerState.GONE ->{
                    hideTimer()
                }
            }
        }
    }

    private fun handleSampleCollectIndex(sampleCollectIndex: Int?) {
        sampleCollectIndex?.let {
            testActSampleCollectIndex.text = it.toString()
        }

    }

    private fun handleSampleCount(sampleCount: Int?) {
        sampleCount?.let {
            testActSampleCount.text = " / $it"
        }
    }

    private fun handleDeviceFirmwareVersion(deviceFirmwareVersion: QuanaDeviceInfo?) {
        deviceFirmwareVersion?.let {
            firmwareRevision.text = "DeviceFirmwareVersion: ${it.firmwareRevision}"
            manufacturerName.text = "ManufactureName: ${it.manufacturerName}"
        }
    }

    private fun initUi() {

        testActRecycler.layoutManager = LinearLayoutManager(this)
        adapter = SampleResponseDataAdapter()
        testActRecycler.adapter = adapter
        testActRecycler.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        testActRecycler.visibility = View.GONE

        testDeviceActSendButton.setOnClickListener {
            viewModel.sendData()
        }
        buttonMold.setOnClickListener {
            updateTagItem(buttonMold, TagInfo.MOLD)
        }
        buttonPesticide.setOnClickListener {
            updateTagItem(buttonPesticide, TagInfo.PESTICIDE)
        }
        buttonSativa.setOnClickListener {
            updateTagItem(buttonSativa, TagInfo.SATIVA)
        }
        buttonIndica.setOnClickListener {
            updateTagItem(buttonIndica, TagInfo.INDICA)
        }
        note.addTextChangedListener(object : AfterTextChangedWatcher(){
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    viewModel.updateNote(it.toString())
                }}
        })
        brand.addTextChangedListener(object : AfterTextChangedWatcher(){
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    viewModel.updateBrand(it.toString())
                }}
        })
        product.addTextChangedListener(object :AfterTextChangedWatcher(){
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    viewModel.updateProduct(it.toString())
                }}
        })
        testDeviceActSendButton.setOnClickListener {
            viewModel.sendData()
        }
        restartBtn.setOnClickListener {
            val intent = Intent(this, DeviceLookupActivity::class.java)
            startActivity(intent)
            finish()
        }

        startScanBtn.setOnClickListener {
            viewModel.startScan()
        }

    }

    private fun handleScreenState(screenState: TestDeviceViewModel.ScreenState?) {
        screenState?.let {
            when(it){
                is TestDeviceViewModel.ScreenState.UpdateScreenState ->{
                    when(it.screenStateId){
                        TestDeviceViewModel.ScreenStatusID.DEVICE_IDLE,
                        TestDeviceViewModel.ScreenStatusID.DEVICE_CONNECTING ->{
                            hideAll()
                        }
                        TestDeviceViewModel.ScreenStatusID.DEVICE_SCANNING->{
                            hideAll()
                        }
                        TestDeviceViewModel.ScreenStatusID.DEVICE_CONNECTED ->{
                            startScanBtn.visibility = View.VISIBLE
                            testDeviceActSendDetailsLayout.visibility = View.GONE
                            scanServerResult.visibility = View.GONE
                        }
                        TestDeviceViewModel.ScreenStatusID.DEVICE_START_SCAN_ERROR ->{
                            hideAll()
                            scanServerResult.visibility = View.VISIBLE
                            scanResultText.text = "START SCAN ERROR"
                        }
                        TestDeviceViewModel.ScreenStatusID.DEVICE_SCANNING_ERROR ->{
                            hideAll()
                            scanServerResult.visibility = View.VISIBLE
                            scanResultText.text = "DEVICE SCANNING ERROR"
                        }
                        TestDeviceViewModel.ScreenStatusID.DEVICE_SCAN_COMPLETE ->{
                            hideAll()
                            testDeviceActSendDetailsLayout.visibility = View.VISIBLE
                        }
                        TestDeviceViewModel.ScreenStatusID.DEVICE_DISCONNECTED ->{
                            hideAll()
                            scanServerResult.visibility = View.VISIBLE
                            scanResultText.text = "DEVICE DISCONNECTED"
                        }
                        TestDeviceViewModel.ScreenStatusID.SERVER_REQUEST_SUCCESS ->{
                            if (it.resultData is List<*>){
                                handleServerResponse("SUCCESS", it.resultData as List<SampleResponseData> )
                            }
                        }
                        TestDeviceViewModel.ScreenStatusID.SERVER_REQUEST_FAIL ->{
                            handleServerResponse(it.resultData.toString(), emptyList())
                        }
                    }
                }
            }
        }
    }

    private fun showTimer() {
        testActTimer.visibility = View.VISIBLE
        testActTimer.startTimer()
    }

    private fun hideTimer(){
        testActTimer.visibility = View.GONE
        testActTimer.stopTimer()
    }

    private fun hideAll() {
        startScanBtn.visibility = View.GONE
        testDeviceActSendDetailsLayout.visibility = View.GONE
        scanServerResult.visibility = View.GONE
    }


    private fun handleProgressData(isLoading: Boolean?) {
        isLoading?.let {
            testDeviceActProgressBar.visibility = if(it) View.VISIBLE else View.GONE
        }
    }


    private fun handleServerResponse(serverResponseStatus: String, serverResponseData: List<SampleResponseData>){
        startScanBtn.visibility = View.GONE
        testDeviceActSendDetailsLayout.visibility = View.GONE
        scanServerResult.visibility = View.VISIBLE
        Toast.makeText(this, "Result: ${serverResponseStatus}", Toast.LENGTH_LONG).show()
        scanResultText.text = serverResponseStatus
        if (::adapter.isInitialized){
            testActRecycler.visibility = View.VISIBLE
            adapter.submitList(serverResponseData)
        }
        scanServerResult.visibility = View.VISIBLE
        testDeviceActSendDetailsLayout.visibility = View.GONE
    }

    private fun updateTagItem(view: CheckBox, tagInfo: TagInfo){
        if (view.isChecked){
            viewModel.addNewTagInfo(tagInfo)
        }else{
            viewModel.removeTagInfo(tagInfo)
        }
    }
}
