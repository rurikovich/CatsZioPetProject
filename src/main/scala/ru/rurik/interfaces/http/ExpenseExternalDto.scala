package ru.rurik.interfaces.http

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class ExpenseExternalDto(name: String, category: String, amount: Long, parentId: Option[Long] = None)

object ExpenseExternalDto {
  implicit val decoder: Decoder[ExpenseExternalDto] = deriveDecoder
}
