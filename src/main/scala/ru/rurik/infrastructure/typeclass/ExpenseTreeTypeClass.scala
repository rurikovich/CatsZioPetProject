package ru.rurik.infrastructure.typeclass

trait Tree[A] {

  def leafs(): List[Tree[A]]

  def value(): A

}

trait TreeBuilder[A] {

  def toTree(value: A): Tree[A]

}
