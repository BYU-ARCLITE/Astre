package models

import couchOrm.{CouchReadable, GenericCouchModel}
import play.api.libs.json.{JsObject, Json}


case class Account(username: String, password: String, host: String, tokens: List[Token]) extends GenericCouchModel {

  def save: Account = {
    save(toJson)
    this
  }

  def toJson = Json.obj(
    "username" -> username,
    "password" -> password,
    "host" -> host,
    "tokens" -> tokens.filter(_.id.isDefined).map(_.id.get)
  )

  def set(username: String = this.username, password: String = this.password, host: String = this.host,
          tokens: List[Token] = this.tokens) = {
    val account = copy(username, password, host, tokens)
    account.id = id
    account.rev = rev
    account
  }

}

object Account extends GenericCouchModel with CouchReadable {

  def fromJson(json: JsObject) = Account(
    (json \ "username").as[String],
    (json \ "password").as[String],
    (json \ "host").as[String],
    (json \ "tokens").as[List[String]].map(Token.findById(_).get)
  )

  def findById(id: String): Option[Account] = findById(id, fromJson).map(_.asInstanceOf[Account])

  def findByToken(id: String): Option[Account] = find(id, "filtering", "hasToken", fromJson).map(_.asInstanceOf[Account])

  def findByUsername(username: String): Option[Account] = find(username, "filtering", "byUsername", fromJson).map(_.asInstanceOf[Account])
}