package ru.rurik.domain.expence

case class Expense(id: Long, name: String, amount: Long, parentId: Option[Long] = None)



