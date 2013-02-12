package tools

import play.api.Play
import play.api.Play.current
import play.api.libs.ws.WS
import concurrent.Await
import concurrent.duration._
import com.ning.http.client.Realm
import concurrent.ExecutionContext.Implicits.global

object CouchInitializer {

  val config = {
    val host = Play.configuration.getString("couchdb.host").get
    val prefix = Play.configuration.getString("couchdb.prefix").get
    val username = Play.configuration.getString("couchdb.username").get
    val password = Play.configuration.getString("couchdb.password").get
    CouchConfig(host, prefix, username, password)
  }

  def init(databases: List[String]) {
    // Get the list of databases
    val dbs = Await.result(WS.url(config.host + "_all_dbs").get().map(_.json.as[List[String]]), 10 seconds)

    // Only create the dbs that don't exist
    databases.foreach(db => {
      if (!dbs.contains(db))
        createDb(db)
    })
  }

  def createDb(db: String) {
    WS.url(config.host + config.database + db + "/").withAuth(config.username, config.password, Realm.AuthScheme.BASIC).put("")
  }

}
