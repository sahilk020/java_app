var chart;
function populateData(data, chartName) {
	 if (data.length === 0) {
        // If data is empty, draw x or y axis only
        drawAxesOnly(chartName);
        return;
    }
	// data,
	$('#chartContainer').css('display','block');
	var baseChartDesignArray = [];
	var datapoint=[];
	var dataArray=[];
	
	//var jsonData=getData();

	var jsonData=data;
	for(var i=0;i<jsonData.length; i++){
		baseChartDesignArray.push(
			{
				type: "line",
				axisYType: "primary",
				legendText: "{name}",
				showInLegend: true,
				markerSize: 0,
				//yValueFormatString: "%.##",
				dataPoints: datapoint
			}
		)
		var dataPointArray=[];
		var label=jsonData[i].label;
		var dataToPopulate=jsonData[i].data;
		var dateArray=jsonData[i].dateArray;
		
	    
	    if(dateArray.length==dataToPopulate.length){
			for(var j =0; j<dataToPopulate.length;j++){
			datapoint.push({ x: new Date(dateFormatChange(dataToPopulate[j].date)), y: dataToPopulate[j].percentage, name: label });
	   		 }
		}else{
			var z=null;
			 for(var j =0; j<dateArray.length;j++){
				if(z==null){
						z=j;		
					}else {
						z++;
					}
					
					if(z!=0 && z>=dataToPopulate.length-1){
					    z--;
						}
					
				if(dateCompare(dateArray[j],dataToPopulate[z].date)){
					datapoint.push({ x: new Date(dateFormatChange(dataToPopulate[z].date)), y: dataToPopulate[z].percentage, name: label });
				}else{
						datapoint.push({ x: new Date(dateFormatChange(dateArray[j])), y: 0, name: label });
						if(z!=0){
					    z--;
						}
						
					}
				
				}		
			}
	    
	    
		dataArray.push(baseChartDesignArray[0]);
			datapoint=[];
			baseChartDesignArray=[];
	}
	 chart = new CanvasJS.Chart("chartContainer", {
		height:500,
		title: {
			text : chartName,
			fontFamily: "\"Lucida Grande\", \"Lucida Sans Unicode\", Verdana, Arial, Helvetica, sans-serif",
        	fontSize: 20
		},
		legend: {
			cursor: "pointer",
			verticalAlign: "bottom" ,
			horizontalAlign: "center",
			dockInsidePlotArea: false,
			itemclick: toogleDataSeries
		},
		exportEnabled: true,
		
		axisX: {
			valueFormatString: "DD MMM YYYY",
			labelAngle: 90
		},
		axisY: {
			title: "Value in Percentage",
			titleFontFamily: "\"Lucida Grande\", \"Lucida Sans Unicode\", Verdana, Arial, Helvetica, sans-serif",
        	titleFontSize: 20,
			// prefix: "$",
			suffix: "%",
			minimum: -10,
			gridThickness: 0
		},
		toolTip: {
			shared: true
		},
		data: dataArray
	});
	chart.render();
	$('#white-bg').css('display','block');



}

