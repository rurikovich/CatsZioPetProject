package ru.rurik.domain.expence.tree

import ru.rurik.domain.expence.Expense

case class ExpenseTree(value: Expense, leafs: Option[List[Tree[Expense]]] = None) extends Tree[Expense]