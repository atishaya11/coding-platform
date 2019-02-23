package com.dscjss.codingplatform.contests;

import com.dscjss.codingplatform.contests.dto.Row;
import com.dscjss.codingplatform.contests.model.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Integer>, PagingAndSortingRepository<RegisteredUser, Integer> {

    Page<RegisteredUser> findByContestCode(String code, Pageable pageable);

    boolean existsByUserIdAndContestId(int id, int contestId);

    boolean existsByUserUsernameAndContestId(String username, int contestId);

    RegisteredUser findByContestIdAndUserUsername(int contestId, String username);

}
