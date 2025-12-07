package rs.ac.uns.acs.nais.GraphDatabaseService.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD.BrandRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD.CategoryRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD.SportProductRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GraphSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SportProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println(">>> STARTING GRAPH DATA SEEDING...");

        // 1. CLEANUP (Delete existing data to start fresh)
        userRepository.deleteAll();
        productRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();

        // =================================================================================
        // STEP 1: CREATE CATEGORIES (Node Type 1)
        // =================================================================================
        Category catRunning = new Category(null, "Running", "All things running", null, null, null);
        Category catGym = new Category(null, "Gym", "Weightlifting and cardio", null, null, null);
        Category catSwimming = new Category(null, "Swimming", "Pool equipment", null, null, null);

        categoryRepository.saveAll(Arrays.asList(catRunning, catGym, catSwimming));

        // =================================================================================
        // STEP 2: CREATE BRANDS (Node Type 2)
        // =================================================================================
        Brand nike = new Brand(null, "Nike", "Just Do It", "USA", true, null);
        Brand adidas = new Brand(null, "Adidas", "Impossible Is Nothing", "Germany", false, null);
        Brand speedo = new Brand(null, "Speedo", "Swimwear", "Australia", false, null);

        brandRepository.saveAll(Arrays.asList(nike, adidas, speedo));

        // =================================================================================
        // STEP 3: CREATE PRODUCTS (Node Type 3)
        // =================================================================================

        // --- RUNNING PRODUCTS ---
        SportProduct pAlphaFly = new SportProduct("Nike AlphaFly", "Elite running shoes", 250.0, "Nike", "Running", "Shoes", 50, "42", "Green", "Unisex");
        pAlphaFly.setBrandRelation(nike);
        pAlphaFly.setCategoryRelation(catRunning);

        SportProduct pUltraBoost = new SportProduct("Adidas UltraBoost", "Daily trainer", 180.0, "Adidas", "Running", "Shoes", 100, "43", "Black", "Male");
        pUltraBoost.setBrandRelation(adidas);
        pUltraBoost.setCategoryRelation(catRunning);

        SportProduct pNikeShorts = new SportProduct("Nike Pro Shorts", "Running apparel", 45.0, "Nike", "Running", "Apparel", 200, "M", "Black", "Male");
        pNikeShorts.setBrandRelation(nike);
        pNikeShorts.setCategoryRelation(catRunning);

        // --- GYM PRODUCTS ---
        SportProduct pDumbbells = new SportProduct("Hex Dumbbells", "20kg pair", 90.0, "Adidas", "Gym", "Equipment", 30, "N/A", "Black", "N/A");
        pDumbbells.setBrandRelation(adidas);
        pDumbbells.setCategoryRelation(catGym);

        // --- SWIMMING PRODUCTS ---
        SportProduct pGoggles = new SportProduct("Speedo Goggles", "Anti-fog", 25.0, "Speedo", "Swimming", "Accessories", 150, "OneSize", "Blue", "Unisex");
        pGoggles.setBrandRelation(speedo);
        pGoggles.setCategoryRelation(catSwimming);

        productRepository.saveAll(Arrays.asList(pAlphaFly, pUltraBoost, pNikeShorts, pDumbbells, pGoggles));

        // =================================================================================
        // STEP 4: CREATE USERS (Node Type 4)
        // =================================================================================

        // 1. "Rich Runner" - High spender (VIP_GOLD), loves running
        User userRich = new User(null, 1L, "rich_runner", "rich@test.com", "Rich", "Guy", "Running", "Advanced", null, null, null, null, null);

        // 2. "Average Joe" - Medium spender (VIP_SILVER), likes running too
        User userJoe = new User(null, 2L, "avg_joe", "joe@test.com", "Joe", "Average", "Running", "Intermediate", null, null, null, null, null);

        // 3. "Newbie" - Low spender (STANDARD), likes Gym
        User userNewbie = new User(null,3L, "newbie_jim", "jim@test.com", "Jim", "New", "Gym", "Beginner", null, null, null, null, null);

        // =================================================================================
        // STEP 5: CREATE INTERACTIONS (Relationships)
        // =================================================================================

        // --- Scenario for Collaborative Filtering ---
        // Rich and Joe both bought "Nike AlphaFly".
        // Rich ALSO bought "Adidas UltraBoost".
        // Query should recommend "Adidas UltraBoost" to Joe because he is similar to Rich.

        // Rich's Purchases (Total > $1000)
        PurchaseRelationship pr1 = new PurchaseRelationship(pAlphaFly, LocalDateTime.now().minusDays(10), 2, 500.0, "COMPLETED");
        PurchaseRelationship pr2 = new PurchaseRelationship(pUltraBoost, LocalDateTime.now().minusDays(5), 2, 360.0, "COMPLETED");
        PurchaseRelationship pr3 = new PurchaseRelationship(pNikeShorts, LocalDateTime.now().minusDays(2), 4, 180.0, "COMPLETED");
        userRich.setPurchasedProducts(Set.of(pr1, pr2, pr3));

        // Joe's Purchases (Total > $200 but < $500)
        PurchaseRelationship pj1 = new PurchaseRelationship(pAlphaFly, LocalDateTime.now().minusDays(8), 1, 250.0, "COMPLETED");
        userJoe.setPurchasedProducts(Set.of(pj1));
        // Joe has NOT bought UltraBoost yet -> This is the target for recommendation!

        // Newbie's Purchases (Low spender)
        PurchaseRelationship pn1 = new PurchaseRelationship(pDumbbells, LocalDateTime.now().minusDays(1), 1, 90.0, "COMPLETED");
        userNewbie.setPurchasedProducts(Set.of(pn1));

        // --- Scenario for Reviews (Avg Rating Calculation) ---
        // Rich loves AlphaFly
        Review rev1 = new Review(pAlphaFly, 5, "Best shoes ever!", LocalDateTime.now(), true);
        userRich.setReviews(Set.of(rev1));
        // Update product rating manually for seeding consistency
        pAlphaFly.setRating(5.0);

        // Joe thinks AlphaFly is okay
        Review rev2 = new Review(pAlphaFly, 3, "Too expensive", LocalDateTime.now(), true);
        userJoe.setReviews(Set.of(rev2));
        // Average should become 4.0 later

        // Save Users (cascades relationships)
        userRepository.saveAll(Arrays.asList(userRich, userJoe, userNewbie));

        // Save product rating update
        productRepository.save(pAlphaFly);

        System.out.println(">>> GRAPH DATA SEEDING COMPLETED SUCCESSFULLY!");
        System.out.println(">>> Ready to test Complex Queries.");
    }
}