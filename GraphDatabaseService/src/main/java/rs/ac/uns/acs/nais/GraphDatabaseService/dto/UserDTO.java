package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String preferredSport;
    private String fitnessLevel;

    public UserDTO(Map<String, Object> map) {
        this.id = safeLong(map.get("userId"));
        this.username = (String) map.get("username");
        this.email = (String) map.get("email");
        this.firstName = (String) map.get("firstName");
        this.lastName = (String) map.get("lastName");
        this.preferredSport = (String) map.get("preferredSport");
        this.fitnessLevel = (String) map.get("fitnessLevel");
    }

    private Long safeLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        return null;
    }
}