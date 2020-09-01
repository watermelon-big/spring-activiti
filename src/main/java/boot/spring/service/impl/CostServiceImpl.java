package boot.spring.service.impl;

import boot.spring.mapper.CostApplyMapper;
import boot.spring.po.CostApply;
import boot.spring.service.CostService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(propagation=Propagation.REQUIRED,isolation=Isolation.DEFAULT,timeout=5)
@Service
public class CostServiceImpl implements CostService {
	@Autowired
	CostApplyMapper costApplyMapper;
	@Autowired
	IdentityService identityservice;
	@Autowired
	RuntimeService runtimeservice;
	@Autowired
	TaskService taskservice;

	public ProcessInstance startWorkflow(CostApply apply, String userid, Map<String, Object> variables) {
		apply.setApply_time(new Date().toString());
		apply.setUser_id(userid);
		costApplyMapper.save(apply);
		String businesskey=String.valueOf(apply.getId());//使用leaveapply表的主键作为businesskey,连接业务数据和流程数据
		identityservice.setAuthenticatedUserId(userid);
		ProcessInstance instance=runtimeservice.startProcessInstanceByKey("cost",businesskey,variables);
		System.out.println(businesskey);
		String instanceid=instance.getId();
		apply.setProcess_instance_id(instanceid);
		costApplyMapper.updateByPrimaryKey(apply);
		return instance;
	}

	public List<CostApply> getpagedepttask(String userid,int firstrow,int rowcount) {
		List<CostApply> results=new ArrayList<CostApply>();
		List<Task> tasks=taskservice.createTaskQuery().taskCandidateGroup("部门经理").listPage(firstrow, rowcount);
		for(Task task:tasks){
			String instanceid=task.getProcessInstanceId();
			ProcessInstance ins=runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
			String businesskey=ins.getBusinessKey();
			CostApply a=costApplyMapper.getCostApply(Integer.parseInt(businesskey));
			a.setTask(task);
			results.add(a);
		}
		return results;
	}

	public int getalldepttask(String userid) {
		List<Task> tasks=taskservice.createTaskQuery().taskCandidateGroup("部门经理").list();
		return tasks.size();
	}

	public CostApply getleave(int id) {
		CostApply leave=costApplyMapper.getCostApply(id);
		return leave;
	}

	public List<CostApply> getpagehrtask(String userid,int firstrow,int rowcount) {
		List<CostApply> results=new ArrayList<CostApply>();
		List<Task> tasks=taskservice.createTaskQuery().taskCandidateGroup("财务").listPage(firstrow, rowcount);
		for(Task task:tasks){
			String instanceid=task.getProcessInstanceId();
			ProcessInstance ins=runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
			String businesskey=ins.getBusinessKey();
			CostApply a=costApplyMapper.getCostApply(Integer.parseInt(businesskey));
			a.setTask(task);
			results.add(a);
		}
		return results;
	}

	public int getallhrtask(String userid) {
		List<Task> tasks=taskservice.createTaskQuery().taskCandidateGroup("财务").list();
		return tasks.size();
	}

	public List<CostApply> getpageXJtask(String userid,int firstrow,int rowcount) {
		List<CostApply> results=new ArrayList<CostApply>();
		List<Task> tasks=taskservice.createTaskQuery().taskCandidateOrAssigned(userid).taskName("确认报销到账").listPage(firstrow, rowcount);
		for(Task task:tasks){
			String instanceid=task.getProcessInstanceId();
			ProcessInstance ins=runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
			String businesskey=ins.getBusinessKey();
			CostApply a=costApplyMapper.getCostApply(Integer.parseInt(businesskey));
			a.setTask(task);
			results.add(a);
		}
		return results;
	}

	public int getallXJtask(String userid) {
		List<Task> tasks=taskservice.createTaskQuery().taskCandidateOrAssigned(userid).taskName("确认报销到账").list();
		return tasks.size();
	}

