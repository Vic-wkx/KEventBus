package com.library.keventbus

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement


class KEventBusProcessor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Subscribe::class.java.canonicalName)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, env: RoundEnvironment?): Boolean {
        annotations?.forEach { annotation ->
            val generatedDir = processingEnv.options["kapt.kotlin.generated"]
            val filePath = "$generatedDir/com/example/eventbus"
            val file = File(filePath, "KEventBusIndex.kt")
            file.parentFile.mkdirs()
            val writer = BufferedWriter(FileWriter(file))
            writer.use {
                it.write(
                    """
                package com.example.eventbus
                
                import com.library.keventbus.SubscriberInfoIndex
                import com.library.keventbus.SubscriberMethodInfo
                import com.library.keventbus.ThreadMode
                
                class KEventBusIndex() : SubscriberInfoIndex {
                    override val methodsByClass = mutableMapOf<Class<*>, MutableList<SubscriberMethodInfo>>()
                    
                    init {
                
            """.trimIndent()
                )
                env?.getElementsAnnotatedWith(annotation)?.forEach { element ->
                    val clazz = element.enclosingElement
                    val method = element as ExecutableElement
                    val subscribe = method.getAnnotation(Subscribe::class.java)
                    val obj = "$clazz::class.javaObjectType"
                    val methodName = method.simpleName
                    val eventType = "${method.parameters.first().asType()}::class.javaObjectType"
                    val threadMode = "ThreadMode.${subscribe.threadMode}"
                    val sticky = subscribe.sticky
                    it.write("\t\tif (methodsByClass[$obj] == null) methodsByClass[$obj] = mutableListOf()\n")
                    it.write("\t\tmethodsByClass[$obj]!!.add(SubscriberMethodInfo(\"$methodName\", $eventType, $threadMode, $sticky))\n")
                }
                it.write(
                    """
                    }
                }
                """.trimIndent()
                )
            }
        }
        return false
    }
}