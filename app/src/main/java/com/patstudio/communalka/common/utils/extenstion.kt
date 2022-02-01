import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.graphics.Bitmap
import android.text.InputFilter
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.Group
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.data.model.APIResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.R.drawable
import android.content.Context

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.patstudio.communalka.R
import java.text.DecimalFormatSymbols


fun CharSequence?.isValidPhoneNumber():Boolean{
    return !isNullOrEmpty() && Patterns.PHONE.matcher(this).matches()
}

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun CharSequence?.isEmailValid(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


fun getServiceIcon(serviceName: String, context: Context): Drawable {

    lateinit var drawable: Drawable

    when (serviceName) {
        "Водоснабжение" -> {
            drawable = context.resources.getDrawable(R.drawable.ic_water)
        }
        "Газ" -> {
            drawable = context.resources.getDrawable(R.drawable.ic_gas)
        }
        "Теплоснабжение" -> {
            drawable = context.resources.getDrawable(R.drawable.ic_warm)
        }
        "Электричество" -> {
            drawable = context.resources.getDrawable(R.drawable.ic_electricity)
        }
        else -> { drawable = context.resources.getDrawable(R.mipmap.ic_launcher) }
    }
    return drawable
}



 fun roundOffTo2DecPlaces(value: Float): String? {

    val DECIMAL_FORMAT = "###,###.#"
    val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
    formatSymbols.decimalSeparator = ','
    formatSymbols.groupingSeparator = ' '
    val formatter = DecimalFormat(DECIMAL_FORMAT, formatSymbols)
    return formatter.format(value)
}



fun roundOffTo2DecPlacesSecond(value: Float): String? {
    val DECIMAL_FORMAT = "###,###.###"
    val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
    formatSymbols.decimalSeparator = ','
    formatSymbols.groupingSeparator = ' '
    val formatter = DecimalFormat(DECIMAL_FORMAT, formatSymbols)
    return formatter.format(value)
}


fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}

 fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("dd.MM.yyyy")
    return format.format(date)
}

fun convertLongToFilterTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return format.format(date)
}

fun maskEmail(email: String): String {
    return email.replace(Regex("""((?:\.|^).)?.(?=.*@)"""), "$1*")
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

