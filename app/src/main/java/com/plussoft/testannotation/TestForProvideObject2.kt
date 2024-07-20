package com.plussoft.testannotation

import com.pls.annotation.GenerateProvidesHiltModule
import dagger.hilt.android.components.ActivityRetainedComponent

@GenerateProvidesHiltModule(ActivityRetainedComponent::class)
class TestForProvideObject2 {
    fun getDummyValue2(): String {
        return "Dummy2"
    }
}