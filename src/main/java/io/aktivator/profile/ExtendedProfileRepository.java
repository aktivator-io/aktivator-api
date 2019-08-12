package io.aktivator.profile;

import io.aktivator.profile.entities.ExtendedProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExtendedProfileRepository extends CrudRepository<ExtendedProfile, Long> {
    Optional<ExtendedProfile> findByOwnerId(String ownerId);
}
