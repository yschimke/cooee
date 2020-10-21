package com.baulsupp.cooee.rsocket

import io.rsocket.RSocket
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.util.MimeType

class FakeRSocketRequester : RSocketRequester {
  override fun rsocket(): RSocket {
    TODO()
  }

  override fun dataMimeType(): MimeType {
    TODO()
  }

  override fun metadataMimeType(): MimeType {
    TODO()
  }

  override fun route(route: String, vararg routeVars: Any?): RSocketRequester.RequestSpec {
    TODO()
  }

  override fun metadata(metadata: Any, mimeType: MimeType?): RSocketRequester.RequestSpec {
    TODO()
  }
}