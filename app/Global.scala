import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.settings.AcGlobalSettings
import play.api.{ Logger, Application }
import utils.VersionUtils

object Global extends AcGlobalSettings with PlayAcConfigured {

  override def onStart(app: Application) {
    super.onStart(app)
    Logger.info(s"${prettyName} has started.")
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info(s"${prettyName} has stopped.")
  }

  private lazy val prettyName = s"${acConfig.pluginName} (${acConfig.pluginKey}}) version ${VersionUtils.VERSION}"
}
