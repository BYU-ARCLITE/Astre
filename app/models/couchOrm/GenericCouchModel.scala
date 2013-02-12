package models.couchOrm

import play.api.libs.json.JsObject
import tools.CouchConfig
import play.api.Play
import play.api.Play.current

/**
 * An abstract CouchDB model
 */
abstract class GenericCouchModel extends CouchSavable {
  // These need to be mutable in order for the couch reader and inheritance to work correctly
  var id: Option[String] = None
  var rev: Option[String] = None

  implicit lazy val config: CouchConfig = {
    val host = Play.configuration.getString("couchdb.host").get
    val prefix = Play.configuration.getString("couchdb.prefix").get
    val username = Play.configuration.getString("couchdb.username").get
    val password = Play.configuration.getString("couchdb.password").get
    val database = prefix + getClass.getSimpleName.toLowerCase.replaceAll("\\$", "")

    CouchConfig(host, database, username, password)
  }

  def save(json: JsObject) {
    val (newId, newRev) = save(json, id, rev)
    id = Some(newId)
    rev = Some(newRev)
  }

}
