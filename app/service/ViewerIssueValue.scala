package service

import scala.concurrent.{Future, Promise}
import com.atlassian.connect.playscala.AcConfig
import com.atlassian.connect.playscala.auth.Token
import com.atlassian.connect.playscala.auth.jwt.JwtSignedAcHostWS
import com.atlassian.connect.playscala.model.AcHostModelStore
import com.atlassian.connect.playscala.util.AcHostWS
import play.Logger
import play.api.libs.ws.WSResponse
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT
import play.api.libs.ws.WSAuthScheme.BASIC
import utils.Constants
import utils.Constants.{VALIDATION_ERROR,VALIDATED_FAILURE,VALIDATED_SUCCESSFULLY}

trait ViewerIssueValue extends JwtSignedAcHostWS{
  
  def getIssue(issueId: String)(implicit token: Token): Future[String] = {
    val request = AcHostWS.uri(s"/rest/api/2/issue/$issueId")
    Logger.debug(s"Making request to $request ")
   request.signedGet() map { response =>
      response.status match {
        case play.api.http.Status.OK =>
          Logger.info(s"200 OK from JIRA: ${response.body}")
          //@todo get value
          VALIDATED_SUCCESSFULLY
        case _ =>
          val msg = s"Error response from JIRA. Status code: ${response.status}. Status text: ${response.statusText}." +
            s" Body: ${response.body}"
          Logger.info(msg)
         VALIDATION_ERROR
      }
    }
}
 
  def validatePositiveField(num: Int): String = if(num>=0) VALIDATED_SUCCESSFULLY else VALIDATED_FAILURE
}
