package com.honey.shop.config;

import com.honey.shop.domain.Category;
import com.honey.shop.domain.Product;
import com.honey.shop.repository.CategoryRepository;
import com.honey.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Loads sample categories and products when running with profile "dev".
 * Runs only if no categories exist yet.
 */
@Component
@Profile("dev")
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class SampleDataLoader implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (categoryRepository.count() > 0) {
            log.debug("Sample data already present, skipping loader");
            return;
        }

        log.info("Loading sample data for development");

        Category forest = categoryRepository.save(Category.builder()
                .name("Forest")
                .slug("forest")
                .description("Dark, rich honey from forest blossoms and honeydew")
                .build());

        Category acacia = categoryRepository.save(Category.builder()
                .name("Ливадски")
                .slug("meadow")
                .description("Light, mild meadow honey")
                .build());

        List<Product> products = List.of(
                Product.builder()
                        .name("Планински мед 950г")
                        .description("Планински интензивен мед, богат со минерали")
                        .price(new BigDecimal("600"))
                        .stock(50)
                        .category(forest)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Ливадски Мед 950г")
                        .description("Нежен и ароматичен, со природна жолта боја")
                        .price(new BigDecimal("600"))
                        .stock(30)
                        .category(forest)
                        .active(true)
                        .build()
        );

        productRepository.saveAll(products);
        log.info("Sample data loaded: {} categories, {} products",
                categoryRepository.count(), productRepository.count());
    }
}
