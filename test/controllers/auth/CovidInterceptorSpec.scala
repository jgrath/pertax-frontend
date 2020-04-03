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

import controllers.auth.requests.AuthenticatedRequest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatestplus.play.OneAppPerSuite
import play.api.mvc.Request
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.auth.core.retrieve.Credentials
import uk.gov.hmrc.http.HeaderCarrier
import util.Fixtures

import scala.concurrent.{ExecutionContext, Future}

class CovidInterceptorSpec extends FreeSpec with MustMatchers with MockitoSugar with OneAppPerSuite with ScalaFutures {

  implicit val hc = HeaderCarrier()
  implicit val ec = app.injector.instanceOf[ExecutionContext]

  val testRequest = FakeRequest()

  def authenticatedRequest[A](request: Request[A]) = AuthenticatedRequest[A](
    Some(Fixtures.fakeNino),
    None,
    Credentials("foo", "bar"),
    ConfidenceLevel.L200,
    None,
    None,
    None,
    Set.empty,
    request
  )

  val covidInterceptor = new CovidInterceptor
  val defaultResult = Future.successful(Ok)

  "interceptOnce" - {
    "return the defaultResult if the session key pageIntercepted has been set to true" in {
      val testRequest = FakeRequest().withSession(CovidInterceptor.pageIntercepted -> "true")

      val result = covidInterceptor.interceptOnce(authenticatedRequest(testRequest), defaultResult)
      result mustBe defaultResult
    }

    "should redirect to the covid page" in {
      val result = covidInterceptor.interceptOnce(authenticatedRequest(testRequest), defaultResult)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.CovidInterceptController.index().url)
    }

    "should redirect once" in {
      for {
        first  <- covidInterceptor.interceptOnce(authenticatedRequest(testRequest), defaultResult)
        second <- covidInterceptor.interceptOnce(authenticatedRequest(testRequest), defaultResult)
      } yield {
        first.header.status mustBe SEE_OTHER
        second.header.status mustBe Ok
      }
    }
  }
}
