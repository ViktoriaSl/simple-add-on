package controllers

import java.net.{HttpURLConnection, URL}
import scala.concurrent.Future
import play.api.mvc.{Action, Controller}
import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.auth.Token
import com.atlassian.connect.playscala.controllers.ActionJwtValidator
import service.{InMemoryHeartBeatService, ViewerDetailsService, ViewerIssueValue}
import play.api.libs.json._
import play.api.libs.Crypto
import com.atlassian.connect.playscala.store.DefaultDbConfiguration
import com.atlassian.connect.playscala.util.AcHostWS
import com.atlassian.connect.playscala.util.AcHostWS.uri
import play.Logger
import play.api.libs.ws.WSResponse
import utils.Constants.VALIDATED_SUCCESSFULLY

//action
object Poller extends Controller with ViewerIssueValue with ActionJwtValidator with DefaultDbConfiguration
  with PlayAcConfigured {


  def index() = Action { implicit request =>
    jwtValidated { implicit token: Token =>
      val hostId = token.acHost.key.value
      
//      val user = token.user
//      val maybeUserId = Some("admin")
println("we have request : "+request)
      ( for {
        resourceId <- request.getQueryString("issue_key")
      } yield {
        retrieveIssue(resourceId)
//        getDetails(resourceId)((Token(acHostModel, Option(UserContext("admin", Option(UserInfo("admin", "admin", "admin"))))), jwtAuthorizationGenerator))
  
//        val future = uri(acHostModel, s"/rest/atlassian-connect/1/macro/app/" + acConfig.pluginKey).signedDelete()(Token(acHostModel, Option(UserContext("admin", Option(UserInfo("admin", "admin", "admin"))))), jwtAuthorizationGenerator)
  
  
        Ok(views.html.validation(hostId, resourceId, "",VALIDATED_SUCCESSFULLY))
      }) getOrElse BadRequest("Missing issue key")
      
  }
  }
}
