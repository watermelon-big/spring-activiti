package boot.spring.pagemodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel("报销任务信息")
public class CostTask {
	@ApiModelProperty("主键")
	int id;
	@ApiModelProperty("流程实例id")
	String process_instance_id;
	@ApiModelProperty("用户名")
	String user_id;
	@ApiModelProperty("报销类型")
	String cost_type;
	@ApiModelProperty("报销金额")
	String cost_amount;
	@ApiModelProperty("报销备注")
	String remark;
	@ApiModelProperty("起始时间")
	String start_time;
	@ApiModelProperty("结束时间")
	String end_time;
	@ApiModelProperty("申请时间")
	String apply_time;
	@ApiModelProperty("任务id")
	String taskid;
	@ApiModelProperty("任务名")
	String taskname;
	@ApiModelProperty("流程实例id")
	String processinstanceid;
	@ApiModelProperty("流程定义id")
	String processdefid;
	@ApiModelProperty("任务创建时间")
	Date taskcreatetime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProcess_instance_id() {
		return process_instance_id;
	}
	public void setProcess_instance_id(String process_instance_id) {
		this.process_instance_id = process_instance_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getApply_time() {
		return apply_time;
	}
	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}
	public String getProcessinstanceid() {
		return processinstanceid;
	}
	public void setProcessinstanceid(String processinstanceid) {
		this.processinstanceid = processinstanceid;
	}
	public String getProcessdefid() {
		return processdefid;
	}
	public void setProcessdefid(String processdefid) {
		this.processdefid = processdefid;
	}
	public Date getTaskcreatetime() {
		return taskcreatetime;
	}
	public void setTaskcreatetime(Date taskcreatetime) {
		this.taskcreatetime = taskcreatetime;
	}
	public String getCost_type() {
		return cost_type;
	}
	public void setCost_type(String cost_type) {
		this.cost_type = cost_type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCost_amount() {
		return cost_amount;
	}

	public void setCost_amount(String cost_amount) {
		this.cost_amount = cost_amount;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
}
