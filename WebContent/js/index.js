	/* Toggle between showing and hiding the navigation menu links when the user clicks on the hamburger menu / bar icon */
	function myFunction() {
	  var x = document.getElementById("myLinks");
	  if (x.style.display === "block") {
	    x.style.display = "none";
	  } else {
	    x.style.display = "block";
	  }
	}

$(document).ready(function() {
	
	var mapaModes = {"OFF":0, "MANUAL":1, "PROGRAMA":2};

	for (i = 10; i < 30; i=i+0.5){
		$("#tempTarget_select").append("<option>"+i+"</option>");
	};
	
	monitorRefresh();
	 
	 $("#monitor_menu").click(function(){
		 $("#myLinks").css("display", "none");
		 $("#monitor").css("display","block");
		 $("#control").css("display", "none");
		 $("#program").css("display","none");
		 $("#historic").css("display","none");
		 $("#about").css("display","none");
		 monitorRefresh();
	 });
	 
	 $("#control_menu").click(function(){
		 $("#myLinks").css("display", "none");
		 $("#monitor").css("display","none");
		 $("#control").css("display", "block");
		 $("#program").css("display","none");
		 $("#historic").css("display","none");
		 $("#about").css("display","none");
		 $("#modeOp_select").val($("#modeOp").text());
		 $("#tempTarget_select").val($("#tempTarget").text().split(" ")[0]);
		 if ($("#modeOp").text() == "MANUAL")
			 $("#tempTarget_select").prop("disabled", false);
		 else
			 $("#tempTarget_select").prop("disabled", true);
	 });
	 
	 $("#modeOp_select").change(function(){
		 if ($("#modeOp_select").val() == "MANUAL")
			 $("#tempTarget_select").prop("disabled", false);
		 else
			 $("#tempTarget_select").prop("disabled", true);
	 });
	 
	 $("#control_button").click(function(){
		 $("#control_button").css("background-color","red");
		 $("#control_button").prop("disabled", true);
		 postea(mapaModes[$("#modeOp_select").val()], $("#tempTarget_select").val());
	 });
	
	 $("#about_menu").click(function(){
		 $("#myLinks").css("display", "none");
		 $("#monitor").css("display","none");
		 $("#control").css("display", "none");
		 $("#program").css("display","none");
		 $("#historic").css("display","none");
		 $("#about").css("display","block");
	 });
	 
	 function monitorRefresh(){
		 $("#refrescando").css("display", "block");
		 $.get("ControlServlet", function(data, status) {
				$("#estado").text(data.estado);
				$("#currentTemp").text(data.currentTemp);
				$("#currentHum").text(data.currentHum);
				modo = Object.keys(mapaModes)[data.modeOp];
				$("#modeOp").text(modo);
				if (data.tempTarget == "9999")
					data,tempTarget="N.A.";
				$("#tempTarget").text(data.tempTarget);
				$("#tempTarget2").text(data.tempTarget+" C");
				$("#refrescando").css("display", "none");
			});
		 
		 var chart = lastChart();
		 //chart.resize();
	 }
	 
	 
	 function lastChart(){
	 	$.get("HistoryServlet?mode=last", function(responseJson) {
			chartdata = responseJson;
			$("#lastchart").html("");
			chart = Morris.Line({
		  	  	element: 'lastchart',
		  	 	data: chartdata,
		  	  	xkey: 'time',
		  	  	ykeys: ['currentTemp'],
		  	  	postUnits: ' ºC',
		  	  	labels: ['Temperatura'],
		  	  	parseTime: false,
		  	  	ymin: 10,
		  		});
		});
	};
	
	function postea(modeClient, tempClient){
	 $("#refrescando").css("display", "block");
	 $("#control_button").text("Activando")
	 $.post("ControlServlet",
			    {
		 			clientMode: modeClient,
		 			clientTemp:tempClient
			    },
			    function(data, status){
			        if (data == "OK" && status == "success"){
			        	$("#refrescando").css("display", "none");
			   	 		$("#control_button").css("background-color","lightskyblue");
			   	 		$("#control_button").text("Activar")
			   	 		$("#control_button").prop("disabled", false);
			        } else {
			        	alert("Algo ha ido mal al conectarnos con el sistema");
			        }
			    });
	}
	 
 });
	