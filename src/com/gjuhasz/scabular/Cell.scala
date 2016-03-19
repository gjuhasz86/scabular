package com.gjuhasz.scabular

import java.time.LocalDate

import org.apache.poi.ss.{usermodel => poi}

sealed trait Cell
case class NumCell(c: poi.Cell) extends Cell {
  def asDouble: Double = c.getNumericCellValue
  def asInt: Int = c.getNumericCellValue.toInt
  def asDate: LocalDate = LocalDate.from(c.getDateCellValue.toInstant)
}
case class TextCell(c: poi.Cell) extends Cell {
  def asString: String = c.getStringCellValue
}
case class BoolCell(c: poi.Cell) extends Cell {
  def asBoolean: Boolean = c.getBooleanCellValue
}
case class BlankCell(c: poi.Cell) extends Cell
case class ErrorCell(c: poi.Cell) extends Cell
case class FormulaCell(c: poi.Cell) extends Cell

object Cell {
  def of(cell: poi.Cell): Cell = {
    cell.getCellType match {
      case poi.Cell.CELL_TYPE_NUMERIC => NumCell(cell)
      case poi.Cell.CELL_TYPE_STRING => TextCell(cell)
      case poi.Cell.CELL_TYPE_BOOLEAN => BoolCell(cell)
      case poi.Cell.CELL_TYPE_BLANK => BlankCell(cell)
      case poi.Cell.CELL_TYPE_ERROR => ErrorCell(cell)
      case poi.Cell.CELL_TYPE_FORMULA => FormulaCell(cell)
      case other => throw new IllegalArgumentException(s"Unexpected cell type [$other]")
    }
  }
}