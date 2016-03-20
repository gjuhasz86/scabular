package com.gjuhasz.scabular

import org.junit.Test
import org.junit.Assert

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
}