package controllers

import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.auth.Token
import com.atlassian.connect.playscala.controllers.ActionJwtValidator
import com.atlassian.connect.playscala.store.DefaultDbConfiguration
import play.api.mvc.{Action, Controller}
import service.ViewerIssueValue
import utils.Constants.VALIDATED_SUCCESSFULLY

object Poller extends Controller with ViewerIssueValue with ActionJwtValidator with DefaultDbConfiguration
  with PlayAcConfigured {
  
  def index() = Action { implicit request =>
    jwtValidated { implicit token: Token =>
      val hostId = token.acHost.key.value
      ( for {
        resourceId <- request.getQueryString("issue_key")
      } yield {
        getIssue(resourceId)
        //@todo
        Ok(views.html.validation(hostId, resourceId, "",VALIDATED_SUCCESSFULLY))
      }) getOrElse BadRequest("Missing issue key")
  }
  }
}
