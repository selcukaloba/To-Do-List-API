package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT DISTINCT t FROM Team t " +
            "WHERE t.leader.username = :username " +
            "OR EXISTS (SELECT tm FROM TeamMember tm WHERE tm.team = t AND tm.user.username = :username)")
    List<Team> findAllByUser(@Param("username") String username);

    Optional<Team> findById(Long teamId);
    boolean existsByNameAndLeaderUsername(String name, String leaderUsername);
}
