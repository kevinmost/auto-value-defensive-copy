@file:JvmName("DefensiveCopyExtensionUtil")

package com.kevinmost.auto.value.defensive_copy.processor

import com.google.auto.value.extension.AutoValueExtension
import com.google.auto.value.extension.AutoValueExtension.Context
import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopier
import com.kevinmost.auto.value.defensive_copy.adapter.DefensiveCopy
import com.kevinmost.auto.value.defensive_copy.processor.util.AutoValueProperty
import com.kevinmost.auto.value.defensive_copy.processor.util.addControlFlow
import com.kevinmost.auto.value.defensive_copy.processor.util.asTypeName
import com.kevinmost.auto.value.defensive_copy.processor.util.autoValueProperties
import com.kevinmost.auto.value.defensive_copy.processor.util.buildAnnotation
import com.kevinmost.auto.value.defensive_copy.processor.util.buildClassFile
import com.kevinmost.auto.value.defensive_copy.processor.util.buildField
import com.kevinmost.auto.value.defensive_copy.processor.util.buildMethod
import com.kevinmost.auto.value.defensive_copy.processor.util.codeBlock
import com.kevinmost.auto.value.defensive_copy.processor.util.generateSuperclassConstructor
import com.kevinmost.auto.value.defensive_copy.processor.util.plusAssign
import com.kevinmost.auto.value.defensive_copy.processor.util.rawType
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import org.jetbrains.annotations.NotNull
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.element.Modifier.ABSTRACT
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PROTECTED
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC

open class DefensiveCopyExtension : AutoValueExtension() {

  override fun applicable(context: Context): Boolean = context.autoValueProperties
      .any { it[DefensiveCopy::class] != null }

  override fun generateClass(
      context: Context,
      className: String,
      classToExtend: String,
      isFinal: Boolean
  ) = buildClassFile(
      packageName = context.packageName(),
      className = className,
      superClass = classToExtend,
      modifiers = setOf(if (isFinal) FINAL else ABSTRACT)
  ) {
    this += context.generateSuperclassConstructor

    context.autoValueProperties.asSequence()
        .filter { it[DefensiveCopy::class] != null }
        .map { DefensivelyCopiedProperty.from(context, it) }
        .forEach { it.addMembersToType(this) }

  }.toString()

}

sealed class DefensivelyCopiedProperty(protected val context: Context, protected val property: AutoValueProperty) {

  companion object {
    fun from(context: Context, property: AutoValueProperty): DefensivelyCopiedProperty {
      val copyAnnotation = property[DefensiveCopy::class]!!
      val copierName = copyAnnotation[DefensiveCopy::copier]
      return when (copierName) {
        DefensiveCopier::class.asTypeName -> DefaultDefensiveCopier(context, property)
        else -> CustomDefensiveCopier(context, property, copierName)
      }
    }
  }

  abstract fun addMembersToType(type: TypeSpec.Builder)

  class DefaultDefensiveCopier(context: Context, property: AutoValueProperty) :
      DefensivelyCopiedProperty(context, property) {

    override fun addMembersToType(type: TypeSpec.Builder) {
      val invokeSuper: String = "super.${property.name}()"
      type += buildPropertyGetter {
        val copyStatement = if (property.returnType is ArrayTypeName) {
          "java.util.Arrays.copyOf($invokeSuper, $invokeSuper.length)"
        } else {
          when (property.returnType.rawType) {
            List::class.asTypeName -> "java.util.Collections.unmodifiableList($invokeSuper)"
            NavigableSet::class.asTypeName -> "java.util.Collections.unmodifiableNavigableSet($invokeSuper)"
            SortedSet::class.asTypeName -> "java.util.Collections.unmodifiableSortedSet($invokeSuper)"
            Set::class.asTypeName -> "java.util.Collections.unmodifiableSet($invokeSuper)"
            NavigableMap::class.asTypeName -> "java.util.Collections.unmodifiableNavigableMap($invokeSuper)"
            SortedMap::class.asTypeName -> "java.util.Collections.unmodifiableSortedMap($invokeSuper)"
            Map::class.asTypeName -> "java.util.Collections.unmodifiableMap($invokeSuper)"
            Collection::class.asTypeName -> "java.util.Collections.unmodifiableCollection($invokeSuper)"
            Date::class.asTypeName -> "new Date($invokeSuper.getTime())"
            else -> error("There is no default implementation of ${DefensiveCopier::class} for ${property.returnType}. You must specify a custom implementation via the @${DefensiveCopy::class} annotation's \"copier\" attribute.")
          }
        }
        addCode("return $copyStatement;\n")
      }
    }
  }

  class CustomDefensiveCopier(context: Context, property: AutoValueProperty, private val copierName: TypeName) :
      DefensivelyCopiedProperty(context, property) {
    override fun addMembersToType(type: TypeSpec.Builder) {
      type += buildField(
          name = "${property.name}Copier",
          type = copierName,
          modifiers = listOf(PRIVATE, STATIC, FINAL),
          annotations = listOf(buildAnnotation(NotNull::class.asTypeName)),
          initializer = codeBlock { "new ${copierName.type()}()" }
      )
      type += buildPropertyGetter {
        this += codeBlock { "final ${property.returnType.type()} tmp = super.${property.name}();\n" }
        addControlFlow("if (tmp == null)") {
          this += codeBlock { "return null;\n" }
        }
        this += codeBlock { "return ${property.name}Copier.defensiveCopy(tmp);\n" }
      }
    }
  }

  protected fun buildPropertyGetter(bodyBlock: MethodSpec.Builder.() -> Unit): MethodSpec = buildMethod(
      annotations = property.annotations.map { buildAnnotation(it.annotationType) }
          + buildAnnotation(Override::class.asTypeName),
      modifiers = property.modifiers.visibilityModsOnly.toList(),
      name = property.name,
      returns = property.returnType,
      block = bodyBlock
  )
}

private val Collection<Modifier>.visibilityModsOnly: Collection<Modifier>
  get() = intersect(setOf(PUBLIC, PROTECTED, PRIVATE))

