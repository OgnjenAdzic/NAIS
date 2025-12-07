package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.CategoryStatsDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserSegmentDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.SportProduct;

import java.util.List;

public interface IGraphService {
    List<SportProduct> getRecommendations(Long userId);
    List<UserSegmentDTO> getUserSegments();
    List<SportProduct> getCrossSell(Long userId);
    void buyProduct(Long userId, Long productId, int qty);
    void reviewProduct(Long userId, Long productId, int rating, String comment);
    List<CategoryStatsDTO> analyzeCategoryPerformance(Integer minPurchases);
    List<UserSegmentDTO> segmentUsersByMinSpent(Double minSpent);
}