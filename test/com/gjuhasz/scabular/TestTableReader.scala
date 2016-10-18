package com.gjuhasz.scabular

import org.junit.Test
import org.junit.Assert
import com.gjuhasz.scabular.example.PersonBuilder
import com.gjuhasz.scabular.example.Person

class TestTableReader {
  @Test
  def testFoo(): Unit = {
    val row = Seq(cell.Text("Alice"), cell.Num(24), cell.Bool(false))

    val reader = TableReader(())
      .col(0, Text.AsString)(prev => value => value)
      .col(1, Num.AsInt)(prev => value => (prev, value))
      .col(2, Bool.AsBoolean)(prev => value => (prev, value))

    val actual = reader.read(row)

    Assert.assertEquals((("Alice", 24), false), actual)
  }

  @Test
  def testSimple(): Unit = {
    import DefaultConverters._

    val row = Seq(cell.Text("Alice"), cell.Num(24), cell.Bool(false))

    val reader = TableReader(PersonBuilder)
      .col(0)(_.name)
      .col(1)(_.age)
      .col(2)(_.isStudent)

    val actual = reader.read(row)

    Assert.assertEquals(Person("Alice", 24, false), actual)
  }
}