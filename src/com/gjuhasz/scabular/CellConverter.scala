package com.gjuhasz.scabular

trait CellConverter[T] {
  def convert(cell: Cell): T
}

case class GenericCellConverter[T](conv: Cell => T) extends CellConverter[T] {
  override def convert(cell: Cell): T = conv(cell)

}

object Text {
  def apply[T](convert: TextCell => T): CellConverter[T] = {
    GenericCellConverter {
      case c: TextCell => convert(c)
      case c => throw new IllegalArgumentException(s"Unexpected cell type ${c.cellType}")
    }
  }
  val AsString = Text(_.asString)
}
object Num {
  def apply[T](convert: NumericCell => T): CellConverter[T] = {
    GenericCellConverter {
      case c: NumericCell => convert(c)
      case c => throw new IllegalArgumentException(s"Unexpected cell type ${c.cellType}")
    }
  }
  val AsInt = Num(_.asInt)
}