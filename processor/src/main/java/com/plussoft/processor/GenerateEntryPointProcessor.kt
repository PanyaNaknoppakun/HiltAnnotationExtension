package com.plussoft.processor

import com.google.auto.service.AutoService
import com.plussoft.annotation.GenerateBindHiltModule
import com.plussoft.annotation.GenerateEntryPointHiltModule
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes("com.plussoft.annotation.GenerateEntryPointHiltModule")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class GenerateEntryPointProcessor : AbstractProcessor() {
    private val generateEntryPoint = GenerateEntryPointHiltModule::class

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val funcList = roundEnv.getElementsAnnotatedWith(generateEntryPoint.java)
            .filterIsInstance<TypeElement>()
            .map { typeElement: TypeElement ->
                generateEntryPointHiltModule(typeElement)
            }

        if (funcList.isNotEmpty()) {
            createClass(funcList)
        }

        return true
    }

    private fun generateEntryPointHiltModule(element: TypeElement): MethodSpec {
        val targetClassName = element.asType().toString()
        val targetClass = ClassName.bestGuess(targetClassName)
        return MethodSpec.methodBuilder("inject")
            .addParameter(targetClass, "className")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .build()
    }

    private fun createClass(funcList: List<MethodSpec>) {
        val packageName = "com.plussoft.testannotation"
        val className = "HiltABCEntryPoint"
        val moduleName = ClassName.get(packageName, className)

        val classSpec = TypeSpec.interfaceBuilder(moduleName)
            .addAnnotation(AnnotationSpec.builder(ClassName.get("dagger.hilt", "EntryPoint")).build())
            .addAnnotation(
                AnnotationSpec.builder(ClassName.get("dagger.hilt", "InstallIn"))
                    .addMember(
                        "value",
                        "\$T.class",
                        ClassName.get("dagger.hilt.components", "SingletonComponent")
                    )
                    .build()
            )
            .addMethods(funcList)
            .build()

        val javaFile = JavaFile.builder(packageName, classSpec)
            .build()

        try {
            javaFile.writeTo(processingEnv.filer)
            processingEnv.messager.printMessage(
                Diagnostic.Kind.NOTE,
                "Generated file: ${packageName}.${className}"
            )
        } catch (e: IOException) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to create file: ${e.message}"
            )
        }
    }
//    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
//        val funcList = roundEnv.getElementsAnnotatedWith(generateEntryPoint.java)
//            .filterIsInstance<TypeElement>()
//            .map { typeElement: TypeElement ->
//                generateEntryPointHiltModule(typeElement)
//            }
//
//        if (funcList.isNotEmpty()) {
//            createClass(funcList)
//        }
//
//        return true
//    }
//
//
//    private fun generateEntryPointHiltModule(element: TypeElement) : FunSpec {
//        val targetClassName = element.asType().toString()
//        val targetClass = ClassName.bestGuess(targetClassName)
//        return FunSpec.builder("inject")
//            .addParameter("className", targetClass)
//            .clearBody()
//            .build()
//    }
//
//    private fun createClass(funcList: List<FunSpec>) {
//        val packageName = "com.plussoft.testannotation"
//        val className = "HiltABCEntryPoint"
//        val moduleName = ClassName(packageName, className)
//
//        val classSpec = TypeSpec.interfaceBuilder(moduleName)
//            .addAnnotation(AnnotationSpec.builder(ClassName("dagger.hilt", "EntryPoint")).build())
//            .addAnnotation(
//                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
//                    .addMember("%T::class", ClassName("dagger.hilt.components", "SingletonComponent"))
//                    .build()
//            )
//            .addFunctions(funcList)
//            .build()
//
//        val kotlinFile = FileSpec.builder(packageName, className)
//            .addType(classSpec)
//            .build()
//
//        try {
//            val fileObject = processingEnv.filer.createResource(
//                javax.tools.StandardLocation.SOURCE_OUTPUT, packageName, "$className.kt"
//            )
//            fileObject.openWriter().use { writer ->
//                kotlinFile.writeTo(writer)
//            }
//
//        } catch (e: Exception) {
//            processingEnv.messager.printMessage(
//                Diagnostic.Kind.ERROR,
//                "Failed to create file: ${e.message}"
//            )
//        }
//    }
}
