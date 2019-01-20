package com.baulsupp.cooee.providers.trello

data class Badges(
  val comments: Int,
  val attachments: Int,
  val attachmentsByType: Map<String, Map<String, Int>>?,
  val dueComplete: Boolean?,
  val description: Boolean?,
  val subscribed: Boolean?,
  val due: String?,
  val viewingMemberVoted: Boolean?,
  val location: Boolean?,
  val votes: Int?,
  val fogbugz: String?,
  val checkItems: Int?,
  val checkItemsChecked: Int?
)

data class Card(
  val descData: Any?,
  val shortUrl: String,
  val dueComplete: Boolean?,
  val dateLastActivity: String?,
  val idList: String?,
  val shortLink: String?,
  val subscribed: Boolean?,
  val pos: Double?,
  val id: String,
  val idBoard: String,
  val checkItemStates: Any?,
  val url: String = "",
  val badges: Badges?,
  val idMembers: List<String>?,
  val idShort: Int,
  val due: String?,
  val idAttachmentCover: Any?,
  val name: String,
  val closed: Boolean,
  val manualCoverAttachment: Boolean?,
  val desc: String?
)

