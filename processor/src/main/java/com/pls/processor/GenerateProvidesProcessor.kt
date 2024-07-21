package com.pls.processor

import com.google.auto.service.AutoService
import com.pls.annotation.GenerateProvidesHiltModule
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
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
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes("com.pls.annotation.GenerateProvidesHiltModule")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class GenerateProvidesProcessor : AbstractProcessor() {
    private val generateProvides = GenerateProvidesHiltModule::class

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(generateProvides.java)
            .filterIsInstance<TypeElement>()
            .forEach { typeElement: TypeElement ->
                generateProvidesHiltModule(typeElement)
            }

        return true
    }

    private fun generateProvidesHiltModule(element: TypeElement) {
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
        val moduleName = ClassName.get(packageName, "${element.simpleName}_HiltModule")
        val providesMethods = generateProvidesMethods(element)

        val installInTypeName =
            Util.getInstallInTypeName(element.getAnnotation(generateProvides.java).toString())
        val classSpec = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(AnnotationSpec.builder(ClassName.get("dagger", "Module")).build())
            .addAnnotation(
                AnnotationSpec.builder(ClassName.get("dagger.hilt", "InstallIn"))
                    .addMember(
                        "value",
                        "\$T.class",
                        installInTypeName
                    )
                    .build()
            )
            .addMethods(providesMethods)
            .build()

        val javaFile = JavaFile.builder(packageName, classSpec).build()

        try {
            javaFile.writeTo(processingEnv.filer)

        } catch (e: IOException) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to create file: ${e.message}"
            )
        }
    }

    private fun generateProvidesMethods(element: TypeElement): List<MethodSpec> {
        val targetClassName = element.asType().toString()
        element.superclass.kind
        val targetClass = ClassName.bestGuess(targetClassName)

        val isObject = element.enclosedElements.any { it.simpleName.toString() == "INSTANCE" }
        val constructors = ElementFilter.constructorsIn(element.enclosedElements)
        val providesMethods = mutableListOf<MethodSpec>()

        constructors.forEach { constructor ->
            val methodBuilder = MethodSpec.methodBuilder("provide${element.simpleName}")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addAnnotation(AnnotationSpec.builder(ClassName.get("dagger", "Provides")).build())
                .returns(ClassName.get(element))

            constructor.parameters.forEach { param: VariableElement ->
                val paramClassName = TypeName.get(param.asType())
                methodBuilder.addParameter(paramClassName, param.simpleName.toString())
            }

            val params = constructor.parameters.joinToString(", ") { it.simpleName.toString() }
            methodBuilder.addStatement(
                "return \$L",
                if (isObject) "$targetClass.INSTANCE" else "new ${ClassName.get(element)}($params)"
            )

            providesMethods.add(methodBuilder.build())
        }

        return providesMethods
    }
}
