package com.pls.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName

object Util {
    fun getComponentName(org: String): String {
        val orgList = org.split(".")
        val name = orgList[orgList.size - 2]
        return name
    }

    fun getInstallInTypeName(annotationString: String): TypeName {
        val prefix = "installIn="
        val suffix = ".class"

        val startIndex = annotationString.indexOf(prefix)
        val endIndex = annotationString.indexOf(suffix)
        val defaultClassTypeName = ClassName.get("dagger.hilt.components", "SingletonComponent")

        if (startIndex == -1 || endIndex == -1) {
            return defaultClassTypeName
        }

        val classNameWithPackage =
            annotationString.substring(startIndex + prefix.length, endIndex).trim()
        val lastDotIndex = classNameWithPackage.lastIndexOf('.')

        if (lastDotIndex == -1) {
            return defaultClassTypeName
        }

        val packageName = classNameWithPackage.substring(0, lastDotIndex)
        val className = classNameWithPackage.substring(lastDotIndex + 1)

        return ClassName.get(packageName, className)
    }
}