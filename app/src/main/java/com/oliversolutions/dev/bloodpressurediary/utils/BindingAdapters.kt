package com.oliversolutions.dev.bloodpressurediary.utils

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.oliversolutions.dev.bloodpressurediary.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("SetTextI18n", "SimpleDateFormat")
@BindingAdapter(value= ["fromDate", "toDate"], requireAll = false)
fun bindFromDateToDate(textView: TextView, fromDate: String, toDate: String) {
    if (fromDate.isEmpty() || toDate.isEmpty()) {
        textView.text = textView.context.getString(R.string.all_records)
    } else if (fromDate.isNotEmpty() && toDate.isNotEmpty()){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd-MMMM")
            val formattedFromDate = LocalDate.parse(fromDate).format(formatter)
            val formattedToDate = LocalDate.parse(toDate).format(formatter)
            textView.text = "$formattedFromDate - $formattedToDate"
        } else {
            val formattedFromDate = SimpleDateFormat("dd-MMMM").format(Date(SimpleDateFormat("yyyy-MM-dd").parse(fromDate).time))
            val formattedToDate = SimpleDateFormat("dd-MMMM").format(Date(SimpleDateFormat("yyyy-MM-dd").parse(toDate).time))
            textView.text = "$formattedFromDate - $formattedToDate"
        }
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("numRecords")
fun bindNumRecords(textView: TextView, numRecords: String) {
    textView.text = numRecords + " " + textView.context.getString(R.string.records)
}

@BindingAdapter("selectedDate")
fun bindSelectedDate(textView: TextView, selectedDate: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("dd-MMMM E")
        val formattedSelectedDate = LocalDate.parse(selectedDate).format(formatter)
        textView.text = formattedSelectedDate

    } else {
        val formattedSelectedDate = SimpleDateFormat("dd-MMMM E", Locale.getDefault()).format(Date(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate).time))
        textView.text = formattedSelectedDate
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter(value= ["averageSystolic", "averageDiastolic"], requireAll = false)
fun bindAverageSystolicAndDiastolic(textView: TextView, averageSystolic: String, averageDiastolic: String) {
    val df = DecimalFormat("#")
    df.roundingMode = RoundingMode.CEILING
    textView.text = df.format(averageSystolic.toDouble()) + "/" + df.format(averageDiastolic.toDouble()) + " mmHg"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("averagePulse")
fun bindAveragePulse(textView: TextView, averagePulse: String) {
    val df = DecimalFormat("#")
    df.roundingMode = RoundingMode.CEILING
    textView.text = df.format(averagePulse.toDouble()) + " bpm"
}

@SuppressLint("SetTextI18n")
@BindingAdapter(value= ["systolic", "diastolic"], requireAll = false)
fun bindSystolicAndDiastolic(textView: TextView, systolic: String, diastolic: String) {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING

    textView.text = df.format(systolic.toDouble()) + " / " + df.format(diastolic.toDouble()) + " mmHg"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("pulse")
fun bindPulse(textView: TextView, pulse: String) {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING

    textView.text = df.format(pulse.toDouble()) + " bpm"
}

@BindingAdapter(value= ["imgDiastolic", "imgSystolic"], requireAll = false)
fun bindImageDiastolicSystolic(imageView: ImageView, imgDiastolic: String, imgSystolic: String) {
    when (getBloodPressureDiagnosis(imgSystolic.toDouble().toInt(), imgDiastolic.toDouble().toInt())) {
        BloodPressureDiagnosis.Normal -> {
            imageView.setImageResource(R.mipmap.ic_smiley_happy_foreground)
        }
        BloodPressureDiagnosis.Pre -> {
            imageView.setImageResource(R.mipmap.ic_smiley_semihappy_foreground)
        }
        BloodPressureDiagnosis.Stage1,
        BloodPressureDiagnosis.Stage2,
        BloodPressureDiagnosis.Hypotension -> {
            imageView.setImageResource(R.mipmap.ic_smiley_angry_foreground)

        }
    }
}

/**
 * Use this binding adapter to show and hide the views using boolean variables
 */
@BindingAdapter("android:fadeVisible")
fun setFadeVisible(view: View, visible: Boolean? = true) {
    if (view.tag == null) {
        view.tag = true
        view.visibility = if (visible == true) View.VISIBLE else View.GONE
    } else {
        view.animate().cancel()
        if (visible == true) {
            if (view.visibility == View.GONE)
                view.fadeIn()
        } else {
            if (view.visibility == View.VISIBLE)
                view.fadeOut()
        }
    }
}

