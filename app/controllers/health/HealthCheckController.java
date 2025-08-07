/**
 *
 * @author Rhea Fernandes
 */
package controllers.health;

import javax.inject.Inject;
import javax.inject.Singleton;
import commons.dto.Response;
import controllers.BaseController;
import managers.HealthCheckManager;
import play.mvc.Result;
import telemetry.TelemetryManager;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

@Singleton
public class HealthCheckController extends BaseController {
    private final HealthCheckManager healthCheckManager;
    private  String apiId = "sunbird.dialcode.health";

    @Inject
    public HealthCheckController(HealthCheckManager healthCheckManager) {
        this.healthCheckManager = healthCheckManager;
    }

    public CompletionStage<Result> checkSystemHealth(){
        try {
            Response response= healthCheckManager.getAllServiceHealth();
            return getResponseEntity(response, apiId, null);
        }catch (Exception e){
            e.printStackTrace();
            TelemetryManager.error("System is Unhealthy, Restart needed",e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }

    public CompletionStage<Result> checkServiceHealth() {
        Response response = healthCheckManager.getServiceHealth();
        return getResponseEntity(response, apiId, null);
    }

}
