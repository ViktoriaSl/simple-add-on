package controllers

import java.io.FileInputStream
import java.net.URL
import com.atlassian.jwt.SigningAlgorithm
import com.atlassian.jwt.core.writer.{JsonSmartJwtJsonBuilder, JwtClaimsBuilder, NimbusJwtWriterFactory}
import com.atlassian.jwt.httpclient.CanonicalHttpUriRequest
import com.atlassian.jwt.writer.{JwtJsonBuilder, JwtWriterFactory}
import java.util.Properties
import service.ViewerIssueValue

object TokenCreation extends App{
  def tokenRequest(issue: String): String ={
   val prop = new Properties()
    val file =  new FileInputStream("config.properties")
    prop.load(file)
  
    val issuedAt: Long = System.currentTimeMillis
    val expiresAt: Long = issuedAt + 180L
    val key: String = "field-validation-addon"
    val sharedSecret: String = "jira:9270c970-2630-41ca-8295-887d76d16f46"
    val method: String = "GET"
    val baseUrl: String =prop.getProperty("baseUrl")
    val contextPath: String = "/"
    val apiPath: String = s"/rest/api/2/issue/$issue"
    val jwtBuilder: JwtJsonBuilder
    = new JsonSmartJwtJsonBuilder().issuedAt(issuedAt).expirationTime(expiresAt).issuer(
      key)
    val canonical: CanonicalHttpUriRequest = new CanonicalHttpUriRequest(method, apiPath,
      contextPath, new java.util.HashMap())
    JwtClaimsBuilder.appendHttpRequestClaims(jwtBuilder, canonical)
    val jwtWriterFactory: JwtWriterFactory = new NimbusJwtWriterFactory
    val jwtbuilt: String = jwtBuilder.build
    val jwtToken: String = jwtWriterFactory.macSigningWriter(SigningAlgorithm.HS256, sharedSecret).jsonToJwt(jwtbuilt)
   baseUrl + apiPath + "?jwt=" + jwtToken
  }
  println(tokenRequest("SIM-5"))
}
