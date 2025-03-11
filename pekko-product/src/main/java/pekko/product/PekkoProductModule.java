package pekko.product;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import pekko.product.api.v1.controller.BusinessUnitController;
import pekko.product.api.v1.controller.ProductController;
import pekko.product.api.v1.service.BusinessUnitService;
import pekko.product.api.v1.service.BusinessUnitServiceImpl;
import pekko.product.api.v1.service.ProductService;
import pekko.product.api.v1.service.ProductServiceImpl;
import play.Environment;

@Slf4j
public class PekkoProductModule extends AbstractModule {

    private final Environment environment;
    private final Config config;

    public PekkoProductModule(Environment environment, Config config){
        this.environment = environment;
        this.config = config;
    }

    @Override
    protected void configure() {
        log.info("hostname::{}, isProdEnv::{}", config.getString("pod.hostname"), environment.isProd());

        bind(PekkoClusterManagementSetup.class).asEagerSingleton();
        bind(PersistenceAndProjectionSetup.class).asEagerSingleton();

        bind(ProductController.class).asEagerSingleton();
        bind(BusinessUnitController.class).asEagerSingleton();
        bind(ProductService.class).to(ProductServiceImpl.class).asEagerSingleton();
        bind(BusinessUnitService.class).to(BusinessUnitServiceImpl.class).asEagerSingleton();
    }
}
