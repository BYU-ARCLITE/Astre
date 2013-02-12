package controllers

import play.api.mvc._
import models.{Token, Account}
import play.api.libs.json.{JsArray, JsBoolean, JsObject, Json}
import businessLogic.{ActivityStreamLogic, ActivityStreamValidator, AccountLogic}
import java.util.Date
import com.twitter.util.Eval

/**
 * Created with IntelliJ IDEA.
 * User: camman3d
 * Date: 2/8/13
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
object API extends Controller {

  def AuthorizedAction(f: Request[AnyContent] => (Token => Result)) = Action {
    request =>

    // Get the authorization header
      val auth = request.headers.get("Authorization")
      if (auth.isDefined) {

        // Get the token
        val tokenId = auth.get.split(" ")(1)
        val token = Token.findById(tokenId)
        if (token.isDefined) {

          // Check the token isn't expired
          val now = new Date().getTime
          if (now <= token.get.expires)
            f(request)(token.get)
          else {
            // TODO : Delete the token?
            Unauthorized(Json.obj("success" -> false, "message" -> "Authorization token is expired"))
              .withHeaders("Access-Control-Allow-Origin" -> "*")
          }
        } else
          Unauthorized(Json.obj("success" -> false, "message" -> "Bad Authorization Token id"))
            .withHeaders("Access-Control-Allow-Origin" -> "*")
      } else
        Unauthorized(Json.obj("success" -> false, "message" -> "Missing Authorization header"))
          .withHeaders("Access-Control-Allow-Origin" -> "*")
  }

  //
  //  def receiveObject = AuthorizedAction {
  //    request =>
  //      token =>
  //        Ok
  //  }

  def receiveObject = AuthorizedAction {
    request =>
      token =>
        try {
          // Get the document
          var activity = request.body.asJson.get.as[JsObject]

          // Check the document
          if (ActivityStreamValidator.validateActivity(activity, token)) {

            // Inject the account id
            val accountId = Account.findByToken(token.id.get).get.id.get
            activity = activity ++ Json.obj("asField_account" -> accountId)

            // Add document to DB
            ActivityStreamLogic.saveDocument(activity)
            Ok(Json.obj("success" -> true))
              .withHeaders("Access-Control-Allow-Origin" -> "*")

          } else
            BadRequest(Json.obj("success" -> false, "message" -> "Malformed JSON document"))
              .withHeaders("Access-Control-Allow-Origin" -> "*")
        } catch {
          case _: Throwable => BadRequest(Json.obj("success" -> false, "message" -> "Bad request"))
            .withHeaders("Access-Control-Allow-Origin" -> "*")
        }
  }

  def authorize = Action(parse.urlFormEncoded) {
    request =>
      try {

        // Get the API key
        val key = request.body("key")(0)
        val account = Account.findById(key)
        if (account.isDefined) {

          // Make sure the host matches
          if (request.host.matches(account.get.host)) {

            // Create a token
            val actorRestriction = request.body.get("actorRestriction").map(_(0)).getOrElse(".*")
            val providerRestriction = request.body.get("providerRestriction").map(_(0)).getOrElse(".*")
            val verbRestriction = request.body.get("verbRestriction").map(_(0)).getOrElse(".*")
            val token = AccountLogic.createToken(account.get, actorRestriction, providerRestriction, verbRestriction)

            Ok(JsObject(Seq("success" -> JsBoolean(value = true), "token" -> token.toFullJson)))
              .withHeaders("Access-Control-Allow-Origin" -> "*")
          } else
            NotFound(Json.obj("success" -> false, "message" -> "Invalid host"))
              .withHeaders("Access-Control-Allow-Origin" -> "*")
        } else
          NotFound(Json.obj("success" -> false, "message" -> "Invalid key"))
            .withHeaders("Access-Control-Allow-Origin" -> "*")
      } catch {
        case _: Throwable => BadRequest(Json.obj("success" -> false, "message" -> "Bad request"))
          .withHeaders("Access-Control-Allow-Origin" -> "*")
      }
  }

  /**
   * The action for the preflight cross-domain options request
   * @return
   */
  def options = Action {
    request =>
      Ok.withHeaders("Access-Control-Allow-Origin" -> "*")
        .withHeaders("Access-Control-Allow-Methods" -> "POST, OPTIONS")
        .withHeaders("Access-Control-Allow-Headers" -> "Accept, Origin, Authorization, Content-Type")
        .withHeaders("Access-Control-Max-Age" -> "1728000")
  }

  def stream = Action {
    request =>
      try {

        // Get the account
        val key = request.queryString("key")(0)
        val account = Account.findById(key)
        if (account.isDefined) {

          // Get the stream
          val items: JsArray = ActivityStreamLogic.listByAccount(key)
          val result = JsObject(Seq("items" -> items))
          Ok(result)

        } else
          BadRequest(Json.obj("success" -> false, "message" -> "Account not found"))
            .withHeaders("Access-Control-Allow-Origin" -> "*")
      } catch {
        case _: Throwable => BadRequest(Json.obj("success" -> false, "message" -> "Bad request"))
          .withHeaders("Access-Control-Allow-Origin" -> "*")
      }
  }

}
