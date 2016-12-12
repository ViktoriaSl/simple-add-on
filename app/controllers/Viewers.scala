package controllers

import play.api.mvc._
import play.Logger
import play.api.libs.json._
import service.{  InMemoryHeartBeatService, ViewerDetailsService }
import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.controllers.PageTokenValidator
import scalaz._, Scalaz._
import com.atlassian.connect.playscala.store.DefaultDbConfiguration


object Viewers extends Controller with PageTokenValidator with ViewerDetailsService with DefaultDbConfiguration with PlayAcConfigured {

  def put(hostId: String, resourceId: String, userId: String) = Action { implicit request =>
    Logger.debug(s"Putting $hostId/$resourceId/$userId")

    PageTokenValidated(allowInsecurePolling = true) { implicit token =>
            InMemoryHeartBeatService.put(hostId, resourceId, userId)
//      RedisHeartbeatService.put(hostId, resourceId, userId)
      val viewersWithDetails: Map[String, JsValue] = getViewersWithDetails(resourceId, hostId)
      val result = Json.toJson(viewersWithDetails)
      Logger.debug(s"View details result: ${result}")
      Ok(result).withTokenHeader
    }
  }

  def delete(hostId: String, resourceId: String, userId: String) = Action { implicit request =>
    PageTokenValidated { implicit token =>
      Logger.debug(s"Deleting ${hostId}/${resourceId}/${userId}")
            InMemoryHeartBeatService.delete(hostId, resourceId, userId)
//      RedisHeartbeatService.delete(hostId, resourceId, userId)
      NoContent: Result // no need to add the token header as the user is leaving
    }
  }
}
