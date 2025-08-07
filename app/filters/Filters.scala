package filters

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
 
class Filters @Inject() (
  telemetryAccessFilter: TelemetryAccessFilter,
  healthCheckFilter: HealthCheckFilter
) extends DefaultHttpFilters(telemetryAccessFilter, healthCheckFilter) 