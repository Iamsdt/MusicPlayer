package com.example.musicplayer.ext

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.musicplayer.R


fun Activity?.addFragment(
    @IdRes id: Int = R.id.container,
    fragment: Fragment,
    tag: String? = null,
    addToBackStack: Boolean = true,
    extras: Bundle? = null
) {
    val compatActivity = this as? AppCompatActivity ?: return
    if (extras != null) fragment.arguments = extras
    compatActivity.supportFragmentManager.beginTransaction()
        .apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            add(id, fragment, tag)
            if (addToBackStack) addToBackStack(null)
            commit()
        }
}

fun Activity?.replaceFragment(
    @IdRes id: Int = R.id.container,
    fragment: Fragment,
    tag: String? = null,
    addToBackStack: Boolean = false
) {
    val compatActivity = this as? AppCompatActivity ?: return
    compatActivity.supportFragmentManager.beginTransaction()
        .apply {
            replace(id, fragment, tag)
            if (addToBackStack) {
                addToBackStack(null)
            }
            commit()
        }
}

fun Activity?.getColorByTheme(
    @AttrRes id: Int,
    name: String
): Int {
    val colorAttr: Int? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        id
    } else {
        this?.resources!!.getIdentifier(name, "attr", packageName)
    }
    val outValue = TypedValue()
    this?.theme!!.resolveAttribute(colorAttr!!, outValue, true)
    return outValue.data
}

fun Activity?.toast(
    msg: String,
    dur: Int
) {
    Toast.makeText(this, msg, dur).show()
}