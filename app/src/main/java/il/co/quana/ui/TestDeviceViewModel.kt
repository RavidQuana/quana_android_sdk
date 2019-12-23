package il.co.quana.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import il.co.quana.common.LiveEventData
import il.co.quana.common.ProgressData
import il.co.quana.model.MetaDataModel
import il.co.quana.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestDeviceViewModel(private val apiService: ApiService) : ViewModel(){

    sealed class NavigationEvent{
        data class RequestResult(val resultType: ResultType, val resultData: Any? = null): NavigationEvent()
    }

    enum class ResultType{
        SUCCESS,
        ERROR
    }

    val navigationEvent = LiveEventData<NavigationEvent>()
    val progressData = ProgressData()

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

    private suspend fun fetchMeta(): MetaDataModel? = withContext(Dispatchers.IO){
        apiService.fetchMeta().data
    }
}