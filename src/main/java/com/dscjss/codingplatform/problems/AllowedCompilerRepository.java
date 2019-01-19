package com.dscjss.codingplatform.problems;

import com.dscjss.codingplatform.compilers.model.Compiler;
import com.dscjss.codingplatform.problems.model.AllowedCompiler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllowedCompilerRepository extends JpaRepository<AllowedCompiler, Integer> {

}
