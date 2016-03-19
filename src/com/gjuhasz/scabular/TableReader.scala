package com.gjuhasz.scabular

trait TableReader[TargetT] {
  def col[CellValueT, NewTargetT](
    columnNumber: Int,
    converter: CellConverter[CellValueT])(
      convertCellValue: TargetT => CellValueT => NewTargetT): TableReader[NewTargetT] =
    TableReaderImpl(this, columnNumber, converter, convertCellValue)

  def read(row: Seq[Cell]): TargetT

}

case class EmptyTableReader[TargetT](init: TargetT) extends TableReader[TargetT] {
  override def read(row: Seq[Cell]): TargetT = init
}

case class TableReaderImpl[PrevTargetT, CellValueT, TargetT](
  prevTableReader: TableReader[PrevTargetT],
  columnNumber: Int,
  converter: CellConverter[CellValueT],
  convertCellValue: PrevTargetT => CellValueT => TargetT) extends TableReader[TargetT] {

  override def read(row: Seq[Cell]): TargetT = {
    val content = converter.convert(row(columnNumber))
    val prev = prevTableReader.read(row)
    convertCellValue(prev)(content)
  }
}