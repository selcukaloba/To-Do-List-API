package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.Team;
import com.selcukaloba.to_do_api_project.entity.TeamMember;
import com.selcukaloba.to_do_api_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    boolean existsByTeamAndUser(Team team, User user);
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
    List<TeamMember> findByUser(User user);
    void deleteAll(Iterable<? extends TeamMember> entities);
    @Modifying
    @Query("DELETE FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.user.username = :username")
    void deleteByTeamIdAndUsername(@Param("teamId") Long teamId, @Param("username") String username);
}
