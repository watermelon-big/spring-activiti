package boot.spring.controller;

import boot.spring.pagemodel.Process;
import boot.spring.pagemodel.*;
import boot.spring.po.*;
import boot.spring.service.CostService;
import boot.spring.service.LeaveService;
import boot.spring.service.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "报销流程接口")
@Controller
public class CostController {
	@Autowired
	RepositoryService rep;
	@Autowired
	RuntimeService runservice;
	@Autowired
	FormService formservice;
	@Autowired
	IdentityService identityservice;
	@Autowired
	CostService costService;
	@Autowired
	TaskService taskservice;
	@Autowired
	HistoryService histiryservice;
	@Autowired
	SystemService systemservice;



	@RequestMapping(value = "/costdeptleaderaudit", method = RequestMethod.GET)
	public String mytask() {
		return "cost/costdeptleaderaudit";
	}

	@RequestMapping(value = "/cwaudit", method = RequestMethod.GET)
	public String cw() {
		return "cost/cwaudit";
	}

	@RequestMapping(value = "/costindex", method = RequestMethod.GET)
	public String my() {
		return "index";
	}

	@RequestMapping(value = "/costapply", method = RequestMethod.GET)
	public String costapply() {
		return "cost/costapply";
	}

	@RequestMapping(value = "/costreportback", method = RequestMethod.GET)
	public String costreprotback() {
		return "cost/costreportback";
	}

	@RequestMapping(value = "/modifycostapply", method = RequestMethod.GET)
	public String modifycostapply() {
		return "cost/modifycostapply";
	}

