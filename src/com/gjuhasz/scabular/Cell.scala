package com.gjuhasz.scabular

import java.time.LocalDate

sealed trait Cell{
  /**
   * Human readable cell type.
   * This can be used in an error message for example in case of unexpected cell type/
   */
  def cellType:String
}
trait NumericCell extends Cell{
  def asInt: Int
  def asDouble: Double
  def asDate: LocalDate
}
trait TextCell extends Cell{
  def asString: String
}

trait BooleanCell extends Cell{
  def asBoolean: Boolean
}

trait BlankCell extends Cell