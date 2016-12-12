package utils

import java.util.concurrent.TimeUnit

object Constants {

  // A viewer is considered to not be looking anymore if no heartbeat has been received in this amount for time.
  val VIEWER_EXPIRY_SECONDS = "whoslooking.viewer-expiry.seconds"
  val VIEWER_EXPIRY_SECONDS_DEFAULT = TimeUnit.SECONDS.toSeconds(15).asInstanceOf[Int]

  // A viewer set associated with an issue is purged if no one has requested it for this amount of time.
  val VIEWER_SET_EXPIRY_SECONDS = "whoslooking.viewer-set-expiry.seconds"
  val VIEWER_SET_EXPIRY_SECONDS_DEFAULT = TimeUnit.DAYS.toSeconds(1).asInstanceOf[Int]

  val DISPLAY_NAME_CACHE_EXPIRY_SECONDS = "whoslooking.display-name-cache-expiry.seconds"
  val DISPLAY_NAME_CACHE_EXPIRY_SECONDS_DEFAULT = TimeUnit.DAYS.toSeconds(1).asInstanceOf[Int]

  val POLLER_INTERVAL_SECONDS = "whoslooking.poller-interval.seconds"
  val POLLER_INTERVAL_SECONDS_DEFAULT = 10

  val AVATAR_SIZE_PX = "whoslooking.avatar-size"
  val AVATAR_SIZE_PX_DEFAULT = 24

  val KEY_SEPARATOR = "#"
  
  val NOTHING_TO_VALIDATE = "No positive number field. Please set one for validation! "
  val VALIDATED_SUCCESSFULLY = "Correct number! "
  val VALIDATED_FAILURE = "Validation wasn't success. Please set positive number !"
  val CUSTOM_FIELD = "com.atlassian.plugins.atlassian-connect-plugin:my-addon__positive-number-field"
}
