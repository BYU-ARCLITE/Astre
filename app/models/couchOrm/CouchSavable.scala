package models.couchOrm

import tools.{CouchConfig, CouchConnector}
import play.api.libs.json.{Json, JsString, JsObject}
import concurrent.duration._
import concurrent.Await

trait CouchSavable {

  private lazy val waitTime = (10 seconds)

  def insert(document: JsObject)(implicit config: CouchConfig): (String, String) = {
    val result = Await.result(
      CouchConnector.post(document), waitTime
    )
    if (result._1 == 201) {
      val id = (result._2 \ "id").as[String]
      val rev = (result._2 \ "rev").as[String]
      (id, rev)
    } else
      throw new CouchConflictError("CouchDB conflict while inserting.")
  }

  def update(document: JsObject, id: String, rev: String)(implicit config: CouchConfig): (String, String) = {
    val json = addIdAndRev(document, id, rev)
    val result = Await.result(CouchConnector.put(id, json), waitTime)
    if (result._1 == 201) {
      val id = (result._2 \ "id").as[String]
      val rev = (result._2 \ "rev").as[String]
      (id, rev)
    } else
      throw new CouchConflictError("CouchDB conflict while updating.")
  }

  def save(document: JsObject, id: Option[String] = None, rev: Option[String] = None)(implicit config: CouchConfig): (String, String) = {
    if (id.isDefined)
      update(document, id.get, rev.get)
    else
      insert(document)
  }

  def addIdAndRev(json: JsObject, id: String, rev: String): JsObject = json ++ Json.obj("_id" -> id, "_rev" -> rev)

}
