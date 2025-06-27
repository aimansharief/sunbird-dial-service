/**
 *
 * @author Rhea Fernandes
 */
package controllers.health;

import commons.dto.Response;
import controllers.BaseController;
import managers.HealthCheckManager;
import play.mvc.Result;
import telemetry.TelemetryManager;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

public class HealthCheckController extends BaseController {
    private HealthCheckManager healthCheckManager = new HealthCheckManager();
    private  String apiId = "sunbird.dialcode.health";

    public CompletionStage<Result> checkSystemHealth(){
        return CompletableFuture.supplyAsync(() -> ok("System Health OK"));
    }

    public CompletionStage<Result> checkServiceHealth() {
        return CompletableFuture.supplyAsync(() -> ok("Service Health OK"));
    }

}
