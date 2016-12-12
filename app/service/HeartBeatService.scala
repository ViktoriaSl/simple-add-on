package service

import scala.collection.{ mutable, immutable }
import utils.Constants
import utils.KeyUtils._
import utils.RedisUtils._
import redis.clients.jedis.Transaction
import scalaz._, Scalaz._
import play.api.Logger

/**
 * @since v1.0
 */
trait HeartBeatService {

  import play.api.Play.current

  lazy val ViewerExpirySeconds: Int =
    current.configuration.getInt(Constants.VIEWER_EXPIRY_SECONDS).getOrElse(Constants.VIEWER_EXPIRY_SECONDS_DEFAULT)

  lazy val ViewerSetExpirySeconds: Int =
    current.configuration.getInt(Constants.VIEWER_SET_EXPIRY_SECONDS).getOrElse(Constants.VIEWER_SET_EXPIRY_SECONDS_DEFAULT)

  /**
   * @return map of userids actively viewing <code>resourceId</code> on <code>hostId</code>, with the UTC timestamp of their last heartbeat.
   */
  def list(hostId: String, resourceId: String): immutable.Map[String, String]

  /**
   * Record the fact that userId has recently viewed resourceId on hostId. It is up to the implementation to expire
   * this event as appropriate.
   */
  def put(hostId: String, resourceId: String, userId: String)

  /**
   * Record the fact that userId has stopped viewing resourceId on hostId.
   * <p>
   * There is no guarantee this will be called when the user stops viewing the resource. The implementation is
   * responsible for expiring viewers as appropriate.
   */
  def delete(hostId: String, resourceId: String, viewer: String)
}

object InMemoryHeartBeatService extends HeartBeatService {
  val store: mutable.Map[(String, String), mutable.Map[String, String]] = mutable.Map()

  private def get(hostId: String, resourceId: String): mutable.Map[String, String] = {
    val userToLastSeen: mutable.Map[String, String] = store getOrElseUpdate ((hostId, resourceId), mutable.Map[String, String]())
    Logger.debug(s"Users viewing ${hostId}-${resourceId}: ${userToLastSeen}")
    val expiredIfLastSeenBefore: Long = System.currentTimeMillis() - ViewerExpirySeconds * 1000
    Logger.debug(s"Any users last seen before ${expiredIfLastSeenBefore} will be removed from the heartbeat")
    val activeUserToLastSeen = userToLastSeen.filter {
      case (userId, lastSeen) => lastSeen.toLong >= expiredIfLastSeenBefore
    }
    store put ((hostId, resourceId), activeUserToLastSeen)
    activeUserToLastSeen
  }

  /**
   * @return map of userids actively viewing <code>resourceId</code> on <code>hostId</code>, with the UTC timestamp of their last heartbeat.
   */
  def list(hostId: String, resourceId: String) = {
    val viewers: mutable.Map[String, String] = get(hostId, resourceId)
    if (viewers.isEmpty) {
      store.remove((hostId, resourceId))
    }
    immutable.Map(viewers.toSeq: _*)
  }

  /**
   * Record the fact that userId has recently viewed resourceId on hostId. It is up to the implementation to expire
   * this event as appropriate.
   */
  def put(hostId: String, resourceId: String, userId: String) = {
    get(hostId, resourceId).put(userId, System.currentTimeMillis().toString)
  }

  /**
   * Record the fact that userId has stopped viewing resourceId on hostId.
   * <p>
   * There is no guarantee this will be called when the user stops viewing the resource. The implementation is
   * responsible for expiring viewers as appropriate.
   */
  def delete(hostId: String, resourceId: String, viewer: String) = {
    val heartBeats: mutable.Map[String, String] = get(hostId, resourceId)
    heartBeats.remove(viewer)
    if (heartBeats.isEmpty)
      store.remove((hostId, resourceId))
  }
}