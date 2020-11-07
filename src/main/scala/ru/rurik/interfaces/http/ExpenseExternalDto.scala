package ru.rurik.interfaces.http

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import ru.rurik.domain.expence.ExpenseCategory.ExpenseCategory

case class ExpenseExternalDto(name: String, category: ExpenseCategory, amount: Long, parentId: Option[Long] = None)

object ExpenseExternalDto {

  import ru.rurik.domain.expence.JsonProtocol._

  implicit val decoder: Decoder[ExpenseExternalDto] = deriveDecoder


}
