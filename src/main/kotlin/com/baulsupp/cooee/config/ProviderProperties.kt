package com.baulsupp.cooee.config;

import com.baulsupp.okurl.authenticator.authflow.AuthOption
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "provider")
data class ProviderProperties(
  var callback: String = "https://stream.coo.ee/callback",
  var secrets: Map<String, String> = mapOf()
) {
  fun readConfigList(
    it: AuthOption<*>
  ) = secrets[it.param.replace(".", "_")]?.split(",")

  fun readConfigValue(
    it: AuthOption<*>
  ) = secrets[it.param.replace(".", "_")] ?: ""
}