package com.kevinmost.auto.value.defensive_copy.processor.util

import com.google.auto.value.extension.AutoValueExtension.Context
import com.squareup.javapoet.MethodSpec

val Context.generateSuperclassConstructor: MethodSpec
  get() = buildConstructor(
      params = autoValueProperties.map {
        buildParam(
            name = it.name,
            type = it.returnType,
            annotations = it.annotations
                .filter { it.annotationType.simpleName() in listOf("NotNull", "NonNull", "Nullable") }
                .map { buildAnnotation(it.annotationType) }
        )
      }
  ) { addCode(autoValueProperties.map { it.name }.joinToString(prefix = "super(", postfix = ");\n")) }
