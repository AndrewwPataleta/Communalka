import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.text.InputFilter
import android.util.Patterns
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.data.model.APIResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException


fun CharSequence?.isValidPhoneNumber():Boolean{
    return !isNullOrEmpty() && Patterns.PHONE.matcher(this).matches()
}

fun CharSequence?.isEmailValid(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

inline fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, crossinline action: () -> Unit) = GlobalScope.launch {
    delay(delayMillis)
    if (repeatMillis > 0) {
        while (true) {
            action()
            delay(repeatMillis)
        }
    } else {
        action()
    }
}

fun EditText.setMaxLength(maxLength: Int){
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
}

fun View.visible(animate: Boolean = true) {
    if (animate) {
        animate().alpha(1f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                visibility = View.VISIBLE
            }
        })
    } else {
        visibility = View.VISIBLE
    }
}

fun convertErrorBody(throwable: HttpException): APIResponse<JsonElement> {

    val type = object : TypeToken<APIResponse<JsonElement>>() {}.type
    return Gson().fromJson(throwable.response()?.errorBody()!!.charStream().readText(), type)

}

fun View.invisible(animate: Boolean = true) {
    hide(View.INVISIBLE, animate)
}

fun View.gone(animate: Boolean = true) {
    hide(View.GONE, animate)
}

fun View.visibleOrInvisible(show: Boolean, animate: Boolean = true) {
    if (show) visible(animate) else invisible(animate)
}

fun View.visibleOrGone(show: Boolean, animate: Boolean = true) {
    if (show) visible(animate) else gone(animate)
}

private fun View.hide(hidingStrategy: Int, animate: Boolean = true) {
    if (animate) {
        animate().alpha(0f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = hidingStrategy
            }
        })
    } else {
        visibility = hidingStrategy
    }
}

