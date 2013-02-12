package businessLogic

import models.{Token, Account}
import tools.Hasher
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: camman3d
 * Date: 2/8/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
object AccountLogic {

  def authenticate(username: String, password: String): Boolean =
    Account.findByUsername(username).map(_.password == Hasher.sha256Base64(password)).getOrElse(false)

  def createAccount(username: String, password: String, host: String) {
    Account(username, Hasher.sha256Base64(password), host, List()).save
  }

  def createToken(account: Account, actorRestriction: String, providerRestriction: String, verbRestriction: String): Token = {
    // Make the token good for two hours
    val time = new DateTime().plusHours(2)
    val token = Token(time.getMillis, actorRestriction, providerRestriction, verbRestriction).save
    account.set(tokens = token :: account.tokens).save
    token
  }

}
