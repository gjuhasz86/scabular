package com.gjuhasz.scabular

import java.io.ByteArrayInputStream

import scala.collection.JavaConversions.asScalaIterator
import scala.collection.JavaConversions.iterableAsScalaIterable

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{ Cell => PCell }
import org.apache.poi.ss.usermodel.Row

case class TableReader[T](colDef: XlsRdColDef[T]) {
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

trait ColDef[T] {
  def column: Int
  def xlsRead(c: Cell): T
  def xlsWrite(t: T): Cell
  def csvRead(s: String): T
  def csvWrite(t: T): String
}
abstract class ColSpec(col: Int) {
  final val column: Int = col
}
trait XlsRdColDef[T] {
  def column: Int
  def xlsRead(c: Cell): T
}

trait TextXlsRdColDef[T] extends XlsRdColDef[T] {
  final def xlsRead(cell: Cell): T = cell match {
    case tc: Text => xlsReadText(tc)
    case other => throw new IllegalArgumentException(s"Unexpected cell type [$other]")
  }
  def xlsReadText(tc: Text): T
}

trait NumXlsRdColDef[T] extends XlsRdColDef[T] {
  final def xlsRead(cell: Cell): T = cell match {
    case nc: Num => xlsReadNum(nc)
    case other => throw new IllegalArgumentException(s"Unexpected cell type [$other]")
  }
  def xlsReadNum(nc: Num): T
}

