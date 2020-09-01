
    $(document).ready(function(){
	$('#startime').datepicker({setDate: new Date(), dateFormat: 'yy-mm-dd'});
	$('#endtime').datepicker({setDate: new Date(), dateFormat: 'yy-mm-dd'});

    	$("#dept").hide();
	    var grid=$("#grid-data").bootgrid({
	    	navigation:2,
  			columnSelection:false,
		    ajax:true,
		    url:"updatecosttasklist",
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
	    	    		$("#userid").val(obj.user_id);
	    	    		$("#startime").val(obj.start_time);
	    	    		$("#endtime").val(obj.end_time);
	    	    		$("#applytime").val(obj.apply_time);
	    	    		$("form").attr("action","task/costupdatecomplete/"+taskid);
	    	    	});
	    	    	$("#dept").show();
	    	    	$("#btn").click(function(){
	    		    	$.post("task/costupdatecomplete/"+taskid,$("form").serialize(),function(a){
	    		    		alert("处理成功");
	    		    		LoadAjaxContent("modifycostapply");
	    		    	});

	    	    });
	    	    });


	    });
	  });

	   function getLocalTime(nS) {
 return new Date(parseInt(nS)).toLocaleString().replace(/:\d{1,2}$/,' ');
}
