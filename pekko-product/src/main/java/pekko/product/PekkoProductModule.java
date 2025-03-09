package pekko.product;

import com.google.inject.AbstractModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PekkoProductModule extends AbstractModule {
    @Override
    protected void configure() {
        log.info("configure");
        bind(PekkoClusterSetup.class).asEagerSingleton();
    }
}
