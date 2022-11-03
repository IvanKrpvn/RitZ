package com.example.viewpagerexperiment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

fun showErrorPopup(context:Context){
  val myDialog=Dialog(context)
    val btnDetermined: Button
    val btnNotDetermined: Button
    myDialog.setContentView(R.layout.error_popup_menu)
    btnDetermined= myDialog.findViewById<View>(R.id.ok_btn) as Button
    btnNotDetermined = myDialog.findViewById<View>(R.id.ok_btn_not_determined) as Button

    btnNotDetermined.setOnClickListener {
        myDialog.dismiss()

    }
    btnDetermined.setOnClickListener {
        myDialog.dismiss()

    }

    myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    myDialog.show()

}