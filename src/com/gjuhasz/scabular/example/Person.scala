package com.gjuhasz.scabular.example

case class Person(name: String, age: Int, isStudent: Boolean)

case object PersonBuilder {
  def name(name: String) = Pb1(name)
}

case class Pb1(name: String) {
  def age(age: Int) = Pb2(name, age)
}

case class Pb2(name: String, age: Int) {
  def isStudent(isStudent: Boolean) = Person(name, age, isStudent)
}