package com.dscjss.codingplatform.compilers;

import com.dscjss.codingplatform.compilers.model.Compiler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompilerRepository extends JpaRepository<Compiler, Integer> {

}
