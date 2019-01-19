package com.dscjss.codingplatform.problems;

import com.dscjss.codingplatform.problems.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Integer> {
}
