package com.baulsupp.cooee.suggester

enum class SuggestionType {
  /** Returns a link to redirect (message is secondary to link) */
  LINK,

  /** Returns a command that can be executed via a POST, with preview etc */
  COMMAND,

  /** Returns a command prefix that allows further comment */
  PREFIX,

  /** Returns subcommands */
  LIST,

  /** Shows a preview or information (link is secondary to message) */
  INFO,

  UNKNOWN
}
