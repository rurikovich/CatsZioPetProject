package ru.rurik.domain.expence

object ExpenseCategory extends Enumeration {
  type ExpenseCategory = Value
  val Food = Value("продукты")
  val Appliances = Value("бытовая техника")
  val Services = Value("услуги")
  val Other = Value("прочее")

}

import ExpenseCategory._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Expense(id: Long, name: String, category: ExpenseCategory, amount: Long, parentId: Option[Long] = None)

object Expense {

  import ExpenseCategoryJsonCodecs._

  implicit val decoder: Decoder[Expense] = deriveDecoder

  implicit val encoder: Encoder[Expense] = deriveEncoder

}


object ExpenseCategoryJsonCodecs {
  implicit val genderDecoder: Decoder[ExpenseCategory.Value] = Decoder.decodeEnumeration(ExpenseCategory)
  implicit val genderEncoder: Encoder[ExpenseCategory.Value] = Encoder.encodeEnumeration(ExpenseCategory)
}

