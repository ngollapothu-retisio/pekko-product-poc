package pekko.product;

import com.google.inject.AbstractModule;
import lombok.extern.slf4j.Slf4j;
import pekko.product.api.v1.service.ProductService;
import pekko.product.api.v1.service.ProductServiceImpl;

@Slf4j
public class PekkoProductModule extends AbstractModule {
    @Override
    protected void configure() {
        log.info("configure");
        bind(PekkoClusterSetup.class).asEagerSingleton();
        bind(ProductService.class).to(ProductServiceImpl.class).asEagerSingleton();
    }
}
