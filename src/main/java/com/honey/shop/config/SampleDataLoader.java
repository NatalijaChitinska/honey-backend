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
                .name("Acacia")
                .slug("acacia")
                .description("Light, mild honey from acacia flowers")
                .build());

        Category chestnut = categoryRepository.save(Category.builder()
                .name("Chestnut")
                .slug("chestnut")
                .description("Strong, distinctive honey from chestnut trees")
                .build());

        List<Product> products = List.of(
                Product.builder()
                        .name("Forest Honey 250g")
                        .description("Pure forest honey with a deep, woody flavour. Ideal for tea and baking.")
                        .price(new BigDecimal("8.50"))
                        .stock(50)
                        .category(forest)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Forest Honey 500g")
                        .description("Our best-selling forest honey in a larger jar.")
                        .price(new BigDecimal("15.00"))
                        .stock(30)
                        .category(forest)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Acacia Honey 250g")
                        .description("Light and delicate acacia honey, perfect for sweetening without overpowering.")
                        .price(new BigDecimal("9.00"))
                        .stock(40)
                        .category(acacia)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Acacia Honey 500g")
                        .description("Premium acacia honey from selected sources.")
                        .price(new BigDecimal("16.50"))
                        .stock(25)
                        .category(acacia)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Chestnut Honey 250g")
                        .description("Bold chestnut honey with a slightly bitter note. Great with cheese.")
                        .price(new BigDecimal("10.00"))
                        .stock(35)
                        .category(chestnut)
                        .active(true)
                        .build(),
                Product.builder()
                        .name("Chestnut Honey 500g")
                        .description("Intense chestnut honey for true connoisseurs.")
                        .price(new BigDecimal("18.00"))
                        .stock(20)
                        .category(chestnut)
                        .active(true)
                        .build()
        );

        productRepository.saveAll(products);
        log.info("Sample data loaded: {} categories, {} products",
                categoryRepository.count(), productRepository.count());
    }
}