	public List<CostApply> getpageupdateapplytask(String userid,int firstrow,int rowcount) {
		List<CostApply> results=new ArrayList<CostApply>();
		List<Task> tasks=taskservice.createTaskQuery().taskCandidateOrAssigned(userid).taskName("调整申请").listPage(firstrow, rowcount);
		for(Task task:tasks){
			String instanceid=task.getProcessInstanceId();
			ProcessInstance ins=runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
			String businesskey=ins.getBusinessKey();
			CostApply a=costApplyMapper.getCostApply(Integer.parseInt(businesskey));
			a.setTask(task);
			results.add(a);
		}
		return results;
	}

	public int getallupdateapplytask(String userid) {
		List<Task> tasks=taskservice.createTaskQuery().taskCandidateOrAssigned(userid).taskName("调整申请").list();
		return tasks.size();
	}

	public void completereportback(String taskid, String realstart_time, String realend_time) {
		Task task=taskservice.createTaskQuery().taskId(taskid).singleResult();
		String instanceid=task.getProcessInstanceId();
		ProcessInstance ins=runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
		String businesskey=ins.getBusinessKey();
		CostApply a=costApplyMapper.getCostApply(Integer.parseInt(businesskey));
//		a.setReality_start_time(realstart_time);
//		a.setReality_end_time(realend_time);
		costApplyMapper.updateByPrimaryKey(a);
		taskservice.complete(taskid);
	}

	public void updatecomplete(String taskid, CostApply leave,String reapply) {
		Task task=taskservice.createTaskQuery().taskId(taskid).singleResult();
		String instanceid=task.getProcessInstanceId();
		ProcessInstance ins=runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
		String businesskey=ins.getBusinessKey();
		CostApply a=costApplyMapper.getCostApply(Integer.parseInt(businesskey));
		a.setCost_type(leave.getCost_type());
//		a.setStart_time(leave.getStart_time());
//		a.setEnd_time(leave.getEnd_time());
		a.setRemark(leave.getRemark());
		Map<String,Object> variables=new HashMap<String,Object>();
		variables.put("reapply", reapply);
		if(reapply.equals("true")){
			costApplyMapper.updateByPrimaryKey(a);
			taskservice.complete(taskid,variables);
		}else
			taskservice.complete(taskid,variables);
	}

	public List<String> getHighLightedFlows(
	        ProcessDefinitionEntity processDefinitionEntity,
	        List<HistoricActivityInstance> historicActivityInstances) {

	    List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
	    for (int i = 0; i < historicActivityInstances.size(); i++) {// 对历史流程节点进行遍历
	        ActivityImpl activityImpl = processDefinitionEntity
	                .findActivity(historicActivityInstances.get(i)
	                        .getActivityId());// 得 到节点定义的详细信息
	        List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
	        if ((i + 1) >= historicActivityInstances.size()) {
	            break;
	        }
	        ActivityImpl sameActivityImpl1 = processDefinitionEntity
	                .findActivity(historicActivityInstances.get(i + 1)
	                        .getActivityId());// 将后面第一个节点放在时间相同节点的集合里
	        sameStartTimeNodes.add(sameActivityImpl1);
	        for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
	            HistoricActivityInstance activityImpl1 = historicActivityInstances
	                    .get(j);// 后续第一个节点
	            HistoricActivityInstance activityImpl2 = historicActivityInstances
	                    .get(j + 1);// 后续第二个节点
	            if (activityImpl1.getStartTime().equals(
	                    activityImpl2.getStartTime())) {// 如果第一个节点和第二个节点开始时间相同保存
	                ActivityImpl sameActivityImpl2 = processDefinitionEntity
	                        .findActivity(activityImpl2.getActivityId());
	                sameStartTimeNodes.add(sameActivityImpl2);
	            } else {// 有不相同跳出循环
	                break;
	            }
	        }
	        List<PvmTransition> pvmTransitions = activityImpl
	                .getOutgoingTransitions();// 取出节点的所有出去的线
	        for (PvmTransition pvmTransition : pvmTransitions) {// 对所有的线进行遍历
	            ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
	                    .getDestination();// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
	            if (sameStartTimeNodes.contains(pvmActivityImpl)) {
	                highFlows.add(pvmTransition.getId());
	            }
	        }
	    }
	    return highFlows;
	}
}
