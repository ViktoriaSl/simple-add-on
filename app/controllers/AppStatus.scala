package controllers

import java.io.FileOutputStream
import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.auth.Token
import com.atlassian.connect.playscala.auth.jwt.JwtSignedAcHostWS
import com.atlassian.connect.playscala.controllers.ActionJwtValidator
import com.atlassian.connect.playscala.store.DefaultDbConfiguration
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller}
import service.{InMemoryHeartBeatService, ViewerIssueValue}
import utils.Constants.VALIDATED_SUCCESSFULLY
import java.util.Properties;

//action
object AppStatus extends Controller with JwtSignedAcHostWS with ActionJwtValidator with DefaultDbConfiguration
  with PlayAcConfigured  {

  def installed() = Action { implicit request =>
    val requestBodyJson: JsValue = request.body.asJson.get
    println(s"""requestBodyJson=`$requestBodyJson`""")
    persistOnInstalled(
      (requestBodyJson \ "clientKey").as[String],
      (requestBodyJson \ "publicKey").as[String],
      (requestBodyJson \ "sharedSecret").as[String],
      (requestBodyJson \ "baseUrl").as[String]
    )
    NoContent
  }
  
  def persistOnInstalled(clientKey:String,publicKey: String,sharedSecret: String,baseUrl:String): Unit ={
    val  prop = new Properties()
    val pr = new FileOutputStream("config.properties")
    prop.setProperty("clientKey",clientKey)
    prop.setProperty("publicKey",  publicKey)
    prop.setProperty("sharedSecret",sharedSecret)
    prop.setProperty("baseUrl", baseUrl)
// save properties to project root folder
    prop.store(pr, null)
  }
  
  
  def uninstalled() = Action { implicit request =>
    val requestBodyJson: JsValue = request.body.asJson.get
    println(s"""requestBodyJson=`$requestBodyJson`""")
    NoContent
  }
}
