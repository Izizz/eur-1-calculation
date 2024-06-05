package com.example.eurcertificatecalc.auth.data.repository;

import com.example.eurcertificatecalc.auth.data.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("update User u set u.username =?1 , u.password = ?2 where u.id =?3")
    void updateUser(String username, String password, Long id);

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(String username);

}
