package com.gjuhasz.scabular

trait TableReader[TargetT] {
  def col[CellValueT, NewTargetT](
    columnNumber: Int,
    converter: CellConverter[CellValueT])(
      convertCellValue: TargetT => CellValueT => NewTargetT): TableReader[NewTargetT] = {

    val toTarget: TargetT => Cell => NewTargetT =
      (tgt: TargetT) => (c: Cell) => convertCellValue(tgt)(converter.convert(c))

    TableReaderImpl(this, columnNumber, toTarget)
  }

  def col[CellValueT, NewTargetT](
    columnNumber: Int)(
      convertCellValue: TargetT => CellValueT => NewTargetT)(
        implicit converter: CellConverter[CellValueT]) = {
    
    val toTarget: TargetT => Cell => NewTargetT =
      (tgt: TargetT) => (c: Cell) => convertCellValue(tgt)(converter.convert(c))

    TableReaderImpl(this, columnNumber, toTarget)
  }

  def read(row: Seq[Cell]): TargetT

}

object TableReader {
  def apply[TargetT](init: TargetT) = EmptyTableReader(init)
}

case class EmptyTableReader[TargetT](init: TargetT) extends TableReader[TargetT] {
  override def read(row: Seq[Cell]): TargetT = init
}

case class TableReaderImpl[PrevTargetT, CellValueT, TargetT](
  prevTableReader: TableReader[PrevTargetT],
  columnNumber: Int,
  convertCellValue: PrevTargetT => Cell => TargetT) extends TableReader[TargetT] {

  override def read(row: Seq[Cell]): TargetT = {
    val prev = prevTableReader.read(row)
    convertCellValue(prev)(row(columnNumber))
  }
}