package controllers

import scala.concurrent.Future
import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.auth.Token
import com.atlassian.connect.playscala.controllers.ActionJwtValidator
import com.atlassian.connect.playscala.store.DefaultDbConfiguration
import play.api.mvc.{Action, Controller}
import service.ViewerIssueValue
import utils.Constants.VALIDATED_SUCCESSFULLY
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import views._
import models._
import scala.collection.mutable.ArrayBuffer
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import java.util.Calendar

object Poller extends Controller with ViewerIssueValue with ActionJwtValidator with DefaultDbConfiguration
  with PlayAcConfigured {
  
  def index() =  Action { implicit request =>
    jwtValidated { implicit token: Token =>
      val hostId = token.acHost.key.value
      (for {
        resourceId <- request.getQueryString("issue_key")
      } yield {
        getIssue(resourceId)
        //@todo show message result
        Ok(views.html.validation(hostId, resourceId, "", VALIDATED_SUCCESSFULLY))
      }) getOrElse BadRequest("Missing issue key")
    }
  }
}
