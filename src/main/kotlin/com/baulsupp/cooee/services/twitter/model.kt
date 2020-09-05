package com.baulsupp.cooee.services.twitter

data class Friend(val id_str: String, val screen_name: String, val name: String)

data class FriendsList(
    val users: List<Friend>,
    val next_cursor_str: String
)

data class Friends(val users: List<Friend>)

data class Target(val recipient_id: String)
data class MessageData(val text: String)
data class MessageCreate(val target: Target, val message_data: MessageData)
data class DmEvent(val type: String = "message_create", val message_create: MessageCreate)
data class DmRequest(val event: DmEvent) {
  companion object {
    fun simple(target: String, message: String) = DmRequest(DmEvent(message_create = MessageCreate(target = Target(target), message_data = MessageData(text = message))))
  }
}
