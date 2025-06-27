package filters

import javax.inject.Inject
import play.api.mvc._
import play.api.Logger
import akka.stream.Materializer
import scala.concurrent.{ExecutionContext, Future}
import commons.AppConfig
import commons.dto.{ExecutionContext => Ctx}
import commons.dto.HeaderParam
import org.apache.commons.lang3.StringUtils
import telemetry.TelemetryAccessEventUtil
import scala.collection.JavaConverters._

class TelemetryAccessFilter @Inject()(
  implicit val mat: Materializer, ec: ExecutionContext
) extends Filter {

  private val logger = Logger("accesslog")

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      try {
        val path = requestHeader.path
        if (!path.contains("/health")) {
          val data = scala.collection.mutable.Map[String, Object](
            "StartTime" -> Long.box(startTime),
            "RemoteAddress" -> requestHeader.remoteAddress,
            "Method" -> requestHeader.method,
            "path" -> path,
            "Protocol" -> (if (requestHeader.secure) "HTTPS" else "HTTP"),
            "env" -> "dialcode"
          )
          val sessionId = requestHeader.headers.get("X-Session-ID").orNull
          val consumerId = requestHeader.headers.get("X-Consumer-ID").orNull
          val deviceId = requestHeader.headers.get("X-Device-ID").orNull
          val authUserId = requestHeader.headers.get("X-Authenticated-Userid").orNull
          val channelId = requestHeader.headers.get("X-Channel-ID").orNull
          val appId = requestHeader.headers.get("X-APP-ID").orNull
          data += ("X-Session-ID" -> sessionId)
          data += ("X-Consumer-ID" -> consumerId)
          data += ("X-Device-ID" -> deviceId)
          data += ("X-Authenticated-Userid" -> authUserId)
          data += (HeaderParam.APP_ID.name() -> appId)
          if (StringUtils.isNotBlank(deviceId))
            Ctx.getCurrent().getGlobalContext().put(HeaderParam.DEVICE_ID.name(), deviceId)
          if (StringUtils.isNotBlank(consumerId))
            Ctx.getCurrent().getGlobalContext().put(HeaderParam.CONSUMER_ID.name(), consumerId)
          if (StringUtils.isNotBlank(channelId))
            Ctx.getCurrent().getGlobalContext().put(HeaderParam.CHANNEL_ID.name(), channelId)
          else
            Ctx.getCurrent().getGlobalContext().put(HeaderParam.CHANNEL_ID.name(), AppConfig.config.getString("channel.default"))
          if (StringUtils.isNotBlank(appId))
            Ctx.getCurrent().getGlobalContext().put(HeaderParam.APP_ID.name(), channelId)
          TelemetryAccessEventUtil.writeTelemetryEventLog(data.toMap.asJava)
          logger.info(s"${requestHeader.remoteAddress} ${requestHeader.host} ${requestHeader.method} ${requestHeader.uri} ${result.header.status}")
        }
      } catch {
        case e: Exception => logger.error(e.getMessage)
      }
      result
    }
  }
} 