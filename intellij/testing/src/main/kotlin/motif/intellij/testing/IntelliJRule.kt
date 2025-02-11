/*
 * Copyright (c) 2018-2019 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package motif.intellij.testing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.TestApplicationManager
import java.util.concurrent.CountDownLatch
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class IntelliJRule : TestRule {

  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {

      override fun evaluate() {
        TestApplicationManager.getInstance()
        var e: Throwable? = null
        val latch = CountDownLatch(1)
        ApplicationManager.getApplication().invokeLater {
          ApplicationManager.getApplication().runReadAction {
            try {
              base.evaluate()
            } catch (throwable: Throwable) {
              e = throwable
            } finally {
              latch.countDown()
            }
          }
        }
        latch.await()
        e?.let { throw it }
      }
    }
  }
}
