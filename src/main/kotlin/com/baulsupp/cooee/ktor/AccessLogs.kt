package com.baulsupp.cooee.ktor

import com.baulsupp.cooee.honeyClient
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.request.contentType
import io.ktor.request.host
import io.ktor.request.httpMethod
import io.ktor.request.httpVersion
import io.ktor.request.path
import io.ktor.request.userAgent
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase

class AccessLogs {
  class Configuration {
  }

  companion object Feature : ApplicationFeature<Application, AccessLogs.Configuration, AccessLogs> {
    override val key: AttributeKey<AccessLogs> = AttributeKey("Access Logs")

    override fun install(pipeline: Application, configure: AccessLogs.Configuration.() -> Unit): AccessLogs {
      val loggingPhase = PipelinePhase("Logging")
      val feature = AccessLogs()

      pipeline.insertPhaseBefore(ApplicationCallPipeline.Monitoring, loggingPhase)

      pipeline.intercept(loggingPhase) {
        proceed()
        feature.logSuccess(call)
      }

      return feature
    }
  }

  private fun logSuccess(call: ApplicationCall) {
    val dataMap = mapOf(
      "path" to call.request.path(),
      "method" to call.request.httpMethod.value,
      "result" to call.response.status()?.value,
      "useragent" to call.request.userAgent(),
      "httpversion" to call.request.httpVersion,
      "host" to call.request.host(),
      "request-contenttype" to call.request.contentType().toString()
    )

    if (honeyClient != null) {
      honeyClient!!.createEvent().addFields(dataMap).setDataset("accesslogs").send()
    }
  }
}
