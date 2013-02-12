package businessLogic

import play.api.libs.json.{JsArray, JsObject}
import play.api.Play
import play.api.Play.current
import tools.{CouchConnector, CouchConfig}
import concurrent.{ExecutionContext, Await}
import concurrent.duration._
import ExecutionContext.Implicits.global

/**
 * Created with IntelliJ IDEA.
 * User: camman3d
 * Date: 2/11/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
object ActivityStreamLogic {

  implicit val config = {
    val host = Play.configuration.getString("couchdb.host").get
    val database = Play.configuration.getString("couchdb.prefix").get + "activity"
    val username = Play.configuration.getString("couchdb.username").get
    val password = Play.configuration.getString("couchdb.password").get
    CouchConfig(host, database, username, password)
  }

  def saveDocument(json: JsObject) {
    CouchConnector.save(json)
  }

  def listByAccount(key: String): JsArray = {
    val activities = CouchConnector.view("list", "byAccount", Some(key)).map(response => {
      val jsonObjects = (response._2 \ "rows").as[JsArray].value
      JsArray(jsonObjects.map(_ \ "value"))
    })
    Await.result(activities, 10 seconds)
  }
}
