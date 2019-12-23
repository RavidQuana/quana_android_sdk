package il.co.quana.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.lifecycle.Observer
import com.monkeytech.playform.common.AfterTextChangedWatcher
import il.co.quana.DeviceActivity
import il.co.quana.QuanaDeviceCommunicatorFactory
import il.co.quana.R
import il.co.quana.common.LiveEvent
import il.co.quana.isLocationPermissionGranted
import il.co.quana.model.TagInfo
import kotlinx.android.synthetic.main.activity_test_device.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class TestDeviceActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DEVICE_MAC_ADDRESS = "EXTRA_DEVICE_MAC_ADDRESS"
    }

    private val viewModel by viewModel<TestDeviceViewModel>()

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
        viewModel.navigationEvent.observe(this, Observer {
            navigationEvent -> handleNavigationEvent(navigationEvent)
        })
        viewModel.progressData.observe(this, Observer {
            isLoading -> handleProgressData(isLoading)
        })
        viewModel.screenState.observe(this, Observer {
            screenState -> handleScreenState(screenState)
        })
    }

    private fun initUi() {
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
            //TODO == restart !!!
        }
    }

    private fun handleScreenState(screenState: TestDeviceViewModel.ScreenState?) {
        screenState?.let {
            when(it){
                TestDeviceViewModel.ScreenState.DEVICE_IN_PROGRESS ->{
                    scanResultLayout.visibility = View.GONE
                    testDeviceActSendDetailsLayout.visibility = View.GONE
                }
                TestDeviceViewModel.ScreenState.DEVICE_SCAN_SUCCESS ->{
                    scanResultLayout.visibility = View.GONE
                    testDeviceActSendDetailsLayout.visibility = View.VISIBLE
                }
                TestDeviceViewModel.ScreenState.DEVICE_ERROR ->{
                    scanResultLayout.visibility = View.VISIBLE
                    testDeviceActSendDetailsLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun handleProgressData(isLoading: Boolean?) {
        isLoading?.let {
            testDeviceActProgressBar.visibility = if(it) View.VISIBLE else View.GONE
        }
    }

    private fun handleNavigationEvent(navigationEvent: LiveEvent<TestDeviceViewModel.NavigationEvent>?) {
        navigationEvent?.let {
            val event = it.getContentIfNotHandled()
            event?.let {navigationEvent->
                when(navigationEvent){
                    is TestDeviceViewModel.NavigationEvent.RequestResult ->{
                        Toast.makeText(this, "Result: ${navigationEvent.resultType}", Toast.LENGTH_LONG).show()
                        scanResultText.text = navigationEvent.resultType.name
                    }
                }
            }
        }
    }

    private fun updateTagItem(view: CheckBox, tagInfo: TagInfo){
        if (view.isChecked){
            viewModel.addNewTagInfo(tagInfo)
        }else{
            viewModel.removeTagInfo(tagInfo)
        }
    }
}
