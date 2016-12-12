package service

import scala.concurrent.Future
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

trait ViewerIssueValue extends JwtSignedAcHostWS{
  def getDetails(resourceId: String)(implicit token: Token): Unit ={
    val request = AcHostWS.uri(s"/rest/api/2/issue/$resourceId?expand=schema")
    Logger.debug(s"Making request to ${request.url} with ${request.queryString}")
    val future: Future[WSResponse] = request.signedGet()
    println("header "+request.headers)
    future onSuccess {
      case response =>
        println("answer "+response.status)
        println("answer "+response.statusText)
        println("answer "+response.allHeaders)
    }
  }
  
  def retrieveIssue(issueId: String,jwt:String)(implicit token: Token): Unit = {
    println("jwt "+jwt)
    val request = AcHostWS.uri(s"/rest/api/2/issue/$issueId")

    Logger.debug(s"Making request to ${request} ")
   request.signedGet() map { response =>
      response.status match {
        case play.api.http.Status.OK =>
          Logger.info(s"200 OK from JIRA: ${response.body}")
        case _ =>
          val msg = s"Error response from JIRA. Status code: ${response.status}. Status text: ${response.statusText}." +
            s" Body: ${response.body}"
          Logger.info(msg)
      }
    }
}
}
