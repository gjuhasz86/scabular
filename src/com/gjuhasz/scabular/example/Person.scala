package com.gjuhasz.scabular.example

case class Person(name: String, age: Int)

case object PersonBuilder {
  def name(name: String) = Pb1(name)
}

case class Pb1(name: String) {
  def age(age: Int) = Person(name, age)
}