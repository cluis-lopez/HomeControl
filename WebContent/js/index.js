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
	

	for (i = 10; i < 30; i=i+0.5){
		$("#tempManual_select").append("<option>"+i+"</option>");
	};
	
	monitorRefresh();
	 
	 $("#monitor_menu").click(function(){
		 $("#myLinks").css("display", "none");
		 $("#monitor").css("display","block");
		 $("#control").css("display", "none");
		 $("#program").css("display","none");
		 $("#historic").css("display","none");
		 $("#about").css("display","none");
	 });
	 
	 $("#control_menu").click(function(){
		 console.log("Pulsado control");
		 $("#myLinks").css("display", "none");
		 $("#monitor").css("display","none");
		 $("#program").css("display","none");
		 $("#historic").css("display","none");
		 $("#about").css("display","none");
		 $("#control").css("display", "block");
		 monitorRefresh();
	 });
	
	 function monitorRefresh(){
		 $("#refrescando").css("display", "block");
		 $.get("ControlServlet", function(data, status) {
				$("#estado").text(data.estado);
				$("#currentTemp").text(data.currentTemp);
				$("#currentHum").text(data.currentHum);
				var modo = "";
				switch (data.modeOp) {
				case 0:
					modo = "OFF";
					break;
				case 1:
					modo = "MANUAL";
					break;
				case 2:
					modo = "PROGRAMADO";
					break;
				}
				$("#modeOp").text(modo);
				$("#tempTarget").text(data.tempTarget);
				$("#refrescando").css("display", "none");
			});
		 
		 lastChart();
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
		  	  	resize: true,
		  	  	labels: ['Temperatura']
		  		});
		});
	};
	 
 });
	