function populateFraudData(data, chartName){
	$('#chartContainer').css('display','block');
	var baseChartDesignArray = [];
	var datapoint=[];
	var dataArray=[];
	
	//var jsonData=getData();

	var jsonData=data;
	for(var i=0;i<jsonData.length; i++){
		var label=jsonData[i].label;
		if(label!=null){
		baseChartDesignArray.push(
			{
				type: "line",
				axisYType: "primary",
				legendText: "{name}",
				showInLegend: true,
				markerSize: 0,
				//yValueFormatString: "%.##",
				dataPoints: datapoint
			}
		)
		var dataPointArray=[];
		
		var dataToPopulate=jsonData[i].data;
		var dateArray=jsonData[i].dateArray;
		
	    
	    if(dateArray.length==dataToPopulate.length){
			for(var j =0; j<dataToPopulate.length;j++){
			datapoint.push({ x: new Date(dateFormatChange(dataToPopulate[j].date)), y: dataToPopulate[j].percentage, name: label });
	   		 }
		}else{
			var z=null;
			 for(var j =0; j<dateArray.length;j++){
				if(z==null){
						z=j;		
					}else {
						z++;
					}
					
					if(z!=0 && z>=dataToPopulate.length-1){
					    z--;
						}
					
				if(dateCompare(dateArray[j],dataToPopulate[z].date)){
					datapoint.push({ x: new Date(dateFormatChange(dataToPopulate[z].date)), y: dataToPopulate[z].percentage, name: label });
				}else{
						datapoint.push({ x: new Date(dateFormatChange(dateArray[j])), y: 0, name: label });
						if(z!=0){
					    z--;
						}
						
					}
				
				}		
			}
	    
	    
		dataArray.push(baseChartDesignArray[0]);
			datapoint=[];
			baseChartDesignArray=[];
	}
}
	 chart = new CanvasJS.Chart("chartContainer", {
		height:500,
		title: {
			text : chartName,
			fontFamily: "\"Lucida Grande\", \"Lucida Sans Unicode\", Verdana, Arial, Helvetica, sans-serif",
        	fontSize: 20
		},
		legend: {
			cursor: "pointer",
			verticalAlign: "bottom" ,
			horizontalAlign: "center",
			dockInsidePlotArea: false,
			itemclick: toogleDataSeries
		},
		exportEnabled: true,
		
		axisX: {
			valueFormatString: "DD MMM YYYY",
			labelAngle: 90
		},
		axisY: {
			title: "Value in Percentage",
			titleFontFamily: "\"Lucida Grande\", \"Lucida Sans Unicode\", Verdana, Arial, Helvetica, sans-serif",
        	titleFontSize: 20,
			// prefix: "$",
			suffix: "%",
			minimum: -10,
			gridThickness: 0
		},
		toolTip: {
			shared: true
		},
		data: dataArray
	});
	chart.render();
	$('#white-bg').css('display','block');

}
function toogleDataSeries(e) {
	if (typeof (e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
		e.dataSeries.visible = false;
	} else {
		e.dataSeries.visible = true;
	}
	chart.render();
}


function dateCompare(date1,date2){
	//return (new Date(dateFormatChange(date1))===new Date(dateFormatChange(date2)));
	return (date1==date2);
	
}


function dateFormatChange(date){
	var year = date.substring(0, 4);
	var month = date.substring(4, 6);
	var day = date.substring(6, 8);
	
	return year + ', ' + month + ', ' + day;

}

function getData(){
	
	var json= [{
 		"data": [{
 			"date": "20220826",
 			"percentage": 14,
 			"totalFieldCount": 8,
 			"totalCount": 59
 		}, {
 			"date": "20220828",
 			"percentage": 14,
 			"totalFieldCount": 2,
 			"totalCount": 14
 		}, {
 			"date": "20220829",
 			"percentage": 19,
 			"totalFieldCount": 8,
 			"totalCount": 42
 		}, {
 			"date": "20220831",
 			"percentage": 35,
 			"totalFieldCount": 6,
 			"totalCount": 17
 		}],
 		"label": "Cancelled",
 		"dateArray": ["20220825", "20220826", "20220827", "20220828", "20220829", "20220830", "20220831"]
 	},
 	{
 		"data": [{
 			"date": "20220825",
 			"percentage": 30,
 			"totalFieldCount": 13,
 			"totalCount": 44
 		}, {
 			"date": "20220826",
 			"percentage": 34,
 			"totalFieldCount": 20,
 			"totalCount": 59
 		}, {
 			"date": "20220827",
 			"percentage": 9,
 			"totalFieldCount": 1,
 			"totalCount": 11
 		}, {
 			"date": "20220828",
 			"percentage": 14,
 			"totalFieldCount": 2,
 			"totalCount": 14
 		}, {
 			"date": "20220829",
 			"percentage": 33,
 			"totalFieldCount": 14,
 			"totalCount": 42
 		}, {
 			"date": "20220830",
 			"percentage": 23,
 			"totalFieldCount": 10,
 			"totalCount": 43
 		}, {
 			"date": "20220831",
 			"percentage": 24,
 			"totalFieldCount": 4,
 			"totalCount": 17
 		}],
 		"label": "Captured",
 		"dateArray": ["20220825", "20220826", "20220827", "20220828", "20220829", "20220830", "20220831"]
 	}
 ];

	return json;
	
}

function drawAxesOnly(chartName) {
    $('#chartContainer').empty(); // Clear existing content
    $('#chartContainer').css('display', 'block');
if (!chartName) {
    $('#chartContainer').html("<div style='text-align:center; padding:20px;'>No data available</div>");
} else {
    $('#chartContainer').html("<div style='text-align:center; padding:20px;'>No data available for " + chartName + "</div>");
}
}
function resetChart(chartNumber){

	$('#white-bg').css('display','none');
		chart=null;
		populateData("","")
	$('#chartContainer').css('display','block');
	if(chartNumber=='1'){
	    $("#fetch").trigger('click'); // Trigger click event on button with ID "fetch"
	}
	if(chartNumber=='2'){
	    $("#fetch1").trigger('click'); // Trigger click event on button with ID "fetch"
	}
	if(chartNumber=='3'){
	    $("#fetch2").trigger('click'); // Trigger click event on button with ID "fetch"	
	}
}