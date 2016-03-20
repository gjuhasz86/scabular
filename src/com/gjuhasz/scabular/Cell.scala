package com.gjuhasz.scabular

import java.time.LocalDate

sealed trait Cell {
  /**
   * Human readable cell type.
   * This can be used in an error message for example in case of unexpected cell type/
   */
  def cellType: String
}

trait NumCell extends Cell {
  def cellType = "NUMERIC"
  def asInt: Int
  def asDouble: Double
  def asDate: LocalDate
}

trait TextCell extends Cell {
  def cellType = "TEXT"
  def asString: String
}

trait BoolCell extends Cell {
  def cellType = "BOOLEAN"
  def asBool: Boolean
}

trait BlankCell extends Cell {
  def cellType = "BLANK"
}

package cell {
  case class Num(value: Double) extends NumCell {
    override def asInt: Int = value.toInt
    override def asDouble: Double = value
    override def asDate: LocalDate = throw new UnsupportedOperationException("Cannot convert value to date")
  }

  case class Date(value: LocalDate) extends NumCell {
    override def asInt: Int = throw new UnsupportedOperationException("Cannot convert value to Int")
    override def asDouble: Double = throw new UnsupportedOperationException("Cannot convert value to Double")
    override def asDate: LocalDate = value
  }

  case class Text(value: String) extends TextCell {
    override def asString: String = value
  }

  case class Bool(value: Boolean) extends BoolCell {
    override def asBool: Boolean = value
  }

  case object Blank extends BlankCell
}