	@RequestMapping(value = "/startcost", method = RequestMethod.POST)
	@ResponseBody
	public MSG start_cost(CostApply apply, HttpSession session) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("applyuserid", userid);
		ProcessInstance ins = costService.startWorkflow(apply, userid, variables);
		System.out.println("流程id" + ins.getId() + "已启动");
		return new MSG("sucess");
	}

	@ApiOperation("获取部门领导审批代办列表")
	@RequestMapping(value = "/costdepttasklist", produces = {
			"application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<CostTask> getcostdepttasklist(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		DataGrid<CostTask> grid = new DataGrid<CostTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setTotal(0);
		grid.setRows(new ArrayList<CostTask>());
		// 先做权限检查，对于没有部门领导审批权限的用户,直接返回空
		String userid = (String) session.getAttribute("username");
		int uid = systemservice.getUidByusername(userid);
		User user = systemservice.getUserByid(uid);
		List<User_role> userroles = user.getUser_roles();
		if (userroles == null)
			return grid;
		boolean flag = false;// 默认没有权限
		for (int k = 0; k < userroles.size(); k++) {
			User_role ur = userroles.get(k);
			Role r = ur.getRole();
			int roleid = r.getRid();
			Role role = systemservice.getRolebyid(roleid);
			List<Role_permission> p = role.getRole_permission();
			for (int j = 0; j < p.size(); j++) {
				Role_permission rp = p.get(j);
				Permission permission = rp.getPermission();
				if (permission.getPermissionname().equals("部门领导审批"))
					flag = true;
				else
					continue;
			}
		}
		if (flag == false)// 无权限
		{
			return grid;
		} else {
			int firstrow = (current - 1) * rowCount;
			List<CostApply> results = costService.getpagedepttask(userid, firstrow, rowCount);
			int totalsize = costService.getalldepttask(userid);
			List<CostTask> tasks = new ArrayList<CostTask>();
			for (CostApply apply : results) {
				CostTask task = new CostTask();
				task.setApply_time(apply.getApply_time());
				task.setUser_id(apply.getUser_id());
				task.setStart_time(apply.getStart_time());
				task.setEnd_time(apply.getEnd_time());
				task.setId(apply.getId());
				task.setCost_type(apply.getCost_type());
				task.setCost_amount(apply.getCost_amount());
				task.setProcess_instance_id(apply.getProcess_instance_id());
				task.setProcessdefid(apply.getTask().getProcessDefinitionId());
				task.setRemark(apply.getRemark());
				task.setTaskcreatetime(apply.getTask().getCreateTime());
				task.setTaskid(apply.getTask().getId());
				task.setTaskname(apply.getTask().getName());
				tasks.add(task);
			}
			grid.setRowCount(rowCount);
			grid.setCurrent(current);
			grid.setTotal(totalsize);
			grid.setRows(tasks);
			return grid;
		}
	}

	@RequestMapping(value = "/cwtasklist", produces = { "application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<CostTask> getcwtasklist(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		DataGrid<CostTask> grid = new DataGrid<CostTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setTotal(0);
		grid.setRows(new ArrayList<CostTask>());
		// 先做权限检查，对于没有人事权限的用户,直接返回空
		String userid = (String) session.getAttribute("username");
		int uid = systemservice.getUidByusername(userid);
		User user = systemservice.getUserByid(uid);
		List<User_role> userroles = user.getUser_roles();
		if (userroles == null)
			return grid;
		boolean flag = false;// 默认没有权限
		for (int k = 0; k < userroles.size(); k++) {
			User_role ur = userroles.get(k);
			Role r = ur.getRole();
			int roleid = r.getRid();
			Role role = systemservice.getRolebyid(roleid);
			List<Role_permission> p = role.getRole_permission();
			for (int j = 0; j < p.size(); j++) {
				Role_permission rp = p.get(j);
				Permission permission = rp.getPermission();
				if (permission.getPermissionname().equals("财务审批"))
					flag = true;
				else
					continue;
			}
		}
		if (flag == false)// 无权限
		{
			return grid;
		} else {
			int firstrow = (current - 1) * rowCount;
			List<CostApply> results = costService.getpagehrtask(userid, firstrow, rowCount);
			int totalsize = costService.getallhrtask(userid);
			List<CostTask> tasks = new ArrayList<CostTask>();
			for (CostApply apply : results) {
				CostTask task = new CostTask();
				task.setApply_time(apply.getApply_time());
				task.setUser_id(apply.getUser_id());
				task.setStart_time(apply.getStart_time());
				task.setEnd_time(apply.getEnd_time());
				task.setId(apply.getId());
				task.setCost_type(apply.getCost_type());
				task.setProcess_instance_id(apply.getProcess_instance_id());
				task.setProcessdefid(apply.getTask().getProcessDefinitionId());
				task.setRemark(apply.getRemark());
				task.setTaskcreatetime(apply.getTask().getCreateTime());
				task.setTaskid(apply.getTask().getId());
				task.setTaskname(apply.getTask().getName());
				tasks.add(task);
			}
			grid.setRowCount(rowCount);
			grid.setCurrent(current);
			grid.setTotal(totalsize);
			grid.setRows(tasks);
			return grid;
		}
	}

	@RequestMapping(value = "/jxtasklist", produces = { "application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<CostTask> getJXtasklist(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		List<CostApply> results = costService.getpageXJtask(userid, firstrow, rowCount);
		int totalsize = costService.getallXJtask(userid);
		List<CostTask> tasks = new ArrayList<CostTask>();
		for (CostApply apply : results) {
			CostTask task = new CostTask();
			task.setApply_time(apply.getApply_time());
			task.setUser_id(apply.getUser_id());
			task.setStart_time(apply.getStart_time());
			task.setEnd_time(apply.getEnd_time());
			task.setId(apply.getId());
			task.setCost_type(apply.getCost_type());
			task.setProcess_instance_id(apply.getProcess_instance_id());
			task.setProcessdefid(apply.getTask().getProcessDefinitionId());
			task.setRemark(apply.getRemark());
			task.setTaskcreatetime(apply.getTask().getCreateTime());
			task.setTaskid(apply.getTask().getId());
			task.setTaskname(apply.getTask().getName());
			tasks.add(task);
		}
		DataGrid<CostTask> grid = new DataGrid<CostTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setTotal(totalsize);
		grid.setRows(tasks);
		return grid;
	}

	@RequestMapping(value = "/updatecosttasklist", produces = {
			"application/json;charset=UTF-8" }, method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<CostTask> getupdatecosttasklist(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		List<CostApply> results = costService.getpageupdateapplytask(userid, firstrow, rowCount);
		int totalsize = costService.getallupdateapplytask(userid);
		List<CostTask> tasks = new ArrayList<CostTask>();
		for (CostApply apply : results) {
			CostTask task = new CostTask();
			task.setApply_time(apply.getApply_time());
			task.setUser_id(apply.getUser_id());
			task.setId(apply.getId());
			task.setCost_type(apply.getCost_type());
			task.setProcess_instance_id(apply.getProcess_instance_id());
			task.setProcessdefid(apply.getTask().getProcessDefinitionId());
			task.setRemark(apply.getRemark());

			task.setTaskcreatetime(apply.getTask().getCreateTime());
			task.setTaskid(apply.getTask().getId());
			task.setTaskname(apply.getTask().getName());
			tasks.add(task);
		}
		DataGrid<CostTask> grid = new DataGrid<CostTask>();
		grid.setRowCount(rowCount);
		grid.setCurrent(current);
		grid.setTotal(totalsize);
		grid.setRows(tasks);
		return grid;
	}

	@RequestMapping(value = "/dealcosttask", method = RequestMethod.POST)
	@ResponseBody
	public CostApply costtaskdeal(@RequestParam("taskid") String taskid, HttpServletResponse response) {
		Task task = taskservice.createTaskQuery().taskId(taskid).singleResult();
		ProcessInstance process = runservice.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId())
				.singleResult();
		CostApply costApply = costService.getleave(new Integer(process.getBusinessKey()));
		return costApply;
	}

	@RequestMapping(value = "/cost/task-deptleaderaudit", method = RequestMethod.GET)
	String url() {
		return "/cost/task-deptleaderaudit";
	}

	@RequestMapping(value = "/task/costdeptcomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG deptcomplete(HttpSession session, @PathVariable("taskid") String taskid, HttpServletRequest req) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		String approve = req.getParameter("deptleaderapprove");
		variables.put("deptleaderapprove", approve);
		taskservice.claim(taskid, userid);
		taskservice.complete(taskid, variables);
		return new MSG("success");
	}

	@RequestMapping(value = "/task/cwcomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG cwcomplete(HttpSession session, @PathVariable("taskid") String taskid, HttpServletRequest req) {
		String userid = (String) session.getAttribute("username");
		Map<String, Object> variables = new HashMap<String, Object>();
		String approve = req.getParameter("financeapprove");
		variables.put("financeapprove", approve);
		taskservice.claim(taskid, userid);
		taskservice.complete(taskid, variables);
		return new MSG("success");
	}

	@RequestMapping(value = "/task/costreportcomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG reportbackcomplete(@PathVariable("taskid") String taskid, HttpServletRequest req) {
		String realstart_time = req.getParameter("realstart_time");
		String realend_time = req.getParameter("realend_time");
		costService.completereportback(taskid, realstart_time, realend_time);
		return new MSG("success");
	}

	@RequestMapping(value = "/task/costupdatecomplete/{taskid}", method = RequestMethod.POST)
	@ResponseBody
	public MSG updatecomplete(@PathVariable("taskid") String taskid, @ModelAttribute("cost") CostApply cost,
			@RequestParam("reapply") String reapply) {
		costService.updatecomplete(taskid, cost, reapply);
		return new MSG("success");
	}

	@RequestMapping(value = "costinvolvedprocess", method = RequestMethod.POST) // 参与的正在运行的请假流程
	@ResponseBody
	public DataGrid<RunningProcess> allexeution(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		ProcessInstanceQuery query = runservice.createProcessInstanceQuery();
		int total = (int) query.count();
		List<ProcessInstance> a = query.processDefinitionKey("leave").involvedUser(userid).listPage(firstrow, rowCount);
		List<RunningProcess> list = new ArrayList<RunningProcess>();
		for (ProcessInstance p : a) {
			RunningProcess process = new RunningProcess();
			process.setActivityid(p.getActivityId());
			process.setBusinesskey(p.getBusinessKey());
			process.setExecutionid(p.getId());
			process.setProcessInstanceid(p.getProcessInstanceId());
			list.add(process);
		}
		DataGrid<RunningProcess> grid = new DataGrid<RunningProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(total);
		grid.setRows(list);
		return grid;
	}

	@RequestMapping(value = "/getfinishcostprocess", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<HistoryProcess> getHistory(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		String userid = (String) session.getAttribute("username");
		HistoricProcessInstanceQuery process = histiryservice.createHistoricProcessInstanceQuery()
				.processDefinitionKey("cost").startedBy(userid).finished();
		int total = (int) process.count();
		int firstrow = (current - 1) * rowCount;
		List<HistoricProcessInstance> info = process.listPage(firstrow, rowCount);
		List<HistoryProcess> list = new ArrayList<HistoryProcess>();
		for (HistoricProcessInstance history : info) {
			HistoryProcess his = new HistoryProcess();
			String bussinesskey = history.getBusinessKey();
			CostApply apply = costService.getleave(Integer.parseInt(bussinesskey));
			his.setCostapply(apply);
			his.setBusinessKey(bussinesskey);
			his.setProcessDefinitionId(history.getProcessDefinitionId());
			list.add(his);
		}
		DataGrid<HistoryProcess> grid = new DataGrid<HistoryProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(total);
		grid.setRows(list);
		return grid;
	}

	@RequestMapping(value = "/historycostprocess", method = RequestMethod.GET)
	public String history() {
		return "cost/historycostprocess";
	}

	@RequestMapping(value = "/costprocessinfo", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricActivityInstance> processinfo(@RequestParam("instanceid") String instanceid) {
		List<HistoricActivityInstance> his = histiryservice.createHistoricActivityInstanceQuery()
				.processInstanceId(instanceid).orderByHistoricActivityInstanceStartTime().asc().list();
		return his;
	}

	@RequestMapping(value = "/costprocesshis", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricActivityInstance> processhis(@RequestParam("ywh") String ywh) {
		String instanceid = histiryservice.createHistoricProcessInstanceQuery().processDefinitionKey("purchase")
				.processInstanceBusinessKey(ywh).singleResult().getId();
		List<HistoricActivityInstance> his = histiryservice.createHistoricActivityInstanceQuery()
				.processInstanceId(instanceid).orderByHistoricActivityInstanceStartTime().asc().list();
		return his;
	}

	@RequestMapping(value = "mycostprocess", method = RequestMethod.GET)
	String myleaveprocess() {
		return "cost/mycostprocess";
	}

	@RequestMapping(value = "costtraceprocess/{executionid}", method = RequestMethod.GET)
	public void traceprocess(@PathVariable("executionid") String executionid, HttpServletResponse response)
			throws Exception {
		ProcessInstance process = runservice.createProcessInstanceQuery().processInstanceId(executionid).singleResult();
		BpmnModel bpmnmodel = rep.getBpmnModel(process.getProcessDefinitionId());
		List<String> activeActivityIds = runservice.getActiveActivityIds(executionid);
		DefaultProcessDiagramGenerator gen = new DefaultProcessDiagramGenerator();
		// 获得历史活动记录实体（通过启动时间正序排序，不然有的线可以绘制不出来）
		List<HistoricActivityInstance> historicActivityInstances = histiryservice.createHistoricActivityInstanceQuery()
				.executionId(executionid).orderByHistoricActivityInstanceStartTime().asc().list();
		// 计算活动线
		List<String> highLightedFlows = costService
				.getHighLightedFlows(
						(ProcessDefinitionEntity) ((RepositoryServiceImpl) rep)
								.getDeployedProcessDefinition(process.getProcessDefinitionId()),
						historicActivityInstances);

		InputStream in = gen.generateDiagram(bpmnmodel, "png", activeActivityIds, highLightedFlows, "宋体", "宋体", null,
				null, 1.0);
		// InputStream in=gen.generateDiagram(bpmnmodel, "png",
		// activeActivityIds);
		ServletOutputStream output = response.getOutputStream();
		IOUtils.copy(in, output);
	}

	@RequestMapping(value = "mycosts", method = RequestMethod.GET)
	String mycosts() {
		return "cost/mycosts";
	}

	@RequestMapping(value = "setcostupprocess", method = RequestMethod.POST)
	@ResponseBody
	public DataGrid<RunningProcess> setupprocess(HttpSession session, @RequestParam("current") int current,
			@RequestParam("rowCount") int rowCount) {
		int firstrow = (current - 1) * rowCount;
		String userid = (String) session.getAttribute("username");
		System.out.print(userid);
		ProcessInstanceQuery query = runservice.createProcessInstanceQuery();
		int total = (int) query.count();
		List<ProcessInstance> a = query.processDefinitionKey("cost").involvedUser(userid).listPage(firstrow, rowCount);
		List<RunningProcess> list = new ArrayList<RunningProcess>();
		for (ProcessInstance p : a) {
			RunningProcess process = new RunningProcess();
			process.setActivityid(p.getActivityId());
			process.setBusinesskey(p.getBusinessKey());
			process.setExecutionid(p.getId());
			process.setProcessInstanceid(p.getProcessInstanceId());
			CostApply l = costService.getleave(Integer.parseInt(p.getBusinessKey()));
			if (l.getUser_id().equals(userid))
				list.add(process);
			else
				continue;
		}
		DataGrid<RunningProcess> grid = new DataGrid<RunningProcess>();
		grid.setCurrent(current);
		grid.setRowCount(rowCount);
		grid.setTotal(total);
		grid.setRows(list);
		return grid;
	}

}
