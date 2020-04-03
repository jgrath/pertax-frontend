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

package controllers.auth

import com.google.inject.Inject
import controllers.auth.requests.AuthenticatedRequest
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.http.HeaderCarrier
import controllers.auth.CovidInterceptor._

import scala.concurrent.Future

private[auth] class CovidInterceptor @Inject()() {
  def interceptOnce[A](request: AuthenticatedRequest[A], defaultResult: Future[Result])(
    implicit hc: HeaderCarrier): Future[Result] =
    request.session.get(pageIntercepted) match {
      case None =>
        val result = Redirect(controllers.routes.InterstitialController.displayNationalInsurance())
        Future.successful(result.addingToSession(pageIntercepted -> "true")(request))
      case _ =>
        defaultResult
    }
}

object CovidInterceptor {
  val pageIntercepted = "pageIntercepted"
}
