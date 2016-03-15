package com.gjuhasz.scabular

import java.time.LocalDate
import org.junit.Test
import org.junit.Assert
import scala.io.Source
import java.nio.file.Paths
import java.nio.file.Files

class Tester {
  import TestData._
  val input = Files.readAllBytes(Paths.get("game.xls"))
  @Test
  def namesShouldMatch(): Unit = {
    val tr = TableReader(Name)
    val actual = tr.readXls(input)
    Assert.assertEquals(names, actual)
  }
}

object Name extends ColSpec(2) with TextXlsRdColDef[String] {
  override def xlsReadText(tc: Text): String = tc.asString
}

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


