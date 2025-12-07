package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.User;

@Data
@NoArgsConstructor
public class UserCreateRequestDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String preferredSport;
    private String fitnessLevel;

    // Helper method to convert this DTO to an Entity
    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setPreferredSport(this.preferredSport);
        user.setFitnessLevel(this.fitnessLevel);
        // Note: We do NOT set id or userId here. The Service handles that.
        return user;
    }
}