package il.co.quana.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import il.co.quana.R
import il.co.quana.common.LiveEvent
import kotlinx.android.synthetic.main.activity_test_device.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TestDeviceActivity : AppCompatActivity() {

    private val viewModel by viewModel<TestDeviceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_device)


        initObservers()
        initUi()
    }

    private fun initUi() {
        testDeviceActSendButton.setOnClickListener {
            viewModel.sendData()
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer {
            navigationEvent -> handleNavigationEvent(navigationEvent)
        })
        viewModel.progressData.observe(this, Observer {
            isLoading -> handleProgressData(isLoading)
        })
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
                    }
                }
            }
        }
    }
}
