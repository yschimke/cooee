package com.baulsupp.cooee.servlet

import com.baulsupp.cooee.users.JwtUserAuthenticator.Companion.parseHeader
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AdminFilter : Filter {
  override fun init(config: FilterConfig) {
  }

  override fun destroy() {
  }

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    if (request is HttpServletRequest && response is HttpServletResponse) {
      if (!isAdmin(request)) {
        response.sendError(401)
        return
      }
    }

    chain.doFilter(request, response);
  }

  private fun isAdmin(request: HttpServletRequest): Boolean {
    // Change to cookie
    val token = parseHeader(request.getHeader("Authorization"))

    return token?.email?.endsWith("@coo.ee") ?: false
  }
}
