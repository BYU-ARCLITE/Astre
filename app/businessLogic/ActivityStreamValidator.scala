package businessLogic

import play.api.libs.json.{JsArray, JsObject}
import javax.xml.bind.DatatypeConverter
import models.Token

/**
 * Created with IntelliJ IDEA.
 * User: camman3d
 * Date: 2/11/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
object ActivityStreamValidator {

  def validateActivity(json: JsObject, token: Token): Boolean = {
    try {
      // Make sure the actor is present and valid
      assert((json \ "actor").isInstanceOf[JsObject])
      assert(validateObject((json \ "actor").as[JsObject]))

      // Make sure there is an id or a url
      assert((json \ "id").asOpt[String].isDefined || (json \ "url").asOpt[String].isDefined)

      // Make sure there is a published and that it is rfc 3339 compliant
      assert((json \ "published").asOpt[String].isDefined)
      DatatypeConverter.parseDateTime((json \ "published").as[String])

      // If there is an updated then make sure it is rfc 3339 compliant
      if ((json \ "updated").asOpt[String].isDefined)
        DatatypeConverter.parseDateTime((json \ "updated").as[String])

      // TODO: Check restrictions

      // We're good!
      true
    } catch {
      case _: Throwable => false
    }
  }

  def validateObject(json: JsObject): Boolean = {
    try {
      // Check each of the attachments if they exist
      if ((json \ "attachments").asOpt[JsObject].isDefined)
        for (attachment <- (json \ "attachments").as[JsArray].value)
          assert(validateObject(attachment.as[JsObject]))

      // If there is an author then verify it
      if ((json \ "author").asOpt[JsObject].isDefined)
        assert(validateObject((json \ "author").as[JsObject]))

      // If there is an image then verify it
      if ((json \ "image").asOpt[JsObject].isDefined)
        assert(validateMediaLink((json \ "image").as[JsObject]))

      // If there is a published then make sure it is rfc 3339 compliant
      if ((json \ "published").asOpt[String].isDefined)
        DatatypeConverter.parseDateTime((json \ "published").as[String])

      // If there is an updated then make sure it is rfc 3339 compliant
      if ((json \ "updated").asOpt[String].isDefined)
        DatatypeConverter.parseDateTime((json \ "updated").as[String])

      // Make sure there is an id or a url
      assert((json \ "id").asOpt[String].isDefined || (json \ "url").asOpt[String].isDefined)

      // We're good!
      true
    } catch {
      case _: Throwable => false
    }
  }

  def validateMediaLink(json: JsObject): Boolean = {
    try {
      // Make sure there is a url
      assert((json \ "url").asOpt[String].isDefined)

      // We're good!
      true
    } catch {
      case _: Throwable => false
    }
  }


}
