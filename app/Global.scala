import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.settings.AcGlobalSettings
import play.api.{Application, Logger}
import utils.VersionUtils

object Global extends AcGlobalSettings with PlayAcConfigured {
  
  private lazy val prettyName = s"${acConfig.pluginName} (${acConfig.pluginKey}}) version ${VersionUtils.VERSION}"
  override def onStart(app: Application) {
    super.onStart(app)
    Logger.info(s"${prettyName} has started.")
  }
  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info(s"${prettyName} has stopped.")
  }
}
