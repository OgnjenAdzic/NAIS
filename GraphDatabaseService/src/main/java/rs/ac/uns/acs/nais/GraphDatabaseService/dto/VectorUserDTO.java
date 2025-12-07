package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VectorUserDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("preferred_sport")
    private String preferredSport;

    @JsonProperty("fitness_level")
    private String fitnessLevel;
}