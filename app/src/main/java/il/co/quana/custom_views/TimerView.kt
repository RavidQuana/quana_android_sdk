package il.co.quana.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import il.co.quana.R
import kotlinx.android.synthetic.main.timer_view.view.*
import timber.log.Timber
import java.util.*
import kotlin.concurrent.timerTask

const val TIME_PERIOD_IN_MILLISECONDS = 1_000

class TimerView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    interface OnTimerViewListener{
        fun onTimeFinish()
    }

    private var listener: OnTimerViewListener? = null
    private var timer: Timer? = null
    private var timeDate: Date? = null
    private var timeInMilliSeconds: Long = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.timer_view, this, true)
    }

    fun setTime(timeDate: Date){
        this.timeDate = timeDate
    }

    fun setListener(listener: OnTimerViewListener){
        this.listener = listener
    }

    fun startTimer(){
        Timber.d("startTimer")
        timer = Timer()
        val startTimeInMilliSeconds = Calendar.getInstance().timeInMillis
        timer?.scheduleAtFixedRate(timerTask {
            timerViewTimeText.post {
                val time = Calendar.getInstance().timeInMillis - startTimeInMilliSeconds
                timerViewTimeText.text = formattedTimeForTimerDisplay(time)
            }
        }, 0,TIME_PERIOD_IN_MILLISECONDS.toLong())
    }

    fun stopTimer() {
        timer?.let {
            it.cancel()
        }
        timer = null
    }

    override fun onDetachedFromWindow() {
        stopTimer()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        startTimer()
    }

    //Display time for timer view { DD : HH : MM : SS}
    fun formattedTimeForTimerDisplay(milliseconds: Long): String {
        val timeInSec = milliseconds / 1_000
        val secs = timeInSec % 60
        var minuts = timeInSec / 60
        var hours: Long = 0
        var days: Long = 0
        if (minuts >= 60) {
            hours = minuts / 60
            minuts %= 60
            if (hours >= 24) {
                days = hours / 24
                hours %= 24
            }
        }
        return String.format("%02d : %02d", minuts, secs)
    }

    fun resetTimer() {
        stopTimer()
        timerViewTimeText.text = formattedTimeForTimerDisplay(0)
    }
}