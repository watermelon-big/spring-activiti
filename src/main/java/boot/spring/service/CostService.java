package boot.spring.service;


import boot.spring.po.CostApply;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;


public interface CostService {
	public ProcessInstance startWorkflow(CostApply apply, String userid, Map<String, Object> variables);
	public List<CostApply> getpagedepttask(String userid, int firstrow, int rowcount);
	public int getalldepttask(String userid);
	public CostApply getleave(int id);
	public List<CostApply> getpagehrtask(String userid, int firstrow, int rowcount);
	public int getallhrtask(String userid);
	public List<CostApply> getpageXJtask(String userid, int firstrow, int rowcount);
	public int getallXJtask(String userid);
	public List<CostApply> getpageupdateapplytask(String userid, int firstrow, int rowcount);
	public int getallupdateapplytask(String userid);
	public void completereportback(String taskid, String realstart_time, String realend_time);
	public void updatecomplete(String taskid, CostApply leave, String reappply);
	public List<String> getHighLightedFlows(ProcessDefinitionEntity deployedProcessDefinition, List<HistoricActivityInstance> historicActivityInstances);
}
