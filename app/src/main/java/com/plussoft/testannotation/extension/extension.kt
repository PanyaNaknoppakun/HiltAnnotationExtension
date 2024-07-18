package com.plussoft.testannotation.extension

import android.content.Context
import dagger.hilt.android.EntryPointAccessors

fun <T> Context.getEntryPoint(c: Class<T>): T {
    return EntryPointAccessors.fromApplication(this, c)
}