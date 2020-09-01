package boot.spring.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.activiti.engine.task.Task;

import java.io.Serializable;

@ApiModel("费用报销表")
public class CostApply implements Serializable{
	@ApiModelProperty("主键")
	int id;
	@ApiModelProperty("流程实例id")
	String process_instance_id;
	@ApiModelProperty("用户名")
	String user_id;
	@ApiModelProperty("报销类型")
	String cost_type;
	@ApiModelProperty("报销备注")
	String remark;
	@ApiModelProperty("申请时间")
	String apply_time;
	@ApiModelProperty("起始时间")
	String start_time;
	@ApiModelProperty("结束时间")
	String end_time;
	@ApiModelProperty("报销金额")
	String cost_amount;

	Task task;
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

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
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

	public String getCost_amount() {
		return cost_amount;
	}

	public void setCost_amount(String cost_amount) {
		this.cost_amount = cost_amount;
	}
}
