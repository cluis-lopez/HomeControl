$(document).ready(function() {
	
	var mapaModes = {"OFF":0, "MANUAL":1, "PROGRAMA":2};

	for (i = 10; i < 30; i=i+0.5){
		$("#tempTarget_select").append("<option>"+i+"</option>");
	};
	
	$(".icon").click(function toggleMenu(){
		if ($("#myLinks").css("display") == "block")
			$("#myLinks").css("display", "none");
		else
			$("#myLinks").css("display", "block");
			
	});
	
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
		 if (isNaN($("#tempTarget").text().split(" ")[0])) // True if this is Not a Number
			 $("#tempTarget_select").val("20");
		 else
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
	 
	 function monitorRefresh() {
		 $("#refrescando").css("display", "block");
		 $.ajax({ cache: false,
			    url: "ControlServlet",
			    success: function (data) {
			    	$("#estado").text(data.estado);
					$("#currentTemp").text(data.currentTemp);
					$("#currentHum").text(data.currentHum);
					modo = Object.keys(mapaModes)[data.modeOp];
					$("#modeOp").text(modo);
					if (data.tempTarget == "9999")
						data.tempTarget="N.A.";
					$("#tempTarget").text(data.tempTarget+"  &degC");
					$("#tempTarget2").text(data.tempTarget+"  &degC");
					$("#refrescando").css("display", "none");
			    },
			    error: function (xhr, ajaxOptions, thrownError) {
			    	$("#refrescando").css("display", "none");
			        alert("Ha habido un problema al comunicarse con el servidor " + thrownError);
			    },
			    timeout: 5000
			});
		 var chart = lastChart();
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
		  	  	postUnits: '&degC',
		  	  	labels: ['Temperatura'],
		  	  	parseTime: false,
		  	  	ymin: 10,
		  	  	hideHover: 'always',
		  		});
		});
	};
	
	function postea(modeClient, tempClient){
		$("#refrescando").css("display", "block");
		$("#control_button").text("Activando");
		$.ajax({
			url: "ControlServlet", 
			data: {clientMode: modeClient,
				clientTemp:tempClient},
				type: 'post',
				error: function(XMLHttpRequest, textStatus, errorThrown){
					$("#refrescando").css("display", "none");
					$("#control_button").css("background-color","lightskyblue");
					$("#control_button").text("Activar")
					$("#control_button").prop("disabled", false);
					alert('Problemas de xonexion. Status:' + XMLHttpRequest.status);
				},
				success: function(data){
					if (data == "OK"){
						$("#refrescando").css("display", "none");
						$("#control_button").css("background-color","lightskyblue");
						$("#control_button").text("Activar")
						$("#control_button").prop("disabled", false);
					} else {
						$("#refrescando").css("display", "none");
						$("#control_button").css("background-color","lightskyblue");
						$("#control_button").text("Activar")
						$("#control_button").prop("disabled", false);
						alert("Algo ha ido mal al conectarnos con el sistema");
					}
				}
		});
	}
	 
 });
	