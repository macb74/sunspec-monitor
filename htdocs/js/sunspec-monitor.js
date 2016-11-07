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
	//console.log(values);
	chart.update();
}


function getSunspecData() {
	var jqxhr = $.getJSON( "/modbus/" );
	jqxhr.done(function( data ) {	
		$( '#I_DC_Power' ).text(Math.round(data.I_DC_Power * getDivisor(data.I_DC_Power_Scale)));
		$( '#M_AC_Power' ).text(Math.round(data.M_AC_Power * getDivisor(data.M_AC_Power_Scale)));
		$( '#M_AC_Power1' ).text(Math.round(data.M_AC_Power1 * getDivisor(data.M_AC_Power_Scale)));
		$( '#M_AC_Power2' ).text(Math.round(data.M_AC_Power2 * getDivisor(data.M_AC_Power_Scale)));
		$( '#M_AC_Power3' ).text(Math.round(data.M_AC_Power3 * getDivisor(data.M_AC_Power_Scale)));
		$( '#My_Power' ).text($( '#I_DC_Power' ).text() - $( '#M_AC_Power' ).text());
		
		self = Math.round($( '#I_DC_Power' ).text() / ($( '#My_Power' ).text() / 100));
		if(self > 100) { self = 100; }
		$( '#My_Power_self' ).text(self);
		
		input = 0;
		if($( '#M_AC_Power' ).text() > 0) {
			input = Math.round($( '#M_AC_Power' ).text / ($( '#I_DC_Power' ).text() / 100));
		}
		$( '#My_Power_in' ).text(input);
		
		diff();
		
		if(labels.length < 31) { labels.push(''); }
		
		data1.push($( '#M_AC_Power1' ).text() * -1);
		if(data1.length > 31) { data1.shift(); }

		data2.push($( '#M_AC_Power2' ).text() * -1);
		if(data2.length > 31) { data2.shift(); }
		
		data3.push($( '#M_AC_Power3' ).text() * -1);
		if(data3.length > 31) { data3.shift(); }
		
		data4.push($( '#M_AC_Power' ).text() * -1);
		if(data4.length > 31) { data4.shift(); }

		data5.push($( '#I_DC_Power' ).text());
		if(data5.length > 31) { data5.shift(); }

		data6.push($( '#My_Power' ).text());
		if(data6.length > 31) { data6.shift(); }

		refreshChart(chart1, data1, labels );
		refreshChart(chart2, data2, labels );
		refreshChart(chart3, data3, labels );
		refreshChart(chart4, data4, labels );
		refreshChart(chart5, data5, labels );
		refreshChart(chart6, data6, labels );
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

function getDivisor(i) {
	d = 1;
	switch(i) {
	    case -1:
	        d = 0.1;
	        break;
	    case -2:
	        d = 0.01;
	        break;
	    case -3:
	        d = 0.001;
	        break;
	}
	return d
}
