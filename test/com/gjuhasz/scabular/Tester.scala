package com.gjuhasz.scabular

import java.time.LocalDate
import org.junit.Test
import org.junit.Assert
import scala.io.Source
import java.nio.file.Paths
import java.nio.file.Files
import com.gjuhasz.scabular.ColDef.ColSpec

class Tester {
  import TestData._
  val input = Files.readAllBytes(Paths.get("game.xls"))
  @Test
  def namesShouldMatch(): Unit = {
    val tr = TableReader(Name, ())
    val actual = tr.readXls(input)
    Assert.assertEquals(names, actual)
  }

  @Test
  def playersShouldMatch(): Unit = {
    def tr = Trb
      .col(Name)
      .col(BirthDate)
      .col(Points)
      .col(Average)
      .col(TrueFalse)
      .build

    val actual = tr.readXls(input)
    Assert.assertEquals(players, actual)
  }
}

object Name extends ColSpec(0) with ColDef.Xls.Read.Text.AsString
object BirthDate extends ColSpec(1) with ColDef.Xls.Read.Num.AsLocalDate
object Points extends ColSpec(2) with ColDef.Xls.Read.NumOrBlank.AsIntOption
object Average extends ColSpec(3) with ColDef.Xls.Read.Num.AsDouble
object TrueFalse extends ColSpec(4) with ColDef.Xls.Read.Text.AsString

object TestData {
  val alice = Player("Alice", LocalDate.of(1991, 1, 2), Some(9), 5.1, true)
  val bob = Player("Bob", LocalDate.of(1989, 12, 9), Some(6), 7.2, false)
  val cecil = Player("Cecil", LocalDate.of(1985, 1, 2), None, 5.2, true)

  val players = Seq(alice, bob, cecil)
  val names = players.map(_.name)
  val averages = players.map(_.average)
}

case class Player(
  name: String,
  birthDay: LocalDate,
  points: Option[Int],
  average: Double,
  trueFalse: Boolean)

object Player {

  object Builder {
    def apply(): Builder = Builder(None, None, None, None, None)
  }

  case class Builder(
    private val _name: Option[String],
    private val _birthDay: Option[LocalDate],
    private val _points: Option[Option[Int]],
    private val _average: Option[Double],
    private val _trueFalse: Option[Boolean]) {

    val name = (x: String) => this.copy(_name = Some(x))
    val birthDay = (x: LocalDate) => this.copy(_birthDay = Some(x))
    val points = (x: Option[Int]) => this.copy(_points = Some(x))
    val average = (x: Double) => this.copy(_average = Some(x))
    val trueFalse = (x: Boolean) => this.copy(_trueFalse = Some(x))

    val build = Player(
      name = _name.getOrElse("UNSET"),
      birthDay = _birthDay.getOrElse(LocalDate.now),
      points = _points.getOrElse(None),
      average = _average.getOrElse(0.0),
      trueFalse = _trueFalse.getOrElse(false))
  }

}