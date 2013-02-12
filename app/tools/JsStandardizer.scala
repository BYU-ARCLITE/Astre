package tools

import play.api.libs.json.JsObject

/**
 * Created with IntelliJ IDEA.
 * User: camman3d
 * Date: 2/8/13
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */

case class JsStandardizerWrapper(json: JsObject) {
  def validate(requiredFields: Seq[String], forbiddenFields: Seq[String] = Seq()): Boolean = {
    val hasRequired = (true /: requiredFields.map(json.keys.contains(_)))(_ && _)
    val hasForbidden = (false /: forbiddenFields.map(json.keys.contains(_)))(_ || _)
    hasRequired && !hasForbidden
  }
}

object JsStandardizer {
  def ~:(json: JsObject) = {
    JsStandardizerWrapper(json)
  }
}
