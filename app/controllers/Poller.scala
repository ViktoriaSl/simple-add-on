package controllers

import scala.concurrent.Future
import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.auth.Token
import com.atlassian.connect.playscala.controllers.ActionJwtValidator
import com.atlassian.connect.playscala.store.DefaultDbConfiguration
import play.api.mvc.{Action, Controller}
import service.ViewerIssueValue
import scala.concurrent.ExecutionContext.Implicits.global

object Poller extends Controller with ViewerIssueValue with ActionJwtValidator with DefaultDbConfiguration
  with PlayAcConfigured {
  
  def index() = Action.async { implicit request =>
    jwtValidatedAsync { implicit token: Token =>
      val hostId = token.acHost.key.value
      (for {
        resourceId <- request.getQueryString("issue_key")
      } yield {
        getIssue(resourceId).map { validationResult =>
          Ok(views.html.validation(hostId, resourceId, "", validationResult))
        }
      }).getOrElse(Future {BadRequest("Missing issue key")})
    }
  }
}
