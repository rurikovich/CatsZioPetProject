package ru.rurik.domain.expence.tree


trait Tree[A] {

  def leafs(): Option[List[Tree[A]]]

  def value(): A

}