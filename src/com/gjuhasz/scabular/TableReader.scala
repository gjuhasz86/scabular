package com.gjuhasz.scabular

import java.io.ByteArrayInputStream
import scala.collection.JavaConversions.asScalaIterator
import scala.collection.JavaConversions.iterableAsScalaIterable
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{ Cell => PCell }
import org.apache.poi.ss.usermodel.Row
import com.gjuhasz.scabular.ColDef.ColumnSpec
import java.time.LocalDate
trait TableReaderBuilder[T1, T2] {
  def col[T1, T2](colDef: ColDef.Xls.Read.Def[T1, T2] with ColumnSpec): TableReaderBuilder[T1, T2]
  def build: TableReader[T1, T2]
}
//case class TrbMul[T0](tr: TableReader[T0]) extends TableReaderBuilder[T0] {
//  def col[T](colDef: ColDef.Xls.Read.Def[T] with ColumnSpec): TableReaderBuilder[T]
//}
object Trb extends TableReaderBuilder[Nothing, Nothing] {
  override def col[T1, T2](colDef: ColDef.Xls.Read.Def[T1, T2] with ColumnSpec): TableReaderBuilder[T1, T2] = ???
  override def build: TableReader[Nothing, Nothing] = ???
}

case class TableReader[T1, T2](colDef: ColDef.Xls.Read.Def[T1, T2] with ColumnSpec, init: T1) {
  val sheetNo = 0
  val firstDataRowNo = 1
  def readXls(in: Array[Byte]): Seq[T2] = {
    val input = new ByteArrayInputStream(in)
    val wb = new HSSFWorkbook(input)
    val sheet = wb.getSheetAt(sheetNo)

    val dataRows = sheet.drop(firstDataRowNo)
    val rows = dataRows.map(rowToIndexToCellMap).toList
    rows.map(rowToTarget)
  }

  def rowToTarget(row: Map[Int, PCell]): T2 = {
    val pCell: PCell = row(colDef.column)
    val cell = Cell.of(pCell)
    colDef.xlsRead(cell, init)
  }

  def rowToIndexToCellMap(row: Row): Map[Int, PCell] =
    row.cellIterator()
      .map(cell => cell.getColumnIndex -> cell)
      .toMap
}

trait RowDef[PrevT, T] {
  def read(row: Map[Int, Cell], init: PrevT): T
}
case class RowDef0[PrevT, T](colDef: ColDef.Xls.Read.Def[PrevT, T] with ColumnSpec) extends RowDef[PrevT, T] {

  override def read(row: Map[Int, Cell], init: PrevT): T = {
    val c = row(colDef.column)
    colDef.xlsRead(c, init)
  }
}

case class RowDef1[InitT, PrevT, T](colDef: ColDef.Xls.Read.Def[PrevT, T] with ColumnSpec, rowDef: RowDef[InitT, PrevT]) extends RowDef[InitT, T] {

  override def read(row: Map[Int, Cell], init: InitT): T = {
    val prev = rowDef.read(row, init)
    val c = row(colDef.column)
    colDef.xlsRead(c, prev)
  }
}

object ColDef {
  trait ColumnSpec {
    def column: Int
  }
  abstract class ColSpec(col: Int) extends ColumnSpec {
    final val column: Int = col
  }

  object Xls {
    object Read {

      trait Def[T1, T2] {
        def xlsRead(c: Cell, t1: T1): T2
      }

      trait Text[T1, T2] extends Def[T1, T2] {
        final def xlsRead(cell: Cell, t1: T1): T2 = cell match {
          case tc: TextCell => xlsReadText(tc, t1)
          case other => throw new IllegalArgumentException(s"Unexpected cell type [$other]")
        }
        def xlsReadText(tc: TextCell, t1: T1): T2
      }
      object Text {
        trait AsString extends Text[Unit, String] {
          override def xlsReadText(tc: TextCell, u: Unit): String = tc.asString
        }
      }

      trait Num[T1, T2] extends Def[T1, T2] {
        final def xlsRead(cell: Cell, t1: T1): T2 = cell match {
          case nc: NumCell => xlsReadNum(nc, t1)
          case other => throw new IllegalArgumentException(s"Unexpected cell type [$other]")
        }
        def xlsReadNum(nc: NumCell, t1: T1): T2
      }

      object Num {
        trait AsInt extends Num[Unit, Int] {
          override def xlsReadNum(nc: NumCell, u: Unit): Int = nc.asInt
        }
        trait AsDouble extends Num[Unit, Double] {
          override def xlsReadNum(nc: NumCell, u: Unit): Double = nc.asDouble
        }
        trait AsLocalDate extends Num[Unit, LocalDate] {
          override def xlsReadNum(nc: NumCell, u: Unit): LocalDate = nc.asDate
        }
      }

      trait NumOrBlank[T1, T2] extends Def[T1, Option[T2]] {
        final def xlsRead(cell: Cell, t1: T1): Option[T2] = cell match {
          case nc: NumCell => Some(xlsReadNum(nc, t1))
          case bc: BlankCell => None
          case other => throw new IllegalArgumentException(s"Unexpected cell type [$other]")
        }
        def xlsReadNum(nc: NumCell, t1: T1): T2
      }
      object NumOrBlank {
        trait AsIntOption extends NumOrBlank[Unit, Int] {
          override def xlsReadNum(nc: NumCell, u: Unit): Int = nc.asInt
        }
      }
    }
  }
}


