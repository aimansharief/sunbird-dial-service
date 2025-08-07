package app;

import javax.inject.Inject;
import javax.inject.Singleton;
import managers.HealthCheckManager;
import play.api.Environment;
import play.api.inject.ApplicationLifecycle;
import telemetry.TelemetryGenerator;

@Singleton
public class AppStartup {
    @Inject
    public AppStartup(ApplicationLifecycle lifecycle, Environment environment) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        TelemetryGenerator.setComponent("dialcode-service");
        HealthCheckManager healthCheckManager = new HealthCheckManager();
        healthCheckManager.getAllServiceHealth();
    }
} 