package utils

import scala.Predef.String
import org.apache.commons.codec.net.URLCodec

/**
 * @since v1.0
 */
object KeyUtils {

  // TODO: replace with PercentEscaper when guava 15 is available.
  private[utils] var codec: URLCodec = new URLCodec("UTF-8")

  def buildHeartbeatKey(hostId: String, resourceId: String, userId: String) = buildKey("heartbeat", hostId, resourceId, userId)

  def buildViewerSetKey(hostId: String, resourceId: String): String = buildKey("viewerset", hostId, resourceId)

  def buildDisplayNameKey(hostId: String, username: String): String = buildKey("cache", hostId, username, "displayName")

  private def buildKey(components: String*): String = components.map(codec.encode).mkString(Constants.KEY_SEPARATOR)
}
