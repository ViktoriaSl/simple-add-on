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
//
//object RedisHeartbeatService extends HeartBeatService {
//
//  def list(hostId: String, resourceId: String): immutable.Map[String, String] = {
//    val resourceKey: String = buildViewerSetKey(hostId, resourceId)
//    Logger.debug(s"listing viewers for ${resourceKey}")
//    sedisPool.withClient { r =>
//      val allUserIds: Set[String] = r.smembers(resourceKey)
//      r.expire(resourceKey, ViewerSetExpirySeconds)
//      val setOfExpiredOrStillSeen: Set[String \/ (String, String)] = allUserIds.map {
//        userId =>
//          val heartbeatKey = buildHeartbeatKey(hostId, resourceId, userId)
//          Logger.debug(s"retrieving heartbeat for ${heartbeatKey}")
//          r.get(heartbeatKey).map(userId -> _) \/> userId
//      }
//      setOfExpiredOrStillSeen.collect {
//        case -\/(expiredUserId) => expiredUserId
//      }.foreach(r.srem(resourceKey, _))
//      setOfExpiredOrStillSeen.collect {
//        case \/-(b) => b
//      }.toMap
//    }
//  }
//
//  def put(hostId: String, resourceId: String, userId: String): Unit = {
//    val heartbeatKey: String = buildHeartbeatKey(hostId, resourceId, userId)
//    val resourceKey: String = buildViewerSetKey(hostId, resourceId)
//    Logger.debug(s"updating heartbeat for ${heartbeatKey}")
//    sedisPool.withJedisClient { j =>
//      val t: Transaction = j.multi()
//      t.sadd(resourceKey, userId)
//      t.set(heartbeatKey, String.valueOf(System.currentTimeMillis))
//      t.expire(heartbeatKey, ViewerExpirySeconds)
//      t.expire(resourceKey, ViewerSetExpirySeconds)
//      t.exec
//    }
//  }
//
//  def delete(hostId: String, resourceId: String, viewer: String): Unit = {
//    val heartbeatKey: String = buildHeartbeatKey(hostId, resourceId, viewer)
//    Logger.debug(s"removing heartbeat for ${heartbeatKey}")
//    sedisPool.withJedisClient { j =>
//      j.del(heartbeatKey)
//    }
//  }
//}