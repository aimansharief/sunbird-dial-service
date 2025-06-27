package app;

import commons.AppConfig;
import managers.HealthCheckManager;
import telemetry.TelemetryGenerator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppStartup {
    @Inject
    public AppStartup() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        TelemetryGenerator.setComponent("dialcode-service");
        new HealthCheckManager().getAllServiceHealth();
    }
} 