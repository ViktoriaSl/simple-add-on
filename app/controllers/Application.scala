package controllers

import com.atlassian.connect.playscala.PlayAcConfigured
import com.atlassian.connect.playscala.controllers.AcController
import com.atlassian.connect.playscala.store.DefaultDbConfiguration

object Application extends AcController with DefaultDbConfiguration with PlayAcConfigured {
  
  def index() = acIndexBuilder home { request =>
    Ok(views.html.home())
  } build()
}