/**
  *
  * @author Rhea Fernandes
  */
package filters;

import commons.DialCodeErrorMessage
import commons.exception.{ResponseCode, ServiceUnavailableException}
import controllers.BaseController
import play.api.mvc._
import akka.stream.Materializer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import play.core.j.JavaHelpers
import play.core.j.JavaContextComponents
import javax.inject.Inject

class HealthCheckFilter @Inject()( javaContextComponents: JavaContextComponents, baseController: BaseController)(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    if (!requestHeader.path.contains("/health")) {
      if (!managers.HealthCheckManager.health) {
        val jContext = JavaHelpers.createJavaContext(requestHeader, javaContextComponents)
        val jResult:play.mvc.Result = baseController.getServiceUnavailableResponseEntity(new ServiceUnavailableException(ResponseCode.SERVICE_UNAVAILABLE.code().toString,DialCodeErrorMessage.ERR_SERVICE_UNAVAILABLE),"sunbird.dialcode.exception",null)
        Future{
          JavaHelpers.createResult(jContext,jResult);
        }
      } else {
        nextFilter(requestHeader)
      }
    } else {
      nextFilter(requestHeader)
    }
  }
}
