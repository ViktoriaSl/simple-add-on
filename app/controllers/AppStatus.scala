package controllers

//import com.atlassian.connect.playscala.PlayAcConfigured
//import com.atlassian.connect.playscala.auth.Token
//import com.atlassian.connect.playscala.controllers.ActionJwtValidator
//import com.atlassian.connect.playscala.store.DefaultDbConfiguration
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller}
import service.{InMemoryHeartBeatService, ViewerIssueValue}
import utils.Constants.VALIDATED_SUCCESSFULLY

//action
//object AppStatus extends Controller with ViewerIssueValue /*with ActionJwtValidator with DefaultDbConfiguration
//  with PlayAcConfigured*/ {
//
//  def installed() = Action { implicit request =>
//    val requestBodyJson: JsValue = request.body.asJson.get
//    println(s"""requestBodyJson=`$requestBodyJson`""")
//  val clientKey =  (requestBodyJson \ "clientKey").as[String]
////    InMemoryHeartBeatService.put(hostId, resourceId, userId)
////    jcloudAuthPersistence.persistOnInstalled(
////      (requestBodyJson \ "clientKey").get.as[String],
////      (requestBodyJson \ "publicKey").get.as[String],
////      (requestBodyJson \ "sharedSecret").get.as[String],
////      (requestBodyJson \ "baseUrl").get.as[String]
////    )
//    NoContent
//  }
//}
