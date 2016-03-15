package com.gjuhasz.scabular

import org.apache.poi.ss.usermodel.{ Cell => PCell }
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.ByteArrayInputStream
import scala.collection.JavaConversions._
import org.apache.poi.ss.usermodel.Row
import java.time.LocalDate

object Domain {

}

case class TableReader[T](colDef: ColDef[T]) {
  val sheetNo = 0
  val firstDataRowNo = 1
  def readXls(in: Array[Byte]): Seq[T] = {
    val input = new ByteArrayInputStream(in)
    val wb = new HSSFWorkbook(input)
    val sheet = wb.getSheetAt(sheetNo)

    val dataRows = sheet.drop(firstDataRowNo)
    val rows = dataRows.map(rowToIndexToCellMap).toList
    rows.map(rowToTarget)
  }

  def rowToTarget(row: Map[Int, PCell]): T = {
    val pCell: PCell = row(colDef.column)
    val cell = Cell.of(pCell)
    colDef.xlsRead(cell)
  }

  def rowToIndexToCellMap(row: Row): Map[Int, PCell] =
    row.cellIterator()
      .map(cell => cell.getColumnIndex -> cell)
      .toMap
}

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

trait ColDef[T] {
  def column: Int
  def xlsRead(c: Cell): T
  def xlsWrite(t: T): Cell
  def csvRead(s: String): T
  def csvWrite(t: T): String
}

