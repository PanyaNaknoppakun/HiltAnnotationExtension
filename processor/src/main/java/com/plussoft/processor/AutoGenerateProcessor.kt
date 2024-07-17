package com.plussoft.processor


import com.google.auto.service.AutoService
import com.plussoft.annotation.GenerateBindHiltModule
import com.squareup.javapoet.*
import dagger.Module
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes("com.plussoft.annotation.GenerateBindHiltModule")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class AutoGenerateProcessor : AbstractProcessor() {
    private val generateMyClass = GenerateBindHiltModule::class

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(generateMyClass.java)
            .filterIsInstance<TypeElement>()
            .forEach { typeElement: TypeElement ->
                generateHiltModuleWithBindingJava(typeElement)
            }

        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "Finished processing.")
        return true
    }

    private fun generateHiltModuleWithBindingJava(element: TypeElement) {
        processingEnv.messager.printMessage(
            Diagnostic.Kind.NOTE,
            "Processing: ${element.simpleName}"
        )
        val interfaceType: TypeMirror = element.interfaces.firstOrNull() ?: element.superclass
        val typeName = TypeName.get(interfaceType)
        val interfaceName: Name = processingEnv.typeUtils.asElement(interfaceType).simpleName
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
        val className = "${interfaceName}HiltModule"
        val moduleName = ClassName.get(packageName, className)
        val targetClassName = element.asType().toString()
        val targetClass = ClassName.bestGuess(targetClassName)

        // Create the Hilt module class
        val classSpec = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Module::class.java)
            .addAnnotation(
                AnnotationSpec.builder(ClassName.get("dagger.hilt", "InstallIn"))
                    .addMember(
                        "value",
                        "\$T.class",
                        ClassName.get("dagger.hilt.components", "SingletonComponent")
                    )
                    .build()
            )

            .addMethod(
                MethodSpec.methodBuilder("bind${interfaceName}")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(ClassName.get("dagger", "Binds"))
                    .addParameter(targetClass, "impl")
                    .returns(typeName)
                    .build()
            )
            .addOriginatingElement(element)
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


//    private fun generateHiltModuleWithBindingKotlin(element: TypeElement) {
//        processingEnv.messager.printMessage(
//            Diagnostic.Kind.NOTE,
//            "Processing: ${element.simpleName}"
//        )
//
//        val interfaceType = element.interfaces.firstOrNull() ?: element.superclass
//        val typeName = interfaceType.asTypeName()
//        val interfaceName = processingEnv.typeUtils.asElement(interfaceType).simpleName.toString()
//        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
//        val className = "${interfaceName}HiltModule"
//        val moduleName = ClassName(packageName, className)
//        val targetClassName = element.asType().toString()
//        val targetClass = ClassName.bestGuess(targetClassName)
//
//        // Create the Hilt module class
//        val classSpec = TypeSpec.classBuilder(moduleName)
//            .addModifiers(KModifier.ABSTRACT)
//            .addAnnotation(Module::class)
//            .addAnnotation(
//                AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
//                    .addMember("%T::class",
//                        ClassName("dagger.hilt.components", "SingletonComponent")
//                    )
//                    .build()
//            )
//            .addFunction(FunSpec.builder("bind$interfaceName")
//                .addModifiers(KModifier.ABSTRACT)
//                .addAnnotation(ClassName("dagger", "Binds"))
//                .addParameter("impl", targetClass)
//                .returns(typeName)
//                .build())
//            .build()
//
//        val kotlinFile = FileSpec.builder(packageName, className)
//            .addType(classSpec)
//            .build()
//
//        try {
//            kotlinFile.writeTo(processingEnv.filer)
//            processingEnv.messager.printMessage(
//                Diagnostic.Kind.NOTE,
//                "Generated file: ${packageName}.${className}"
//            )
//        } catch (e: Exception) {
//            processingEnv.messager.printMessage(
//                Diagnostic.Kind.ERROR,
//                "Failed to create file: ${e.message}"
//            )
//        }
//    }

}
