package com.example.liferayuserfactory.repository;

import com.example.liferayuserfactory.model.LiferayUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LiferayUserRepository extends CrudRepository<LiferayUser, Long> {

    boolean existsByEmailAddressIgnoreCase(String emailAddress);

    Optional<LiferayUser> findByEmailAddressIgnoreCase(String emailAddress);

    Iterable<LiferayUser> findByEmailAddressIgnoreCaseIn(Iterable<String> emailAddresses);
}
