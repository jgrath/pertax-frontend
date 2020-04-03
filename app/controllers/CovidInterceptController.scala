/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import com.google.inject.Inject
import config.ConfigDecorator
import controllers.auth.CovidInterceptor.{interceptorContinueUrl, pageIntercepted}
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.renderer.TemplateRenderer
import util.LocalPartialRetriever

import scala.concurrent.Future

class CovidInterceptController @Inject()(val messagesApi: MessagesApi)(
  implicit partialRetriever: LocalPartialRetriever,
  configDecorator: ConfigDecorator,
  templateRenderer: TemplateRenderer)
    extends PertaxBaseController {

  def index: Action[AnyContent] = Action.async { implicit request =>
    request.session.get(interceptorContinueUrl) match {
      case Some(uri) => Future.successful(Ok(views.html.covid_help(configDecorator.pertaxFrontendHost + uri)))
      case _ =>
        Logger.warn("No interceptorContinueUrl found")
        Future.successful(Ok(views.html.covid_help(configDecorator.pertaxFrontendHomeUrl)))
    }
  }
}
