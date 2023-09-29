package com.github.asodja.task.api

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyGetter

class KspGradleAnnotationProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val getters = mutableMapOf<KSDeclaration, MutableList<PropertyGetter>>()
        val annotatedClasses = resolver.getSymbolsWithAnnotation("org.gradle.api.tasks.Input")
        for (annotated in annotatedClasses) {
            if (annotated is KSFunctionDeclaration) {
                annotated.parentDeclaration?.let {
                    val typeGetters = getters.computeIfAbsent(it) { mutableListOf() }
                    typeGetters.add(PropertyGetter(annotated.simpleName.asString(), "String", "return null;"))
                    environment.logger.info("Found function with MyAnnotation: ${annotated.qualifiedName?.asString()}")
                }
            } else if (annotated is KSPropertyGetter) {
                annotated.receiver.parentDeclaration?.let {
                    val typeGetters = getters.computeIfAbsent(it) { mutableListOf() }
                    typeGetters.add(PropertyGetter("get" + annotated.receiver.simpleName.asString().capitalize(), "String", "return null;"))
                    environment.logger.info("Found function with MyAnnotation: ${annotated.receiver.qualifiedName?.asString()}")
                }
                environment.logger.info("Found annotated property class: ${annotated.receiver.qualifiedName?.asString()}")
            }
        }
        getters.forEach {
            generateSourceFile(it.key, it.value)
        }
        return emptyList()
    }

    private fun generateSourceFile(typeElement: KSDeclaration, getters: List<PropertyGetter>) {
        val packageName: String = typeElement.packageName.asString()
        val className = typeElement.simpleName.asString() + "GeneratedViaKsp"
        environment.logger.info("Generating source file: $packageName.$className, getters: $getters")
        // Write the source code to a new file
        environment.codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false, typeElement.containingFile!!),
            packageName = packageName,
            fileName = className,
            extensionName = "java"
        ).writer().use {
            val gettersAsString = generateGetters(getters)
            it.write(
                """
                |package $packageName;
                |public class $className {
                |    $gettersAsString
                |}
                """.trimMargin()
            )
        }
    }

    private fun generateGetters(getters: List<PropertyGetter>): String {
        val out = StringBuilder()
        for (getter in getters) {
            out.append(
                """
                    |
                    |   public ${getter.returnType} ${getter.name}() {
                    |       ${getter.body}
                    |   }
                """.trimMargin()
            )
        }
        return out.toString()
    }

    override fun finish() {}

    private data class PropertyGetter(val name: String, val returnType: String, val body: String)
}