package models.couchOrm

import tools.{CouchConfig, CouchConnector}
import concurrent.duration._
import concurrent.Await
import play.api.libs.json.JsObject

trait CouchReadable {

  private lazy val waitTime = (10 seconds)

  def findById(id: String, jsonWriter: JsObject => GenericCouchModel)(implicit config: CouchConfig): Option[GenericCouchModel] = {
    val result = Await.result(CouchConnector.get(id), waitTime)
    if (result._1 == 200)
      Some(modelFromJson(jsonWriter, result._2))
    else
      None
  }

  def modelFromJson(jsonWriter: (JsObject) => GenericCouchModel, json: JsObject): GenericCouchModel = {
    val model = jsonWriter(json)
    model.id = Some((json \ "_id").as[String])
    model.rev = Some((json \ "_rev").as[String])
    model
  }

  def find(id: String, design: String, view: String, jsonWriter: JsObject => GenericCouchModel)(implicit config: CouchConfig): Option[GenericCouchModel] = {
    val result = Await.result(CouchConnector.view(design, view, Some(id)), waitTime)
    if (result._1 == 200) {
      if ((result._2 \ "total_rows").as[Int] > 0) {
        val json = ((result._2 \ "rows")(0) \ "value").asInstanceOf[JsObject]
        Some(modelFromJson(jsonWriter, json))
      } else
        None
    } else
      None
  }

}
