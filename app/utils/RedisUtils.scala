package utils

import org.sedis.Pool
import com.typesafe.plugin.RedisPlugin

object RedisUtils {
  lazy val sedisPool: Pool = play.api.Play.current.plugin(classOf[RedisPlugin]).
    getOrElse(throw new IllegalStateException("Redis plugin not available")).sedisPool
}
