package models

import couchOrm._
import play.api.libs.json._
import play.api.libs.json.JsObject

case class Token(
                  expires: Long,
                  actorRestriction: String = ".*",
                  providerRestriction: String = ".*",
                  verbRestriction: String = ".*"
                  ) extends GenericCouchModel {

  def save: Token = {
    save(toJson)
    this
  }

  def toJson = Json.obj(
    "expires" -> expires,
    "actorRestriction" -> actorRestriction,
    "providerRestriction" -> providerRestriction,
    "verbRestriction" -> verbRestriction
  )

  def toFullJson = toJson ++ Json.obj("id" -> id.get)

  def set(expires: Long = this.expires, actorRestriction: String = this.actorRestriction,
          providerRestriction: String = this.providerRestriction, verbRestriction: String = this.verbRestriction) = {
    val token = copy(expires, actorRestriction, providerRestriction, verbRestriction)
    token.id = id
    token.rev = rev
    token
  }
}

object Token extends GenericCouchModel with CouchReadable {

  def fromJson(json: JsObject) = Token(
    (json \ "expires").as[Long],
    (json \ "actorRestriction").as[String],
    (json \ "providerRestriction").as[String],
    (json \ "verbRestriction").as[String]
  )

  def findById(id: String): Option[Token] = findById(id, fromJson).map(_.asInstanceOf[Token])
}
