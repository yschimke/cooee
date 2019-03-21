package com.baulsupp.cooee.api

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.mongo.StringService
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.providers.CombinedProvider
import com.baulsupp.cooee.suggester.SuggestionList
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.authenticator.AuthInterceptor
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.bounceApi(
  goInfo: GoInfo,
  providers: CombinedProvider
) {
  val r =
    goInfo.command?.let { providers.go(it, *goInfo.args.toTypedArray()) } ?: Unmatched

  if (r == Unmatched) {
    call.respond(Completed(message = "no match"))
  } else {
    call.respond(r)
  }
}

suspend fun PipelineContext<Unit, ApplicationCall>.userApi(
  user: UserEntry?
) {
  if (user == null) {
    throw AuthenticationException()
  }

  call.respond(user)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.completionApi(
  user: UserEntry?,
  appServices: AppServices,
  commandQuery: CompletionRequest,
  providers: CombinedProvider
) {
  if (appServices.featureChecks(user).enabled("newsuggestions")) {
    val commands = providers.suggest(commandQuery.q ?: "")
    call.respond(SuggestionList(commands))
  } else {
    val results = commandCompletion(providers, commandQuery)

    call.respond(results)
  }
}

@KtorExperimentalLocationsAPI
private suspend fun commandCompletion(
  providers: CombinedProvider,
  command: CompletionRequest
): Completions {
  val commands = providers.suggest(command.q ?: "")
  return Completions.complete(command, commands)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.todoApi(
  user: UserEntry?,
  appServices: AppServices,
  todoQuery: TodoRequest,
  providers: CombinedProvider
) {
  val commands = providers.todo()
  call.respond(Todos(commands))
}

@KtorExperimentalLocationsAPI
private suspend fun todo(
  providers: CombinedProvider
): Todos {
  val commands = providers.todo()
  return Todos(commands)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.authorize(
  authorize: Authorize,
  user: UserEntry,
  appServices: AppServices
) {
  if (authorize.serviceName == null || authorize.token == null) {
    throw BadRequestException()
  }

  appServices.credentialsStore.set(StringService(authorize.serviceName), user.email, authorize.token)
  call.respond(HttpStatusCode.Created)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.searchSuggestion(
  it: SearchSuggestion,
  providers: CombinedProvider
) {
  val results = commandCompletion(providers, CompletionRequest(it.q ?: ""))

  val response =
    SearchSuggestionsResults(
      it.q ?: "",
      results.completions.map { it.line },
      results.completions.map { it.description },
      results.completions.map { "https://coo.ee/go?q=${it.line.replace(" ", "+")}" })

  call.respond(response)
}

data class ProviderList(val providers: List<ProviderStatus>)
data class ProviderStatus(
  val name: String,
  val installed: Boolean,
  val config: Map<String, Any>?,
  val services: List<String>
)

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.providersList(
  appServices: AppServices,
  providers: CombinedProvider,
  user: UserEntry
) {
  val list = appServices.providerRegistry.registeredForUser(user).map { (_, provider) ->
    providerStatus(providers, provider)
  }

  call.respond(ProviderList(list))
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.providerRequest(
  providerRequest: ProviderRequest,
  appServices: AppServices,
  providers: CombinedProvider,
  user: UserEntry
) {
  val provider = appServices.providerRegistry.registeredForUser(user)[providerRequest.name]

  if (provider == null) {
    call.respond(HttpStatusCode.NotFound)
  } else {
    call.respond(providerStatus(providers, provider))
  }
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.providerDeleteRequest(
  providerRequest: ProviderRequest,
  appServices: AppServices,
  user: UserEntry
) {
  appServices.providerConfigStore.remove(user.email, providerRequest.name)

  call.respond(HttpStatusCode.OK)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.providerConfigRequest(
  providerRequest: ProviderRequest,
  providerConfig: ProviderConfig,
  appServices: AppServices,
  user: UserEntry
) {
  appServices.providerConfigStore.store(user.email, providerRequest.name, providerConfig.config.orEmpty())

  call.respond(HttpStatusCode.OK)
}

private fun providerStatus(
  providers: CombinedProvider,
  provider: BaseProvider
): ProviderStatus {
  val userStatus = providers.providers.find { it.name == provider.name }

  val services = userStatus?.associatedServices() ?: provider.associatedServices()

  return ProviderStatus(provider.name, userStatus != null, userStatus?.config, services.sorted())
}

data class ServicesList(val services: List<ServiceStatus>)
data class ServiceStatus(
  val name: String,
  val installed: Boolean
)

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.servicesList(
  appServices: AppServices,
  user: UserEntry
) {
  val serviceNames =
    appServices.providerRegistry.registeredForUser(user).values.flatMap { it.associatedServices() }.toSet()

  val list = appServices.services.filter { serviceNames.contains(it.name()) }.map {
    serviceStatus(appServices, it, user)
  }

  call.respond(ServicesList(list))
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.serviceRequest(
  serviceRequest: ServiceRequest,
  appServices: AppServices,
  user: UserEntry
) {
  val service = appServices.services.find { it.name() == serviceRequest.name }

  if (service == null) {
    call.respond(HttpStatusCode.NotFound)
  } else {
    call.respond(serviceStatus(appServices, service, user))
  }
}

private suspend fun serviceStatus(
  appServices: AppServices,
  service: AuthInterceptor<*>,
  user: UserEntry
): ServiceStatus {
  val token = appServices.credentialsStore.get(service.serviceDefinition, user.email)

  return ServiceStatus(service.name(), token != null)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.serviceDeleteRequest(
  serviceRequest: ServiceRequest,
  appServices: AppServices,
  user: UserEntry
) {
  val service = appServices.services.find { it.name() == serviceRequest.name }

  if (service != null) {
    appServices.credentialsStore.remove(service.serviceDefinition, user.email)
  }

  call.respond(HttpStatusCode.OK)
}


data class FeaturesList(val features: List<FeatureStatus>)
data class FeatureStatus(
  val name: String,
  val enabled: Boolean
)

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.featuresRequest(
  appServices: AppServices,
  user: UserEntry?
) {
  val featureMap = appServices.featureChecks(user).all()

  call.respond(FeaturesList(featureMap.map { FeatureStatus(it.key, it.value) }))
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.featureRequest(
  appServices: AppServices,
  user: UserEntry?,
  request: FeatureRequest
) {
  val featureCheck = appServices.featureChecks(user).enabled(request.feature, false)

  call.respond(FeatureStatus(request.feature, featureCheck))
}
