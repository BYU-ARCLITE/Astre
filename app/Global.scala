import play.api.GlobalSettings
import tools.CouchInitializer

/**
 * Created with IntelliJ IDEA.
 * User: camman3d
 * Date: 2/8/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    setupDB()
  }

  def setupDB() {
    val databases: List[String] = List("account", "token", "activity")
    CouchInitializer.init(databases)
  }

}
