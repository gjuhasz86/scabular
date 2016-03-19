package com.gjuhasz.proto

import org.junit.Test
import org.junit.Assert

class TestBuilder {

  @Test
  def testBuilder(): Unit = {
    val p: Person =
      PersonBuilder
        .name("Alice")
        .age(24)
    Assert.assertEquals(Person("Alice", 24), p)
  }

  @Test
  def testFoo(): Unit = {
    val row = Seq(TextCell("Alice"), NumCell(24))

    val fooReader = Foo(PersonBuilder)
      .foo(0, Text.AsString)(_.name)
      .foo(1, Num.AsInt)(_.age)

    val actual = fooReader.read(row)

    Assert.assertEquals(Person("Alice", 24), actual)
  }
}

sealed trait Cell
case class NumCell(value: Int) extends Cell
case class TextCell(value: String) extends Cell

sealed trait CellConverter[T] {
  def convert(cell: Cell): T
}
object Text {
  case object AsString extends CellConverter[String] {
    override def convert(cell: Cell): String = {
      cell match {
        case NumCell(v) => ??? // TODO
        case TextCell(v) => v
      }
    }
  }
}
object Num {
  case object AsInt extends CellConverter[Int] {
    override def convert(cell: Cell): Int = {
      cell match {
        case NumCell(v) => v
        case TextCell(v) => ??? // TODO
      }
    }
  }
}

trait Foo[T] {
  def foo[CT, T2](col: Int, c: CellConverter[CT])(f: T => CT => T2): Foo[T2]
  def read(row: Seq[Cell]): T
}
object Foo {
  def apply[T](t: T): Foo[T] = Init(t)
}

case class Init[T](init: T) extends Foo[T] {
  override def foo[CT, T2](_col: Int, _c: CellConverter[CT])(_f: T => CT => T2): Foo[T2] = Incr(this, _col, _c, _f)
  override def read(row: Seq[Cell]): T = init
}

case class Incr[PT, CT, T](prevFoo: Foo[PT], col: Int, c: CellConverter[CT], f: PT => CT => T) extends Foo[T] {
  override def foo[CT, T2](_col: Int, _c: CellConverter[CT])(_f: T => CT => T2): Foo[T2] = Incr(this, _col, _c, _f)
  override def read(row: Seq[Cell]): T = {
    val content = c.convert(row(col))
    val prev = prevFoo.read(row)
    f(prev)(content)
  }
}

case class Person(name: String, age: Int)

case object PersonBuilder {
  def name(name: String) = Pb1(name)
}

case class Pb1(name: String) {
  def age(age: Int) = Person(name, age)
}