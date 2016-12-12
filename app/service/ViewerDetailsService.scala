package service

import utils.KeyUtils._
import play.cache.Cache
import org.apache.commons.lang3.StringUtils
import play.Logger
import scala.concurrent.Future
import utils.Constants._
import play.api.libs.ws.WSResponse
import play.api.libs.json.{ Json, JsValue, JsString }
import com.atlassian.connect.playscala.auth.Token
import play.api.libs.concurrent.Execution.Implicits._
import scalaz.syntax.std.option._
import com.atlassian.connect.playscala.auth.jwt.{ JwtSignedAcHostWS, JwtConfig }
import com.atlassian.connect.playscala.util.AcHostWS

case class Heartbeat(displayName: String, lastSeen: String)

/**
 * @since v1.0
 */
trait ViewerDetailsService extends JwtSignedAcHostWS {
  val DISPLAY_NAME = "displayName"
    val heartbeatService = InMemoryHeartBeatService
//  val heartbeatService = RedisHeartbeatService
  /**
   * @return map of userids actively viewing <code>resourceId</code> on <code>hostId</code> to any additional details
   *         we have about the user.
   */
  def getViewersWithDetails(resourceId: String, hostId: String)(implicit token: Token): Map[String, JsValue] = {
    val viewers: Map[String, String] = heartbeatService.list(hostId, resourceId)
    viewers.map {
      case (username, lastSeen) => {
        username -> Json.parse(
          s"""
             |{
             |  "displayName": "${getCachedDisplayNameFor(hostId, username).getOrElse(username)}",
             |  "lastSeen": "$lastSeen"
             |}
          """.stripMargin)
      }
    }
  }

  import play.api.Play.current
  lazy val displayNameCacheExpirySeconds: Int = current.configuration.getInt(DISPLAY_NAME_CACHE_EXPIRY_SECONDS).getOrElse(DISPLAY_NAME_CACHE_EXPIRY_SECONDS_DEFAULT)

  /**
   * Query cache for user's display name. Return if present in cache. Otherwise, initiate cache population and return
   * null, so the full name is available in the future. Non-blocking.
   *
   * @return user display name, or null if not yet known.
   */
  private def getCachedDisplayNameFor(hostId: String, username: String)(implicit token: Token): Option[String] = {
    val key: String = buildDisplayNameKey(hostId, username)
    val cachedValue: String = Cache.get(key).asInstanceOf[String]
    if (StringUtils.isNotEmpty(cachedValue)) {
      return Some(cachedValue)
    }

    Logger.info(s"Cache miss. Requesting details for $username on $hostId...")
//generated request
    val request = AcHostWS.uri("/rest/api/2/user").withQueryString("username" -> username)
    Logger.debug(s"Making request to ${request.url} with ${request.queryString}")
    val future: Future[WSResponse] = request.signedGet()
    future onSuccess {
      case response if response.status == play.api.http.Status.OK =>
        Logger.debug(s"Response from JIRA: ${response.status} ${response.statusText} - ${response.body}")
        val userDetailsJson = response.json
        val displayNameNode = userDetailsJson \ DISPLAY_NAME
        displayNameNode match {
          case JsString(displayName) =>
            Logger.info(s"Obtained display name for $username on $hostId: $displayName")
            Cache.set(key, displayName, displayNameCacheExpirySeconds)
          case _ => Logger.error("Could not extract full name from user details, which were: " + userDetailsJson.toString)
        }
      case response =>
        Logger.debug(s"Unexpected response from JIRA while trying to retrieve details for $username on $hostId: ${response.status} ${response.statusText} - ${response.body}")
    }
    future onFailure {
      case throwable =>
        Logger.error(s"Encountered an exception $throwable while trying to retrieve details for $username on $hostId...")
    }
    None
  }
}
