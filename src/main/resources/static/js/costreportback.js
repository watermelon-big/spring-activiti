    $(document).ready(function(){
    $('#realstart_time').datepicker({setDate: new Date(), dateFormat: 'yy-mm-dd'});
	$('#realend_time').datepicker({setDate: new Date(), dateFormat: 'yy-mm-dd'});

    	$("#dept").hide();
	    var grid=$("#grid-data").bootgrid({
	    	navigation:2,
  			columnSelection:false,
		    ajax:true,
		    url:"jxtasklist",
		    formatters: {
		    "taskcreatetime":function(column, row){
		    	return getLocalTime(row.taskcreatetime);
		    },
		    "commands": function(column, row)
		    {
		            return "<button class=\"btn btn-xs btn-default ajax-link command-run1\" data-row-id=\"" + row.taskid + "\">处理</button>";
		    }
	    	}

	    }).on("loaded.rs.jquery.bootgrid", function()
	    		{
	    	    grid.find(".command-run1").on("click", function(e)
	    	    {
	    	    	var taskid=$(this).data("row-id");
	    	    	$.post("dealcosttask",{"taskid":taskid},function(data){
	    	    		var obj = data;
	    	    		$("#remark").val(obj.remark);
	    	    		$("#type").val(obj.cost_type);
                        $("#costamount").val(obj.cost_amount);
	    	    		$("#userid").val(obj.user_id);
	    	    		$("#startime").val(obj.start_time);
	    	    		$("#endtime").val(obj.end_time);
	    	    		$("#applytime").val(obj.apply_time);
	    	    		$("form").attr("action","task/costreportcomplete/"+taskid);
	    	    	});
	    	    	$("#dept").show();
	    	    	$("#btn").click(function(){
	    		    	$.post("task/costreportcomplete/"+taskid,$("form").serialize(),function(a){
	    		    		alert("处理成功");
	    		    		LoadAjaxContent("costreportback");
	    		    	});

	    	    });
	    	    });


	    });
	  });


	  function getLocalTime(nS) {
 return new Date(parseInt(nS)).toLocaleString().replace(/:\d{1,2}$/,' ');
}
