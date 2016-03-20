package com.gjuhasz.scabular.excel

import com.gjuhasz.scabular.CellConverter
import com.gjuhasz.scabular.Cell


trait ColDef[TargetT] {
  def col[CellValueT, NewTargetT](
    columnNumber: Int,
    converter: CellConverter[CellValueT])(
      convertCellValue: TargetT => CellValueT => NewTargetT): ColDef[NewTargetT] = {

    val toTarget: TargetT => Cell => NewTargetT =
      (tgt: TargetT) => (c: Cell) => convertCellValue(tgt)(converter.convert(c))

    ColDefImpl(this, columnNumber, toTarget)
  }

  def read(row: Seq[Cell]): TargetT

}

object ColDef {
  def apply[TargetT](init: TargetT) = EmptyColDef(init)
}

case class EmptyColDef[TargetT](init: TargetT) extends ColDef[TargetT] {
  override def read(row: Seq[Cell]): TargetT = init
}

case class ColDefImpl[PrevTargetT, CellValueT, TargetT](
  prevColDef: ColDef[PrevTargetT],
  columnNumber: Int,
  convertCellValue: PrevTargetT => Cell => TargetT) extends ColDef[TargetT] {

  override def read(row: Seq[Cell]): TargetT = {
    val prev = prevColDef.read(row)
    convertCellValue(prev)(row(columnNumber))
  }
}