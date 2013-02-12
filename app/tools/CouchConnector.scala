package tools

import play.api.libs.json.{Json, JsString, JsObject}
import concurrent.{ExecutionContext, Future}
import play.api.libs.ws.WS
import com.ning.http.client.Realm
import ExecutionContext.Implicits.global


case class CouchConfig(host: String, database: String, username: String, password: String)

object CouchConnector {
  /*
   * Standard methods
   */
  def get(id: String)(implicit config: CouchConfig): Future[(Int, JsObject)] = {
    val url = config.host + config.database + "/" + id
    WS.url(url).withAuth(config.username, config.password, Realm.AuthScheme.BASIC).get()
      .map(response => (response.status, response.json.asInstanceOf[JsObject]))
  }

  def put(id: String, document: JsObject)(implicit config: CouchConfig): Future[(Int, JsObject)] = {
    val url = config.host + config.database + "/" + id
    WS.url(url).withAuth(config.username, config.password, Realm.AuthScheme.BASIC).put(document)
      .map(response => (response.status, response.json.asInstanceOf[JsObject]))
  }

  def post(document: JsObject)(implicit config: CouchConfig): Future[(Int, JsObject)] = {
    val url = config.host + config.database
    WS.url(url).withAuth(config.username, config.password, Realm.AuthScheme.BASIC).post(document)
      .map(response => (response.status, response.json.asInstanceOf[JsObject]))
  }

  def delete(id: String, revision: String)(implicit config: CouchConfig): Future[(Int, JsObject)] = {
    val url = config.host + config.database + "/" + id + "?" + revision
    WS.url(url).withAuth(config.username, config.password, Realm.AuthScheme.BASIC).delete()
      .map(response => (response.status, response.json.asInstanceOf[JsObject]))
  }
  
  /*
   * Special things
   */

  def view(design: String, view: String, key: Option[String])(implicit config: CouchConfig) = {
    val id = "_design/" + design + "/_view/" + view + key.map("?key=%22" + _ + "%22").getOrElse("")
    get(id)
  }

  def allDocs(implicit config: CouchConfig): Future[JsObject] = get("_all_docs").map(_._2)

  def addId(json: JsObject, id: String): JsObject = json + ("_id" -> JsString(id))

  def addRev(json: JsObject, rev: String): JsObject = json + ("_rev" -> JsString(rev))
  
  def save(document: JsObject)(implicit config: CouchConfig): Future[JsObject] = {
    // Save the document
    val id = (document \ "_id").asOpt[String]
    val result = (if (id.isDefined) put(id.get, document) else post(document)).map(_._2)

    // Update the result with the new id/rev
    result.map(json => {
      document ++ Json.obj(
        "_id" -> (json \ "id").as[String],
        "_rev" -> (json \ "rev").as[String]
      )
    })
  }
}
