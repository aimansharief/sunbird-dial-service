package app;

import com.google.inject.AbstractModule;

public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(AppStartup.class).asEagerSingleton();
    }

} 