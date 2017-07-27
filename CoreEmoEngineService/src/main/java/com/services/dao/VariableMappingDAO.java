package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.VariableMapping;

public interface VariableMappingDAO extends JpaRepository<VariableMapping, Long>, JpaSpecificationExecutor<VariableMapping>{
	
	@Query("from VariableMapping vm where vm.variableId = :variableId")
	public VariableMapping getMappingByVariableId(@Param("variableId") String variableId);
	
	@Query("from VariableMapping vm where vm.variableId In (:vars)")
	public List<VariableMapping> getMappingsForVariables(@Param("vars") String vars);

}
