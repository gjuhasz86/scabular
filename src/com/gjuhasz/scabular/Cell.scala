package com.gjuhasz.scabular

import java.time.LocalDate

import org.apache.poi.ss.usermodel.{Cell => PCell}

sealed trait Cell
case class Num(c: PCell) extends Cell {
  def asDouble: Double = c.getNumericCellValue
  def asInt: Int = c.getNumericCellValue.toInt
  def asDate: LocalDate = LocalDate.from(c.getDateCellValue.toInstant)
}
case class Text(c: PCell) extends Cell {
  def asString: String = c.getStringCellValue
}
case class Bool(c: PCell) extends Cell {
  def asBoolean: Boolean = c.getBooleanCellValue
}
case class Blank(c: PCell) extends Cell
case class Error(c: PCell) extends Cell
case class Formula(c: PCell) extends Cell

object Cell {
  def of(cell: PCell): Cell = {
    cell.getCellType match {
      case PCell.CELL_TYPE_NUMERIC => Num(cell)
      case PCell.CELL_TYPE_STRING => Text(cell)
      case PCell.CELL_TYPE_BOOLEAN => Bool(cell)
      case PCell.CELL_TYPE_BLANK => Blank(cell)
      case PCell.CELL_TYPE_ERROR => Error(cell)
      case PCell.CELL_TYPE_FORMULA => Formula(cell)
      case other => throw new IllegalArgumentException(s"Unexpected cell type [$other]")
    }
  }
}