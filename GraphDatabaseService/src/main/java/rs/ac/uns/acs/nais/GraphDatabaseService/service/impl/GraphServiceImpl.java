package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.CategoryStatsDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserSegmentDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.SportProduct;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.ComplexQueriesRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IGraphService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphServiceImpl implements IGraphService {

    private final ComplexQueriesRepository complexRepo;

    // 1. Recommendation
    @Override
    public List<SportProduct> getRecommendations(Long userId) {
        return complexRepo.recommendCollaborative(userId, 5);
    }

    // 2. Segmentation
    @Override
    @Transactional(readOnly = true)
    public List<UserSegmentDTO> getUserSegments() {
        return complexRepo.segmentUsersByMinSpent(0.0);
    }

    // 3. Cross-Sell
    @Override
    public List<SportProduct> getCrossSell(Long userId) {
        return complexRepo.recommendCrossSell(userId, 5);
    }

    // 4. Complex CRUD: Purchase
    @Transactional
    public void buyProduct(Long userId, Long productId, int qty) {
        Boolean success = complexRepo.executeComplexPurchase(userId, productId, qty);

        if (success == null || !success) {
            throw new IllegalArgumentException("Purchase failed: Not enough stock or invalid product/user ID.");
        }
    }

    // 5. Complex CRUD: Review
    @Transactional
    public void reviewProduct(Long userId, Long productId, int rating, String comment) {
        complexRepo.executeComplexReview(userId, productId, rating, comment);
    }

    //6. Category Performance
    @Override
    @Transactional(readOnly = true)
    public List<CategoryStatsDTO> analyzeCategoryPerformance(Integer minPurchases) {
        return complexRepo.analyzeCategoryPerformance(minPurchases);
    }

    @Override
    public List<UserSegmentDTO> segmentUsersByMinSpent(Double minSpent) {
        double threshold = (minSpent != null) ? minSpent : 0.0;
        return complexRepo.segmentUsersByMinSpent(threshold);
    }

}