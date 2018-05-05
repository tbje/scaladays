package example
import shared._

object MyService extends Api {

  def getUser(): Person =
    if (scala.util.Random.nextBoolean())
      Person("Jenny", 27)
    else
      Person("Pierre", 10)

  def addUser(p: Person) = {
    Validation.validatePerson(p.name, p.age.toString) match {
      case Left(err) =>
        println(s"Person not added: $err")
        None
      case Right(p) =>
        println(s"Person added: $p")
        Some(p)
    }
  }
}
