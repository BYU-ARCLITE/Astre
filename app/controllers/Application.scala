package controllers

import play.api.mvc._
import models.Account
import businessLogic.AccountLogic
import javax.xml.bind.DatatypeConverter

object Application extends Controller {


  def index = Action { request =>
    Ok(views.html.index())
  }

  def login = Action(parse.urlFormEncoded) {
    request =>

      val username = request.body("username")(0)
      val password = request.body("password")(0)

      if (AccountLogic.authenticate(username, password))
        Redirect(routes.Application.home()).withSession("username" -> username)
      else
        Redirect(routes.Application.index())
  }

  def signup = Action(parse.urlFormEncoded) {
    request =>

      val username = request.body("username")(0)
      val password = request.body("password")(0)
      val host = request.body("host")(0)

      AccountLogic.createAccount(username, password, host)
      Redirect(routes.Application.home()).withSession("username" -> username)
  }

  def home = Action {
    request =>
      val username = request.session.get("username")
      if (username.isDefined) {
        val account = Account.findByUsername(username.get)
        if (account.isDefined) {
          Ok("Your account authorization ID is: " + account.get.id.get + "\nThis is valid for the host: " + account.get.host)
        } else
          Redirect(routes.Application.index())
      } else
        Redirect(routes.Application.index())
  }

  def test = Action {

//    val time = "13604345"
//    val parsed = DatatypeConverter.parseDateTime(time)
    Ok(views.html.test())
  }

}