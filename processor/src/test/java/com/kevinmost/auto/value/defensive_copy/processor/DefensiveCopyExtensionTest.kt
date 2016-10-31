package com.kevinmost.auto.value.defensive_copy.processor

import com.google.auto.value.processor.AutoValueProcessor
import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.junit.Test
import javax.tools.JavaFileObject

class DefensiveCopyExtensionTest {
  @Test fun `test methods generated as expected`() {
    listOf(
        "test/DefensiveList.java" to "test/AutoValue_DefensiveList.java",
        "test/DefensiveArray.java" to "test/AutoValue_DefensiveArray.java",
        "test/CustomAdapter.java" to "test/AutoValue_CustomAdapter.java",
        "test/CustomAdapterExternal.java" to "test/AutoValue_CustomAdapterExternal.java"
    ).forEach {
      sourceFile(it.first).assertGenerates(sourceFile(it.second))
    }
  }

  private fun JavaFileObject.assertGenerates(generated: JavaFileObject) {
    Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
        .that(listOf(this))
        .processedWith(AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generated)
  }

  private fun sourceFile(name: String): JavaFileObject {
    return JavaFileObjects.forResource(name)
  }
}

