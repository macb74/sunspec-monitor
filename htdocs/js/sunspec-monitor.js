function powerChart(chart, values) {
	myChart = new Chart(chart, {
		type : 'line',
		data : {
			labels: [0],
			datasets: [{
				label: false,
				fill: false,
				lineTension: 0.1,
				backgroundColor: "rgba(75,192,192,0.4)",
				borderColor: "#31708f",
				borderCapStyle: 'butt',
				borderDash: [],
				borderDashOffset: 0.0,
				borderJoinStyle: 'miter',
				pointBorderColor: "#31708f",
				pointBackgroundColor: "#fff",
				pointBorderWidth: 1,
				pointHoverRadius: 5,
				pointHoverBackgroundColor: "rgba(75,192,192,1)",
				pointHoverBorderColor: "rgba(220,220,220,1)",
				pointHoverBorderWidth: 2,
				pointRadius: 1,
				pointHitRadius: 10,
				data: values,
				cubicInterpolationMode: "monotone",
				spanGaps: false
			}],
		},
		options : {
			responsive: true,
			maintainAspectRatio: false,
			scales: {
				xAxes: [{
					display: false
					}]
				},
				legend: {
					display: false,
					labels: {
						display: false
					}
				}
			}
		});
	return myChart;
}


function refreshChart(chart, values, labels) {
	chart.data.datasets[0].data = values;
	chart.data.labels = labels;	
	// console.log(values);
	chart.update();
}


function getSunspecData() {
	var jqxhr = $.getJSON( "/modbus/" );
	jqxhr.done(function( data ) {
				
		$( '#I_DC_Power' ).text(Math.round(data.I_DC_Power[data.I_DC_Power.length-1]));
		$( '#M_AC_Power' ).text(Math.round(data.M_AC_Power[data.M_AC_Power.length-1]));
		$( '#M_AC_Power1' ).text(Math.round(data.M_AC_Power1[data.M_AC_Power1.length-1]));
		$( '#M_AC_Power2' ).text(Math.round(data.M_AC_Power2[data.M_AC_Power2.length-1]));
		$( '#M_AC_Power3' ).text(Math.round(data.M_AC_Power3[data.M_AC_Power3.length-1]));
		$( '#My_Power' ).text(Math.round(data.My_Power[data.My_Power.length-1]));
		
		self = Math.round(data.I_DC_Power[data.I_DC_Power.length-1] / (data.My_Power[data.My_Power.length-1] / 100));
		if(self > 100) { self = 100; }
		$( '#My_Power_self' ).text(self);
		
		input = 0;
		if(data.M_AC_Power[data.M_AC_Power.length-1] > 0) {
			input = Math.round(data.M_AC_Power[data.M_AC_Power.length-1] / (data.I_DC_Power[data.I_DC_Power.length-1] / 100));
		}
		$( '#My_Power_in' ).text(input);
		
		diff();
		
		labels = data.Date;
		refreshChart(chart1, data.I_DC_Power, labels );
		refreshChart(chart2, data.M_AC_Power, labels );
		refreshChart(chart3, data.M_AC_Power1, labels );
		refreshChart(chart4, data.M_AC_Power2, labels );
		refreshChart(chart5, data.M_AC_Power3, labels );
		refreshChart(chart6, data.My_Power, labels );
		
		setTimeout( function() { getSunspecData() }, 5000);
	});
}

function snap() {
	$( '#I_DC_Power_snap').text($( '#I_DC_Power').text());
	$( '#M_AC_Power_snap').text($( '#M_AC_Power').text());
	$( '#M_AC_Power1_snap').text($( '#M_AC_Power1').text());
	$( '#M_AC_Power2_snap').text($( '#M_AC_Power2').text());
	$( '#M_AC_Power3_snap').text($( '#M_AC_Power3').text());
	$( '#My_Power_snap').text($( '#My_Power').text());
	diff();
}

function diff() {
	$( '#I_DC_Power_diff').text($( '#I_DC_Power_snap').text() - $( '#I_DC_Power').text());
	$( '#M_AC_Power_diff').text($( '#M_AC_Power_snap').text() - $( '#M_AC_Power').text());
	$( '#M_AC_Power1_diff').text($( '#M_AC_Power1_snap').text() - $( '#M_AC_Power1').text());
	$( '#M_AC_Power2_diff').text($( '#M_AC_Power2_snap').text() - $( '#M_AC_Power2').text());
	$( '#M_AC_Power3_diff').text($( '#M_AC_Power3_snap').text() - $( '#M_AC_Power3').text());
	$( '#My_Power_diff').text($( '#My_Power_snap').text() - $( '#My_Power').text());
}
