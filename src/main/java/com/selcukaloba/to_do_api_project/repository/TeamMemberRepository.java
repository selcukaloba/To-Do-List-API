package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.Team;
import com.selcukaloba.to_do_api_project.entity.TeamMember;
import com.selcukaloba.to_do_api_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    boolean existsByTeamAndUser(Team team, User user);
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
    List<TeamMember> findByUser(User user);
}
