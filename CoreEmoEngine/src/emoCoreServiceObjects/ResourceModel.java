package emoCoreServiceObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="model_resources")
public class ResourceModel {
	
	@Column(name="simulation_id")
	Long simId;
	
	@Column(name="agent_id")
	Long agentId;
	
	@Column(name="user_id")
	String userId;
	
	@Column(name="variable_to_model")
	String varToModel;
	
	@Column(name="source_agent")
	Long sourceAgent;
	
	@Column(name="target_agent")
	Long targetAgent;
	
	@Column(name="target_event")
	Long targetEvent;
	
	@Column(name="target_object")
	Long targetObject;
	
	@Column(name="target_emotion")
	String targetEmotion;
	
	@Column(name="target_variable")
	String targetVariable;
	
	@Column(name="is_interaction")
	Boolean isInteraction;
	
	@Column(name="is_function")
	Boolean isFunction;
	
	@Column(name="class_path")
	String classPath;
	
	@Column(name="method_name")
	String methodName;
	
	@Column(name="return_type")
	String returnType;
	
	@Column(name="file_path")
	String filePath;
	
	@Column(name="tab_name")
	String tabName;

	public Long getSimId() {
		return simId;
	}

	public void setSimId(Long simId) {
		this.simId = simId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVarToModel() {
		return varToModel;
	}

	public void setVarToModel(String varToModel) {
		this.varToModel = varToModel;
	}

	public Long getSourceAgent() {
		return sourceAgent;
	}

	public void setSourceAgent(Long sourceAgent) {
		this.sourceAgent = sourceAgent;
	}

	public Long getTargetAgent() {
		return targetAgent;
	}

	public void setTargetAgent(Long targetAgent) {
		this.targetAgent = targetAgent;
	}

	public Long getTargetEvent() {
		return targetEvent;
	}

	public void setTargetEvent(Long targetEvent) {
		this.targetEvent = targetEvent;
	}

	public Long getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(Long targetObject) {
		this.targetObject = targetObject;
	}

	public String getTargetEmotion() {
		return targetEmotion;
	}

	public void setTargetEmotion(String targetEmotion) {
		this.targetEmotion = targetEmotion;
	}

	public String getTargetVariable() {
		return targetVariable;
	}

	public void setTargetVariable(String targetVariable) {
		this.targetVariable = targetVariable;
	}

	public Boolean getIsInteraction() {
		return isInteraction;
	}

	public void setIsInteraction(Boolean isInteraction) {
		this.isInteraction = isInteraction;
	}

	public Boolean getIsFunction() {
		return isFunction;
	}

	public void setIsFunction(Boolean isFunction) {
		this.isFunction = isFunction;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	

}
