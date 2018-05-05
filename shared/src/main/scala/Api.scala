package shared

case class Person(name: String, age: Int)

object Validation {
  def validatePerson(name: String, age: String): Either[(Option[String], Option[String]), Person] = {

  val nameVal =
    if(name.size < 2) {
      Some("Name should be at least 2 chars")
    } else if (name.head != name.head.toUpper) {
      Some("Name should start with capital letter")
    } else None

    val ageVal = try {
      age.toInt
      None
    } catch { case n: NumberFormatException => Some("Age is not valid") }

    (nameVal, ageVal) match {
      case (None, None) => Right(Person(name, age.toInt))
      case failure => Left(failure)
    }
  }
}

trait Api {
  def getUser(): Person
  def addUser(p: Person): Option[Person]
}
