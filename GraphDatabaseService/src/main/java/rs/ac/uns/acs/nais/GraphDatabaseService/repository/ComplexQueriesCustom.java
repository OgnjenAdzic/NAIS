package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.CategoryStatsDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserSegmentDTO;

import java.util.List;

public interface ComplexQueriesCustom {
    List<CategoryStatsDTO> analyzeCategoryPerformance(Integer minPurchases);
    List<UserSegmentDTO> segmentUsersByMinSpent(Double minSpent);
}
