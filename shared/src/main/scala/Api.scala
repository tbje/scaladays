package shared

case class Person(name: String, age: Int)

trait Api {
  def getUser(): Person
}
