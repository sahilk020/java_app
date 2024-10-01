"use strict";

// Class definition
var KTWidgets = function () {
    // Statistics widgets
    var initStatisticsWidget3 = function () {
        var charts = document.querySelectorAll('.statistics-widget-3-chart');

        [].slice.call(charts).map(function (element) {
            var height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var color = element.getAttribute('data-kt-chart-color');

            var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
            var baseColor = KTUtil.getCssVariableValue('--kt-' + color);
            var lightColor = KTUtil.getCssVariableValue('--kt-' + color + '-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [30, 45, 32, 70, 40]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 0.3
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 3,
                    colors: [baseColor]
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: '#E4E6EF',
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 80,
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [baseColor],
                markers: {
                    colors: [baseColor],
                    strokeColor: [lightColor],
                    strokeWidth: 3
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initStatisticsWidget4 = function () {
        var charts = document.querySelectorAll('.statistics-widget-4-chart');

        [].slice.call(charts).map(function (element) {
            var height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var color = element.getAttribute('data-kt-chart-color');

            var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
            var baseColor = KTUtil.getCssVariableValue('--kt-' + color);
            var lightColor = KTUtil.getCssVariableValue('--kt-' + color + '-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [40, 40, 30, 30, 35, 35, 50]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 0.3
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 3,
                    colors: [baseColor]
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: '#E4E6EF',
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 60,
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [baseColor],
                markers: {
                    colors: [baseColor],
                    strokeColor: [lightColor],
                    strokeWidth: 3
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    // Charts widgets
    var initChartsWidget1 = function () {
        var element = document.getElementById("kt_charts_widget_1_chart");

        if (!element) {
            return;
        }

        var chart = {
            self: null,
            rendered: false
        };

        var initChart = function () {
            var height = parseInt(KTUtil.css(element, 'height'));
            var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
            var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');
            var baseColor = KTUtil.getCssVariableValue('--kt-primary');
            var secondaryColor = KTUtil.getCssVariableValue('--kt-gray-300');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [44, 55, 57, 56, 61, 58]
                }, {
                    name: 'Revenue',
                    data: [76, 85, 101, 98, 87, 105]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'bar',
                    height: height,
                    toolbar: {
                        show: false
                    }
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: ['30%'],
                        borderRadius: 4
                    },
                },
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                stroke: {
                    show: true,
                    width: 2,
                    colors: ['transparent']
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                fill: {
                    opacity: 1
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [baseColor, secondaryColor],
                grid: {
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    }
                }
            };

            chart.self = new ApexCharts(element, options);
            chart.self.render();
            chart.rendered = true;
        }

        // Init chart
        initChart();

        // Update chart on theme mode change
        KTThemeMode.on("kt.thememode.change", function () {
            if (chart.rendered) {
                chart.self.destroy();
            }

            initChart();
        });
    }

    var initChartsWidget2 = function () {
        var element = document.getElementById("kt_charts_widget_2_chart");

        if (!element) {
            return;
        }

        var chart = {
            self: null,
            rendered: false
        };

        var initChart = function () {
            var height = parseInt(KTUtil.css(element, 'height'));
            var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
            var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');
            var baseColor = KTUtil.getCssVariableValue('--kt-warning');
            var secondaryColor = KTUtil.getCssVariableValue('--kt-gray-300');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [44, 55, 57, 56, 61, 58]
                }, {
                    name: 'Revenue',
                    data: [76, 85, 101, 98, 87, 105]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'bar',
                    height: height,
                    toolbar: {
                        show: false
                    }
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: ['30%'],
                        borderRadius: 4
                    },
                },
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                stroke: {
                    show: true,
                    width: 2,
                    colors: ['transparent']
                },
                xaxis: {
                    categories: ['00-04', '04-08', '08-12', '12-16', '16-20', '20-24'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                fill: {
                    opacity: 1
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [baseColor, secondaryColor],
                grid: {
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    }
                }
            };

            chart.self = new ApexCharts(element, options);
            chart.self.render();
            chart.rendered = true;
        }

        // Init chart
        initChart();

        // Update chart on theme mode change
        KTThemeMode.on("kt.thememode.change", function () {
            if (chart.rendered) {
                chart.self.destroy();
            }

            initChart();
        });
    }

    var initChartsWidget4 = function () {
        var element = document.getElementById("kt_charts_widget_4_chart");

        if (!element) {
            return;
        }

        var chart = {
            self: null,
            rendered: false
        };

        var initChart = function () {
            var height = parseInt(KTUtil.css(element, 'height'));
            var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
            var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');

            var baseColor = KTUtil.getCssVariableValue('--kt-success');
            var baseLightColor = KTUtil.getCssVariableValue('--kt-success-light');
            var secondaryColor = KTUtil.getCssVariableValue('--kt-warning');
            var secondaryLightColor = KTUtil.getCssVariableValue('--kt-warning-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [60, 50, 80, 40, 100, 60]
                }, {
                    name: 'Revenue',
                    data: [70, 60, 110, 40, 50, 70]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: 350,
                    toolbar: {
                        show: false
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 1
                },
                stroke: {
                    curve: 'smooth'
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        position: 'front',
                        stroke: {
                            color: labelColor,
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [baseColor, secondaryColor],
                grid: {
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    }
                },
                markers: {
                    colors: [baseLightColor, secondaryLightColor],
                    strokeColor: [baseLightColor, secondaryLightColor],
                    strokeWidth: 3
                }
            };

            chart.self = new ApexCharts(element, options);
            chart.self.render();
            chart.rendered = true;
        }

        // Init chart
        initChart();

        // Update chart on theme mode change
        KTThemeMode.on("kt.thememode.change", function () {
            if (chart.rendered) {
                chart.self.destroy();
            }

            initChart();
        });
    }

    var initChartsWidget5 = function () {
        var element = document.getElementById("kt_charts_widget_5_chart");

        if (!element) {
            return;
        }

        var chart = {
            self: null,
            rendered: false
        };

        var initChart = function () {
            var height = parseInt(KTUtil.css(element, 'height'));
            var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
            var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');

            var baseColor = KTUtil.getCssVariableValue('--kt-primary');
            var secondaryColor = KTUtil.getCssVariableValue('--kt-info');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [40, 50, 65, 70, 50, 30]
                }, {
                    name: 'Revenue',
                    data: [-30, -40, -55, -60, -40, -20]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'bar',
                    stacked: true,
                    height: 350,
                    toolbar: {
                        show: false
                    }
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: ['12%'],
                        borderRadius: [6, 6]
                    },
                },
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                stroke: {
                    show: true,
                    width: 2,
                    colors: ['transparent']
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: -80,
                    max: 80,
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                fill: {
                    opacity: 1
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [baseColor, secondaryColor],
                grid: {
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    }
                }
            };

            chart.self = new ApexCharts(element, options);
            chart.self.render();
            chart.rendered = true;
        }

        // Init chart
        initChart();

        // Update chart on theme mode change
        KTThemeMode.on("kt.thememode.change", function () {
            if (chart.rendered) {
                chart.self.destroy();
            }

            initChart();
        });
    }

    var initChartsWidget6 = function () {
        var element = document.getElementById("kt_charts_widget_6_chart");

        if (!element) {
            return;
        }

        var chart = {
            self: null,
            rendered: false
        };

        var initChart = function () {
            var height = parseInt(KTUtil.css(element, 'height'));
            var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
            var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');

            var baseColor = KTUtil.getCssVariableValue('--kt-primary');
            var baseLightColor = KTUtil.getCssVariableValue('--kt-primary-light');
            var secondaryColor = KTUtil.getCssVariableValue('--kt-info');

            var options = {
                series: [{
                    name: 'Net Profit',
                    type: 'bar',
                    stacked: true,
                    data: [40, 50, 65, 70, 50, 30]
                }, {
                    name: 'Revenue',
                    type: 'bar',
                    stacked: true,
                    data: [20, 20, 25, 30, 30, 20]
                }, {
                    name: 'Expenses',
                    type: 'area',
                    data: [50, 80, 60, 90, 50, 70]
                }],
                chart: {
                    fontFamily: 'inherit',
                    stacked: true,
                    height: 350,
                    toolbar: {
                        show: false
                    }
                },
                plotOptions: {
                    bar: {
                        stacked: true,
                        horizontal: false,
                        borderRadius: 4,
                        columnWidth: ['12%']
                    },
                },
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 2,
                    colors: ['transparent']
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    max: 120,
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                fill: {
                    opacity: 1
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [baseColor, secondaryColor, baseLightColor],
                grid: {
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    },
                    padding: {
                        top: 0,
                        right: 0,
                        bottom: 0,
                        left: 0
                    }
                }
            };

            chart.self = new ApexCharts(element, options);
            chart.self.render();
            chart.rendered = true;
        }

        // Init chart
        initChart();

        // Update chart on theme mode change
        KTThemeMode.on("kt.thememode.change", function () {
            if (chart.rendered) {
                chart.self.destroy();
            }

            initChart();
        });
    }

    var initChartsWidget7 = function () {
        var element = document.getElementById("kt_charts_widget_7_chart");

        if (!element) {
            return;
        }

        var chart = {
            self: null,
            rendered: false
        };

        var initChart = function () {

            var height = parseInt(KTUtil.css(element, 'height'));

            var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
            var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');
            var strokeColor = KTUtil.getCssVariableValue('--kt-gray-300');

            var color1 = KTUtil.getCssVariableValue('--kt-warning');
            var color1Light = KTUtil.getCssVariableValue('--kt-warning-light');

            var color2 = KTUtil.getCssVariableValue('--kt-success');
            var color2Light = KTUtil.getCssVariableValue('--kt-success-light');

            var color3 = KTUtil.getCssVariableValue('--kt-primary');
            var color3Light = KTUtil.getCssVariableValue('--kt-primary-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [30, 30, 50, 50, 35, 35]
                }, {
                    name: 'Revenue',
                    data: [55, 20, 20, 20, 70, 70]
                }, {
                    name: 'Expenses',
                    data: [60, 60, 40, 40, 30, 30]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 1
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 2,
                    colors: [color1, 'transparent', 'transparent']
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: strokeColor,
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [color1, color2, color3],
                grid: {
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    }
                },
                markers: {
                    colors: [color1Light, color2Light, color3Light],
                    strokeColor: [color1, color2, color3],
                    strokeWidth: 3
                }
            };

            chart.self = new ApexCharts(element, options);
            chart.self.render();
            chart.rendered = true;
        }

        // Init chart
        initChart();

        // Update chart on theme mode change
        KTThemeMode.on("kt.thememode.change", function () {
            if (chart.rendered) {
                chart.self.destroy();
            }

            initChart();
        });
    }

    var initChartsWidget8 = function () {
        var element = document.getElementById("kt_charts_widget_8_chart");

        if (!element) {
            return;
        }

        var chart = {
            self: null,
            rendered: false
        };

        var initChart = function () {
            var height = parseInt(KTUtil.css(element, 'height'));

            var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
            var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');
            var strokeColor = KTUtil.getCssVariableValue('--kt-gray-300');

            var color1 = KTUtil.getCssVariableValue('--kt-warning');
            var color1Light = KTUtil.getCssVariableValue('--kt-warning-light');

            var color2 = KTUtil.getCssVariableValue('--kt-success');
            var color2Light = KTUtil.getCssVariableValue('--kt-success-light');

            var color3 = KTUtil.getCssVariableValue('--kt-primary');
            var color3Light = KTUtil.getCssVariableValue('--kt-primary-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [30, 30, 50, 50, 35, 35]
                }, {
                    name: 'Revenue',
                    data: [55, 20, 20, 20, 70, 70]
                }, {
                    name: 'Expenses',
                    data: [60, 60, 40, 40, 30, 30]
                }, ],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 1
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 2,
                    colors: [color1, color2, color3]
                },
                xaxis: {
                    x: 0,
                    offsetX: 0,
                    offsetY: 0,
                    padding: {
                        left: 0,
                        right: 0,
                        top: 0,
                    },
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: strokeColor,
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    y: 0,
                    offsetX: 0,
                    offsetY: 0,
                    padding: {
                        left: 0,
                        right: 0
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [color1Light, color2Light, color3Light],
                grid: {
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    padding: {
                        top: 0,
                        bottom: 0,
                        left: 0,
                        right: 0
                    }
                },
                markers: {
                    colors: [color1, color2, color3],
                    strokeColor: [color1, color2, color3],
                    strokeWidth: 3
                }
            };

            chart.self = new ApexCharts(element, options);
            chart.self.render();
            chart.rendered = true;
        }

        // Init chart
        initChart();

        // Update chart on theme mode change
        KTThemeMode.on("kt.thememode.change", function () {
            if (chart.rendered) {
                chart.self.destroy();
            }

            initChart();
        });
    }

    // Mixed widgets
    var initMixedWidget2 = function () {
        var charts = document.querySelectorAll('.mixed-widget-2-chart');

        var color;
        var strokeColor;
        var height;
        var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
        var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');
        var options;
        var chart;

        [].slice.call(charts).map(function (element) {
            height = parseInt(KTUtil.css(element, 'height'));
            color = KTUtil.getCssVariableValue('--kt-' + element.getAttribute("data-kt-color"));
            strokeColor = KTUtil.colorDarken(color, 15);

            options = {
                series: [{
                    name: 'Net Profit',
                    data: [30, 45, 32, 70, 40, 40, 40]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    },
                    dropShadow: {
                        enabled: true,
                        enabledOnSeries: undefined,
                        top: 5,
                        left: 0,
                        blur: 3,
                        color: strokeColor,
                        opacity: 0.5
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 0
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 3,
                    colors: [strokeColor]
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: borderColor,
                            width: 1,
                            dashArray: 3
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 80,
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px',
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    },
                    marker: {
                        show: false
                    }
                },
                colors: ['transparent'],
                markers: {
                    colors: [color],
                    strokeColor: [strokeColor],
                    strokeWidth: 3
                }
            };

            chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget3 = function () {
        var charts = document.querySelectorAll('.mixed-widget-3-chart');

        [].slice.call(charts).map(function (element) {
            var height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var color = element.getAttribute('data-kt-chart-color');

            var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
            var strokeColor = KTUtil.getCssVariableValue('--kt-' + 'gray-300');
            var baseColor = KTUtil.getCssVariableValue('--kt-' + color);
            var lightColor = KTUtil.getCssVariableValue('--kt-' + color + '-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [30, 25, 45, 30, 55, 55]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 1
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 3,
                    colors: [baseColor]
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: strokeColor,
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 60,
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [lightColor],
                markers: {
                    colors: [lightColor],
                    strokeColor: [baseColor],
                    strokeWidth: 3
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget4 = function () {
        var charts = document.querySelectorAll('.mixed-widget-4-chart');

        [].slice.call(charts).map(function (element) {
            var height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var color = element.getAttribute('data-kt-chart-color');

            var baseColor = KTUtil.getCssVariableValue('--kt-' + color);
            var lightColor = KTUtil.getCssVariableValue('--kt-' + color + '-light');
            var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-700');

            var options = {
                series: [74],
                chart: {
                    fontFamily: 'inherit',
                    height: height,
                    type: 'radialBar',
                },
                plotOptions: {
                    radialBar: {
                        hollow: {
                            margin: 0,
                            size: "65%"
                        },
                        dataLabels: {
                            showOn: "always",
                            name: {
                                show: false,
                                fontWeight: '700'
                            },
                            value: {
                                color: labelColor,
                                fontSize: "30px",
                                fontWeight: '700',
                                offsetY: 12,
                                show: true,
                                formatter: function (val) {
                                    return val + '%';
                                }
                            }
                        },
                        track: {
                            background: lightColor,
                            strokeWidth: '100%'
                        }
                    }
                },
                colors: [baseColor],
                stroke: {
                    lineCap: "round",
                },
                labels: ["Progress"]
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget5 = function () {
        var charts = document.querySelectorAll('.mixed-widget-5-chart');

        [].slice.call(charts).map(function (element) {
            var height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var color = element.getAttribute('data-kt-chart-color');

            var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
            var strokeColor = KTUtil.getCssVariableValue('--kt-' + 'gray-300');
            var baseColor = KTUtil.getCssVariableValue('--kt-' + color);
            var lightColor = KTUtil.getCssVariableValue('--kt-' + color + '-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [30, 30, 60, 25, 25, 40]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 1
                },
                fill1: {
                    type: 'gradient',
                    opacity: 1,
                    gradient: {
                        type: "vertical",
                        shadeIntensity: 0.5,
                        gradientToColors: undefined,
                        inverseColors: true,
                        opacityFrom: 1,
                        opacityTo: 0.375,
                        stops: [25, 50, 100],
                        colorStops: []
                    }
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 3,
                    colors: [baseColor]
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: strokeColor,
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 65,
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [lightColor],
                markers: {
                    colors: [lightColor],
                    strokeColor: [baseColor],
                    strokeWidth: 3
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget6 = function () {
        var charts = document.querySelectorAll('.mixed-widget-6-chart');

        [].slice.call(charts).map(function (element) {
            var height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var color = element.getAttribute('data-kt-chart-color');

            var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
            var strokeColor = KTUtil.getCssVariableValue('--kt-' + 'gray-300');
            var baseColor = KTUtil.getCssVariableValue('--kt-' + color);
            var lightColor = KTUtil.getCssVariableValue('--kt-' + color + '-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [30, 25, 45, 30, 55, 55]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 1
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 3,
                    colors: [baseColor]
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: strokeColor,
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 60,
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [lightColor],
                markers: {
                    colors: [lightColor],
                    strokeColor: [baseColor],
                    strokeWidth: 3
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget7 = function () {
        var charts = document.querySelectorAll('.mixed-widget-7-chart');

        [].slice.call(charts).map(function (element) {
            var height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var color = element.getAttribute('data-kt-chart-color');

            var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
            var strokeColor = KTUtil.getCssVariableValue('--kt-' + 'gray-300');
            var baseColor = KTUtil.getCssVariableValue('--kt-' + color);
            var lightColor = KTUtil.getCssVariableValue('--kt-' + color + '-light');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [15, 25, 15, 40, 20, 50]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'solid',
                    opacity: 1
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 3,
                    colors: [baseColor]
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: strokeColor,
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 60,
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: [lightColor],
                markers: {
                    colors: [lightColor],
                    strokeColor: [baseColor],
                    strokeWidth: 3
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget10 = function () {
        var charts = document.querySelectorAll('.mixed-widget-10-chart');

        var color;
        var height;
        var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
        var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');
        var baseLightColor;
        var secondaryColor = KTUtil.getCssVariableValue('--kt-gray-300');
        var baseColor;
        var options;
        var chart;

        [].slice.call(charts).map(function (element) {
            color = element.getAttribute("data-kt-color");
            height = parseInt(KTUtil.css(element, 'height'));
            baseColor = KTUtil.getCssVariableValue('--kt-' + color);

            options = {
                series: [{
                    name: 'Net Profit',
                    data: [50, 60, 70, 80, 60, 50, 70, 60]
                }, {
                    name: 'Revenue',
                    data: [50, 60, 70, 80, 60, 50, 70, 60]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'bar',
                    height: height,
                    toolbar: {
                        show: false
                    }
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: ['50%'],
                        borderRadius: 4
                    },
                },
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                stroke: {
                    show: true,
                    width: 2,
                    colors: ['transparent']
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    y: 0,
                    offsetX: 0,
                    offsetY: 0,
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                fill: {
                    type: 'solid'
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " revenue"
                        }
                    }
                },
                colors: [baseColor, secondaryColor],
                grid: {
                    padding: {
                        top: 10
                    },
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    }
                }
            };

            chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget12 = function () {
        var charts = document.querySelectorAll('.mixed-widget-12-chart');

        var color;
        var strokeColor;
        var height;
        var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
        var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');
        var options;
        var chart;

        [].slice.call(charts).map(function (element) {
            height = parseInt(KTUtil.css(element, 'height'));

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [35, 65, 75, 55, 45, 60, 55]
                }, {
                    name: 'Revenue',
                    data: [40, 70, 80, 60, 50, 65, 60]
                }],
                chart: {
                    fontFamily: 'inherit',
                    type: 'bar',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    sparkline: {
                        enabled: true
                    },
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: ['30%'],
                        borderRadius: 2
                    }
                },
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                stroke: {
                    show: true,
                    width: 1,
                    colors: ['transparent']
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 100,
                    labels: {
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                fill: {
                    type: ['solid', 'solid'],
                    opacity: [0.25, 1]
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    },
                    marker: {
                        show: false
                    }
                },
                colors: ['#ffffff', '#ffffff'],
                grid: {
                    borderColor: borderColor,
                    strokeDashArray: 4,
                    yaxis: {
                        lines: {
                            show: true
                        }
                    },
                    padding: {
                        left: 20,
                        right: 20
                    }
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render()
        });
    }

    var initMixedWidget13 = function () {
        var height;
        var charts = document.querySelectorAll('.mixed-widget-13-chart');

        [].slice.call(charts).map(function (element) {
            height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
            var strokeColor = KTUtil.getCssVariableValue('--kt-' + 'gray-300');

            var options = {
                series: [{
                    name: 'Net Profit',
                    data: [15, 25, 15, 40, 20, 50]
                }],
                grid: {
                    show: false,
                    padding: {
                        top: 0,
                        bottom: 0,
                        left: 0,
                        right: 0
                    }
                },
                chart: {
                    fontFamily: 'inherit',
                    type: 'area',
                    height: height,
                    toolbar: {
                        show: false
                    },
                    zoom: {
                        enabled: false
                    },
                    sparkline: {
                        enabled: true
                    }
                },
                plotOptions: {},
                legend: {
                    show: false
                },
                dataLabels: {
                    enabled: false
                },
                fill: {
                    type: 'gradient',
                    gradient: {
                        opacityFrom: 0.4,
                        opacityTo: 0,
                        stops: [20, 120, 120, 120]
                    }
                },
                stroke: {
                    curve: 'smooth',
                    show: true,
                    width: 3,
                    colors: ['#FFFFFF']
                },
                xaxis: {
                    categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                    axisBorder: {
                        show: false,
                    },
                    axisTicks: {
                        show: false
                    },
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    },
                    crosshairs: {
                        show: false,
                        position: 'front',
                        stroke: {
                            color: strokeColor,
                            width: 1,
                            dashArray: 3
                        }
                    },
                    tooltip: {
                        enabled: true,
                        formatter: undefined,
                        offsetY: 0,
                        style: {
                            fontSize: '12px'
                        }
                    }
                },
                yaxis: {
                    min: 0,
                    max: 60,
                    labels: {
                        show: false,
                        style: {
                            colors: labelColor,
                            fontSize: '12px'
                        }
                    }
                },
                states: {
                    normal: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    hover: {
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    },
                    active: {
                        allowMultipleDataPointsSelection: false,
                        filter: {
                            type: 'none',
                            value: 0
                        }
                    }
                },
                tooltip: {
                    style: {
                        fontSize: '12px'
                    },
                    y: {
                        formatter: function (val) {
                            return "$" + val + " thousands"
                        }
                    }
                },
                colors: ['#ffffff'],
                markers: {
                    colors: [labelColor],
                    strokeColor: [strokeColor],
                    strokeWidth: 3
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget14 = function () {
        var charts = document.querySelectorAll('.mixed-widget-14-chart');
        var options;
        var chart;
        var height;

        [].slice.call(charts).map(function (element) {
            height = parseInt(KTUtil.css(element, 'height'));
            var labelColor = KTUtil.getCssVariableValue('--kt-gray-800');

            options = {
                series: [{
                    name: 'Inflation',
                    data: [1, 2.1, 1, 2.1, 4.1, 6.1, 4.1, 4.1, 2.1, 4.1, 2.1, 3.1, 1, 1, 2.1]
                }],
                chart: {
                    fontFamily: 'inherit',
                    height: height,
                    type: 'bar',
                    toolbar: {
                        show: false
                    }
                },
                grid: {
                    show: false,
                    padding: {
                        top: 0,
                        bottom: 0,
                        left: 0,
                        right: 0
                    }
                },
                colors: ['#ffffff'],
                plotOptions: {
                    bar: {
                        borderRadius: 2.5,
                        dataLabels: {
                            position: 'top', // top, center, bottom
                        },
                        columnWidth: '20%'
                    }
                },
                dataLabels: {
                    enabled: false,
                    formatter: function (val) {
                        return val + "%";
                    },
                    offsetY: -20,
                    style: {
                        fontSize: '12px',
                        colors: ["#304758"]
                    }
                },
                xaxis: {
                    labels: {
                        show: false,
                    },
                    categories: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar"],
                    position: 'top',
                    axisBorder: {
                        show: false
                    },
                    axisTicks: {
                        show: false
                    },
                    crosshairs: {
                        show: false
                    },
                    tooltip: {
                        enabled: false
                    }
                },
                yaxis: {
                    show: false,
                    axisBorder: {
                        show: false
                    },
                    axisTicks: {
                        show: false,
                        background: labelColor
                    },
                    labels: {
                        show: false,
                        formatter: function (val) {
                            return val + "%";
                        }
                    }
                }
            };

            chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget16 = function () {
        var element = document.getElementById("kt_charts_mixed_widget_16_chart");
        var height = parseInt(KTUtil.css(element, 'height'));

        if (!element) {
            return;
        }

        var options = {
            labels: ["Total Members"],
            series: [74],
            chart: {
                fontFamily: 'inherit',
                height: height,
                type: 'radialBar',
                offsetY: 0
            },
            plotOptions: {
                radialBar: {
                    startAngle: -90,
                    endAngle: 90,

                    hollow: {
                        margin: 0,
                        size: "70%"
                    },
                    dataLabels: {
                        showOn: "always",
                        name: {
                            show: true,
                            fontSize: "13px",
                            fontWeight: "700",
                            offsetY: -5,
                            color: KTUtil.getCssVariableValue('--kt-gray-500')
                        },
                        value: {
                            color: KTUtil.getCssVariableValue('--kt-gray-700'),
                            fontSize: "30px",
                            fontWeight: "700",
                            offsetY: -40,
                            show: true
                        }
                    },
                    track: {
                        background: KTUtil.getCssVariableValue('--kt-primary-light'),
                        strokeWidth: '100%'
                    }
                }
            },
            colors: [KTUtil.getCssVariableValue('--kt-primary')],
            stroke: {
                lineCap: "round",
            }
        };

        var chart = new ApexCharts(element, options);
        chart.render();
    }

    var initMixedWidget17 = function () {
        var charts = document.querySelectorAll('.mixed-widget-17-chart');

        [].slice.call(charts).map(function (element) {
            var height = parseInt(KTUtil.css(element, 'height'));

            if (!element) {
                return;
            }

            var color = element.getAttribute('data-kt-chart-color');

            var options = {
                labels: ["Total Orders"],
                series: [75],
                chart: {
                    fontFamily: 'inherit',
                    height: height,
                    type: 'radialBar',
                    offsetY: 0
                },
                plotOptions: {
                    radialBar: {
                        startAngle: -90,
                        endAngle: 90,
                        hollow: {
                            margin: 0,
                            size: "55%"
                        },
                        dataLabels: {
                            showOn: "always",
                            name: {
                                show: true,
                                fontSize: "12px",
                                fontWeight: "700",
                                offsetY: -5,
                                color: KTUtil.getCssVariableValue('--kt-gray-500')
                            },
                            value: {
                                color: KTUtil.getCssVariableValue('--kt-gray-900'),
                                fontSize: "24px",
                                fontWeight: "600",
                                offsetY: -40,
                                show: true,
                                formatter: function (value) {
                                    return '8,346';
                                }
                            }
                        },
                        track: {
                            background: KTUtil.getCssVariableValue('--kt-gray-300'),
                            strokeWidth: '100%'
                        }
                    }
                },
                colors: [KTUtil.getCssVariableValue('--kt-' + color)],
                stroke: {
                    lineCap: "round",
                }
            };

            var chart = new ApexCharts(element, options);
            chart.render();
        });
    }

    var initMixedWidget18 = function () {
        var element = document.getElementById("kt_charts_mixed_widget_18_chart");
        var height = parseInt(KTUtil.css(element, 'height'));

        if (!element) {
            return;
        }

        var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
        var strokeColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
        var fillColor = KTThemeMode.getMode() === "dark" ? KTUtil.getCssVariableValue('--kt-gray-200') : '#D6D6E0';

        var options = {
            series: [{
                name: 'Net Profit',
                data: [30, 25, 45, 30, 55, 55]
            }],
            chart: {
                fontFamily: 'inherit',
                type: 'area',
                height: height,
                toolbar: {
                    show: false
                },
                zoom: {
                    enabled: false
                },
                sparkline: {
                    enabled: true
                }
            },
            plotOptions: {},
            legend: {
                show: false
            },
            dataLabels: {
                enabled: false
            },
            fill: {
                type: 'solid',
                opacity: 1
            },
            stroke: {
                curve: 'smooth',
                show: true,
                width: 3,
                colors: [strokeColor]
            },
            xaxis: {
                categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                axisBorder: {
                    show: false,
                },
                axisTicks: {
                    show: false
                },
                labels: {
                    show: false,
                    style: {
                        colors: labelColor,
                        fontSize: '12px'
                    }
                },
                crosshairs: {
                    show: false,
                    position: 'front',
                    stroke: {
                        color: strokeColor,
                        width: 1,
                        dashArray: 3
                    }
                },
                tooltip: {
                    enabled: true,
                    formatter: undefined,
                    offsetY: 0,
                    style: {
                        fontSize: '12px'
                    }
                }
            },
            yaxis: {
                min: 0,
                max: 60,
                labels: {
                    show: false,
                    style: {
                        colors: labelColor,
                        fontSize: '12px'
                    }
                }
            },
            states: {
                normal: {
                    filter: {
                        type: 'none',
                        value: 0
                    }
                },
                hover: {
                    filter: {
                        type: 'none',
                        value: 0
                    }
                },
                active: {
                    allowMultipleDataPointsSelection: false,
                    filter: {
                        type: 'none',
                        value: 0
                    }
                }
            },
            tooltip: {
                style: {
                    fontSize: '12px'
                },
                y: {
                    formatter: function (val) {
                        return "$" + val + " thousands"
                    }
                }
            },
            colors: [fillColor],
            markers: {
                colors: [fillColor],
                strokeColor: [strokeColor],
                strokeWidth: 3
            }
        };

        var chart = new ApexCharts(element, options);
        chart.render();
    }

    var initMixedWidget19 = function () {
        var element = document.getElementById("kt_charts_mixed_widget_19_chart");
        var height = parseInt(KTUtil.css(element, 'height'));

        if (!element) {
            return;
        }

        var labelColor = KTUtil.getCssVariableValue('--kt-' + 'gray-800');
        var strokeColor = KTUtil.getCssVariableValue('--kt-' + 'info');
        var fillColor = KTUtil.getCssVariableValue('--kt-info-light')

        var options = {
            series: [{
                name: 'Net Profit',
                data: [30, 25, 45, 30, 55, 55]
            }],
            chart: {
                fontFamily: 'inherit',
                type: 'area',
                height: height,
                toolbar: {
                    show: false
                },
                zoom: {
                    enabled: false
                },
                sparkline: {
                    enabled: true
                }
            },
            plotOptions: {},
            legend: {
                show: false
            },
            dataLabels: {
                enabled: false
            },
            fill: {
                type: 'solid',
                opacity: 1
            },
            stroke: {
                curve: 'smooth',
                show: true,
                width: 3,
                colors: [strokeColor]
            },
            xaxis: {
                categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                axisBorder: {
                    show: false,
                },
                axisTicks: {
                    show: false
                },
                labels: {
                    show: false,
                    style: {
                        colors: labelColor,
                        fontSize: '12px'
                    }
                },
                crosshairs: {
                    show: false,
                    position: 'front',
                    stroke: {
                        color: strokeColor,
                        width: 1,
                        dashArray: 3
                    }
                },
                tooltip: {
                    enabled: true,
                    formatter: undefined,
                    offsetY: 0,
                    style: {
                        fontSize: '12px'
                    }
                }
            },
            yaxis: {
                min: 0,
                max: 60,
                labels: {
                    show: false,
                    style: {
                        colors: labelColor,
                        fontSize: '12px'
                    }
                }
            },
            states: {
                normal: {
                    filter: {
                        type: 'none',
                        value: 0
                    }
                },
                hover: {
                    filter: {
                        type: 'none',
                        value: 0
                    }
                },
                active: {
                    allowMultipleDataPointsSelection: false,
                    filter: {
                        type: 'none',
                        value: 0
                    }
                }
            },
            tooltip: {
                style: {
                    fontSize: '12px'
                },
                y: {
                    formatter: function (val) {
                        return "$" + val + " thousands"
                    }
                }
            },
            colors: [fillColor],
            markers: {
                colors: [fillColor],
                strokeColor: [strokeColor],
                strokeWidth: 3
            }
        };

        var chart = new ApexCharts(element, options);
        chart.render();
    }

    // Feeds Widgets
    var initFeedWidget1 = function () {
        var formEl = document.querySelector("#kt_forms_widget_1_form");
        var editorId = 'kt_forms_widget_1_editor';

        if (!formEl) {
            return;
        }

        // init editor
        var options = {
            modules: {
                toolbar: {
                    container: "#kt_forms_widget_1_editor_toolbar"
                }
            },
            placeholder: 'What is on your mind ?',
            theme: 'snow'
        };

        if (!formEl) {
            return;
        }

        // Init editor
        var editorObj = new Quill('#' + editorId, options);
    }

    var initFeedsWidget4 = function () {
        var btn = document.querySelector('#kt_widget_5_load_more_btn');
        var widget5 = document.querySelector('#kt_widget_5');

        if (btn) {
            btn.addEventListener('click', function (e) {
                e.preventDefault();
                btn.setAttribute('data-kt-indicator', 'on');

                setTimeout(function () {
                    btn.removeAttribute('data-kt-indicator');
                    widget5.classList.remove('d-none');
                    btn.classList.add('d-none');

                    KTUtil.scrollTo(widget5, 200);
                }, 2000);
            });
        }
    }

    // Calendar
    var initCalendarWidget1 = function () {
        if (typeof FullCalendar === 'undefined' || !document.querySelector('#kt_calendar_widget_1')) {
            return;
        }

        var todayDate = moment().startOf('day');
        var YM = todayDate.format('YYYY-MM');
        var YESTERDAY = todayDate.clone().subtract(1, 'day').format('YYYY-MM-DD');
        var TODAY = todayDate.format('YYYY-MM-DD');
        var TOMORROW = todayDate.clone().add(1, 'day').format('YYYY-MM-DD');

        var calendarEl = document.getElementById('kt_calendar_widget_1');
        var calendar = new FullCalendar.Calendar(calendarEl, {
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay,listMonth'
            },

            height: 800,
            contentHeight: 780,
            aspectRatio: 3, // see: https://fullcalendar.io/docs/aspectRatio

            nowIndicator: true,
            now: TODAY + 'T09:25:00', // just for demo

            views: {
                dayGridMonth: {
                    buttonText: 'month'
                },
                timeGridWeek: {
                    buttonText: 'week'
                },
                timeGridDay: {
                    buttonText: 'day'
                }
            },

            initialView: 'dayGridMonth',
            initialDate: TODAY,

            editable: true,
            dayMaxEvents: true, // allow "more" link when too many events
            navLinks: true,
            events: [{
                    title: 'All Day Event',
                    start: YM + '-01',
                    description: 'Toto lorem ipsum dolor sit incid idunt ut',
                    className: "fc-event-danger fc-event-solid-warning"
                },
                {
                    title: 'Reporting',
                    start: YM + '-14T13:30:00',
                    description: 'Lorem ipsum dolor incid idunt ut labore',
                    end: YM + '-14',
                    className: "fc-event-success"
                },
                {
                    title: 'Company Trip',
                    start: YM + '-02',
                    description: 'Lorem ipsum dolor sit tempor incid',
                    end: YM + '-03',
                    className: "fc-event-primary"
                },
                {
                    title: 'ICT Expo 2017 - Product Release',
                    start: YM + '-03',
                    description: 'Lorem ipsum dolor sit tempor inci',
                    end: YM + '-05',
                    className: "fc-event-light fc-event-solid-primary"
                },
                {
                    title: 'Dinner',
                    start: YM + '-12',
                    description: 'Lorem ipsum dolor sit amet, conse ctetur',
                    end: YM + '-10'
                },
                {
                    id: 999,
                    title: 'Repeating Event',
                    start: YM + '-09T16:00:00',
                    description: 'Lorem ipsum dolor sit ncididunt ut labore',
                    className: "fc-event-danger"
                },
                {
                    id: 1000,
                    title: 'Repeating Event',
                    description: 'Lorem ipsum dolor sit amet, labore',
                    start: YM + '-16T16:00:00'
                },
                {
                    title: 'Conference',
                    start: YESTERDAY,
                    end: TOMORROW,
                    description: 'Lorem ipsum dolor eius mod tempor labore',
                    className: "fc-event-primary"
                },
                {
                    title: 'Meeting',
                    start: TODAY + 'T10:30:00',
                    end: TODAY + 'T12:30:00',
                    description: 'Lorem ipsum dolor eiu idunt ut labore'
                },
                {
                    title: 'Lunch',
                    start: TODAY + 'T12:00:00',
                    className: "fc-event-info",
                    description: 'Lorem ipsum dolor sit amet, ut labore'
                },
                {
                    title: 'Meeting',
                    start: TODAY + 'T14:30:00',
                    className: "fc-event-warning",
                    description: 'Lorem ipsum conse ctetur adipi scing'
                },
                {
                    title: 'Happy Hour',
                    start: TODAY + 'T17:30:00',
                    className: "fc-event-info",
                    description: 'Lorem ipsum dolor sit amet, conse ctetur'
                },
                {
                    title: 'Dinner',
                    start: TOMORROW + 'T05:00:00',
                    className: "fc-event-solid-danger fc-event-light",
                    description: 'Lorem ipsum dolor sit ctetur adipi scing'
                },
                {
                    title: 'Birthday Party',
                    start: TOMORROW + 'T07:00:00',
                    className: "fc-event-primary",
                    description: 'Lorem ipsum dolor sit amet, scing'
                },
                {
                    title: 'Click for Google',
                    url: 'http://google.com/',
                    start: YM + '-28',
                    className: "fc-event-solid-info fc-event-light",
                    description: 'Lorem ipsum dolor sit amet, labore'
                }
            ]
        });

        calendar.render();
    }

    // Daterangepicker
    var initDaterangepicker = function () {
        if (!document.querySelector('#kt_dashboard_daterangepicker')) {
            return;
        }

        var picker = $('#kt_dashboard_daterangepicker');
        var start = moment();
        var end = moment();

        function cb(start, end, label) {
            var title = '';
            var range = '';

            if ((end - start) < 100 || label == 'Today') {
                title = 'Today:';
                range = start.format('MMM D');
            } else if (label == 'Yesterday') {
                title = 'Yesterday:';
                range = start.format('MMM D');
            } else {
                range = start.format('MMM D') + ' - ' + end.format('MMM D');
            }

            $('#kt_dashboard_daterangepicker_date').html(range);
            $('#kt_dashboard_daterangepicker_title').html(title);
        }

        picker.daterangepicker({
            direction: KTUtil.isRTL(),
            startDate: start,
            endDate: end,
            opens: 'left',
            applyClass: 'btn-primary',
            cancelClass: 'btn-light-primary',
            ranges: {
                'Today': [moment(), moment()],
                'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                'Last 7 Days': [moment().subtract(6, 'days'), moment()],
                'Last 30 Days': [moment().subtract(29, 'days'), moment()],
                'This Month': [moment().startOf('month'), moment().endOf('month')],
                'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
            }
        }, cb);

        cb(start, end, '');
    }

    // Dark mode toggler
    var initDarkModeToggle = function () {
        var toggle = document.querySelector('#kt_user_menu_dark_mode_toggle');

        if (toggle) {
            toggle.addEventListener('click', function () {
                window.location.href = this.getAttribute('data-kt-url');
            });
        }
    }

    // Public methods
    return {
        init: function () {
            // Daterangepicker
            initDaterangepicker();

            // Dark Mode
            initDarkModeToggle();

            // Statistics widgets
            initStatisticsWidget3();
            initStatisticsWidget4();

            // Charts widgets
            initChartsWidget1();
            initChartsWidget2();
            /*initChartsWidget4();*/
            initChartsWidget5();
            initChartsWidget6();
            initChartsWidget7();
            initChartsWidget8();

            // Mixed widgets
            initMixedWidget2();
            initMixedWidget3();
            initMixedWidget4();
            initMixedWidget5();
            initMixedWidget6();
            initMixedWidget7();
            initMixedWidget10();
            initMixedWidget12();
            initMixedWidget13();
            initMixedWidget14();
            initMixedWidget16();
            initMixedWidget17();
            initMixedWidget18();
            initMixedWidget19();

            // Feeds
            initFeedWidget1();
            initFeedsWidget4();

            // Calendar
            initCalendarWidget1();
        }
    }
}();

// Webpack support
if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
    module.exports = KTWidgets;
}

// On document ready
KTUtil.onDOMContentLoaded(function () {
    KTWidgets.init();
});

function initChartsWidget4(data) {
    var element = document.getElementById("kt_charts_widget_3_chart");
    element.innerHTML = "";
    let categoryArr = [];
    let successArr = [];
    for (let i = 0; i < data.pieChart.length; i++) {
        categoryArr.push(data.pieChart[i].txndate);
        let amount = data.pieChart[i].totalSuccessAmount;
        amount = amount.toFixed(2);
        successArr.push(amount);
    }
    var element = document.getElementById("kt_charts_widget_3_chart");

    if (!element) {
        return;
    }

    var chart = {
        self: null,
        rendered: false
    };

    var initChart = function () {
        var height = parseInt(KTUtil.css(element, 'height'));
        var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
        var borderColor = KTUtil.getCssVariableValue('--kt-gray-200');
        var baseColor = KTUtil.getCssVariableValue('--kt-info');
        var lightColor = KTUtil.getCssVariableValue('--kt-info-light');

        var options = {
            series: [{
                name: 'Net Sales',
                data: successArr
            }],
            chart: {
                fontFamily: 'inherit',
                type: 'area',
                height: 350,
                toolbar: {
                    show: false
                }
            },
            plotOptions: {

            },
            legend: {
                show: false
            },
            dataLabels: {
                enabled: false
            },
            fill: {
                type: 'solid',
                opacity: 1
            },
            stroke: {
                curve: 'smooth',
                show: true,
                width: 3,
                colors: [baseColor]
            },
            xaxis: {
                categories: categoryArr,
                axisBorder: {
                    show: false,
                },
                axisTicks: {
                    show: false
                },
                labels: {
                    style: {
                        colors: labelColor,
                        fontSize: '12px'
                    }
                },
                crosshairs: {
                    position: 'front',
                    stroke: {
                        color: baseColor,
                        width: 1,
                        dashArray: 3
                    }
                },
                tooltip: {
                    enabled: false,
                    formatter: undefined,
                    offsetY: 0,
                    style: {
                        fontSize: '12px'
                    }
                }
            },
            yaxis: {
                labels: {
                    style: {
                        colors: labelColor,
                        fontSize: '12px'
                    }
                }
            },
            states: {
                normal: {
                    filter: {
                        type: 'none',
                        value: 0
                    }
                },
                hover: {
                    filter: {
                        type: 'none',
                        value: 0
                    }
                },
                active: {
                    allowMultipleDataPointsSelection: false,
                    filter: {
                        type: 'none',
                        value: 0
                    }
                }
            },
            tooltip: {
                style: {
                    fontSize: '12px'
                },
                y: {
                    formatter: function (val) {
                        return "&#8377;" + val
                    }
                }
            },
            colors: [lightColor],
            grid: {
                borderColor: borderColor,
                strokeDashArray: 4,
                yaxis: {
                    lines: {
                        show: true
                    }
                }
            },
            markers: {
                strokeColor: baseColor,
                strokeWidth: 3
            }
        };

        chart.self = new ApexCharts(element, options);
        chart.self.render();
        chart.rendered = true;
    }

    // Init chart
    initChart();

    // Update chart on theme mode change
    KTThemeMode.on("kt.thememode.change", function () {
        if (chart.rendered) {
            chart.self.destroy();
        }

        initChart();
    });
}

var pieChart;

function initPieChart(data, tabId, chartId, initByDefault, paymentMethod, mopType) {
    initChart(tabId, chartId, data, initByDefault, paymentMethod, mopType);
    // Private methods
    function initChart(tabSelector, chartSelector, data, initByDefault, paymentMethod, mopType) {
        var element = document.querySelector(chartSelector);
        if (!element) {
            return;
        }
        // element.innerHTML = "";
        let seriesData = [];
        let labelData = [];
        for (let i = 0; i < data.length; i++) {
            let details = data[i];
            seriesData.push(details.count);
            labelData.push(details.label);
        }
        var options = {
            series: seriesData,
            chart: {
                fontFamily: 'inherit',
                type: 'donut',
                width: 250,
            },
            plotOptions: {
                pie: {
                    donut: {
                        size: '50%',
                        labels: {
                            value: {
                                fontSize: '10px'
                            }
                        }
                    }
                }
            },
            colors: [
                KTUtil.getCssVariableValue('--kt-info'),
                KTUtil.getCssVariableValue('--kt-success'),
                KTUtil.getCssVariableValue('--kt-primary'),
                KTUtil.getCssVariableValue('--kt-danger')
            ],
            stroke: {
                width: 0
            },
            labels: labelData,
            legend: {
                show: true,
                position: 'bottom',
            },
            fill: {
                type: 'false',
            }
        };

        var tab = document.querySelector(tabSelector);

        if (pieChart) {
            pieChart.updateSeries(seriesData);
            pieChart.updateOptions({
                labels: labelData
            },true, true,true);
        }else{
            console.log("Initializing");
            pieChart = new ApexCharts(element, options);
            var init = false;
            var tab = document.querySelector(tabSelector);
            if (initByDefault === true) {
                pieChart.render();
                init = true;
            }
        }

        tab.addEventListener('shown.bs.tab', function (event) {
            if (init == false) {
                pieChart.render();
                init = true;
            }
        })
    }
}

var acquirerWisePieChart;
function initAcquirerWisePieChart(seriesData, labelData, tabId, chartId, initByDefault) {
    initChart(tabId, chartId, seriesData, labelData, initByDefault);
    // Private methods
    function initChart(tabSelector, chartSelector, seriesData, labelData, initByDefault) {
        var element = document.querySelector(chartSelector);
        if (!element) {
            return;
        }
        element.innerHTML = "";
        var options = {
            series: seriesData,
            chart: {
                fontFamily: 'inherit',
                type: 'donut',
                width: 250,
            },
            plotOptions: {
                pie: {
                    donut: {
                        size: '50%',
                        labels: {
                            value: {
                                fontSize: '10px'
                            }
                        }
                    }
                }
            },
            colors: [
                KTUtil.getCssVariableValue('--kt-info'),
                KTUtil.getCssVariableValue('--kt-success'),
                KTUtil.getCssVariableValue('--kt-primary'),
                KTUtil.getCssVariableValue('--kt-danger')
            ],
            stroke: {
                width: 0
            },
            labels: labelData,
            legend: {
                show: true,
                position: 'bottom',
            },
            fill: {
                type: 'false',
            }
        };

        var tab = document.querySelector(tabSelector);

        if (acquirerWisePieChart) {
            acquirerWisePieChart.updateSeries(seriesData);
            acquirerWisePieChart.updateOptions({
                labels: labelData
            }, true, true, true);
        } else {
            console.log("Initializing");
            acquirerWisePieChart = new ApexCharts(element, options);
            var init = false;
            var tab = document.querySelector(tabSelector);
            if (initByDefault === true) {
                acquirerWisePieChart.render();
                init = true;
            }
        }

        tab.addEventListener('shown.bs.tab', function (event) {
            if (init == false) {
                acquirerWisePieChart.render();
                init = true;
            }
        })
    }
}

function loadHourlyTransactionsChart(data) {

    var chart = {
        self: null,
        rendered: false
    };
    let dataArray = [];
    let successTxnArray = [];
    //    let refundTxnArray = [];
    let failedTxnArray = [];
    let cancelledTxnArray = [];
    let timeArray = [];
    for (let i = 0; i < data.length; i++) {
        timeArray.push(data[i].txnDate);
        successTxnArray.push(data[i].totalSuccess);
        //        refundTxnArray.push(data[i].totalRefund);
        failedTxnArray.push(data[i].totalFailed);
        cancelledTxnArray.push(data[i].totalCancelled);
    }
    let successObj = {
        name: "Total Success",
        data: successTxnArray
    }
    //    let refundObj = {
    //        name: "Total Refund",
    //        data: refundTxnArray
    //    }
    let failedObj = {
        name: "Total Failed",
        data: failedTxnArray
    }
    let cancelledObj = {
        name: "Total Cancelled",
        data: cancelledTxnArray
    }

    //    dataArray.push(refundObj);
    dataArray.push(cancelledObj);
    dataArray.push(successObj);
    dataArray.push(failedObj);
    initChart(chart, data);
    // Private methods
    function initChart(chart, data) {
        var element = document.getElementById("kt_charts_widget_40");

        if (!element) {
            return;
        }
        element.innerHTML = "";

        var height = parseInt(KTUtil.css(element, 'height'));
        var labelColor = KTUtil.getCssVariableValue('--kt-gray-500');
        var borderColor = KTUtil.getCssVariableValue('--kt-border-dashed-color');
        var options = {
            series: dataArray,
            chart: {
                fontFamily: 'inherit',
                type: 'area',
                height: height,
                toolbar: {
                    show: false
                }
            },
            plotOptions: {

            },
            legend: {
                show: false
            },
            dataLabels: {
                enabled: false
            },
            fill: {
                type: "gradient",
                gradient: {
                    shadeIntensity: 1,
                    opacityFrom: 0.4,
                    opacityTo: 0.2,
                    stops: [0, 5, 10]
                }
            },
            stroke: {
                curve: 'smooth',
                show: true,
                width: 3
            },
            xaxis: {
                categories: timeArray,
                axisBorder: {
                    show: false,
                },
                axisTicks: {
                    show: false
                },
                tickAmount: 6,
                labels: {
                    rotate: 0,
                    rotateAlways: true,
                    style: {
                        colors: labelColor,
                        fontSize: '12px'
                    }
                },
                crosshairs: {
                    position: 'front',
                    stroke: {
                        //color: [baseprimaryColor, basesuccessColor, labelColor],
                        width: 1,
                        dashArray: 3
                    }
                },
                tooltip: {
                    enabled: true,
                    formatter: undefined,
                    offsetY: 0,
                    style: {
                        fontSize: '12px'
                    }
                }
            },
            yaxis: {
                tickAmount: 6,
                labels: {
                    style: {
                        colors: labelColor,
                        fontSize: '12px'
                    }
                }
            },
            states: {
                normal: {
                    filter: {
                        type: 'none',
                        value: 0
                    }
                },
                hover: {
                    filter: {
                        type: 'none',
                        value: 0
                    }
                },
                active: {
                    allowMultipleDataPointsSelection: false,
                    filter: {
                        type: 'none',
                        value: 0
                    }
                }
            },
            tooltip: {
                style: {
                    fontSize: '12px'
                }
            },
            // colors: [lightprimaryColor, lightsuccessColor, labelColor],
            grid: {
                borderColor: borderColor,
                strokeDashArray: 4,
                yaxis: {
                    lines: {
                        show: true
                    }
                }
            },
            markers: {
                //strokeColor: [baseprimaryColor, basesuccessColor, labelColor],
                strokeWidth: 3
            }
        };

        chart.self = new ApexCharts(element, options);

        // Set timeout to properly get the parent elements width
        setTimeout(function () {
            chart.self.render();
            chart.rendered = true;
        }, 200);
    }
}

let settlementRootArray = [];

function loadSettlementChart(data) {
    // Check if amchart library is included
    if (typeof am5 === "undefined") {
        return;
    }

    var element = document.getElementById("kt_charts_widget_41_chart");
    if (!element) {
        return;
    }
    var root;
    if (settlementRootArray.length == 0) {
        root = am5.Root.new(element);
        // Set themes
        // https://www.amcharts.com/docs/v5/concepts/themes/
        root.setThemes([am5themes_Animated.new(root)]);
        settlementRootArray.push(root);
    } else {
        root = settlementRootArray[0];
        root.container.children.pop();
    }

    // Create chart
    // https://www.amcharts.com/docs/v5/charts/percent-charts/sliced-chart/
    var chart = root.container.children.push(am5xy.XYChart.new(root, {
        panX: false,
        panY: false,
        wheelX: "panX",
        wheelY: "zoomX",
        layout: root.verticalLayout
    }));


    // Add legend
    // https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
    var legend = chart.children.push(am5.Legend.new(root, {
        centerX: am5.p50,
        x: am5.p50
    }));

    // Create axes
    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
    var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
        categoryField: "txnDate",
        renderer: am5xy.AxisRendererX.new(root, {
            cellStartLocation: 0.1,
            cellEndLocation: 0.9
        }),
        tooltip: am5.Tooltip.new(root, {})
    }));

    xAxis.data.setAll(data);

    var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
        min: 0,
        renderer: am5xy.AxisRendererY.new(root, {})
    }));


    // Add series
    // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
    function makeSeries(name, fieldName, stacked) {
        var series = chart.series.push(am5xy.ColumnSeries.new(root, {
            stacked: stacked,
            name: name,
            xAxis: xAxis,
            yAxis: yAxis,
            valueYField: fieldName,
            categoryXField: "txnDate"
        }));

        series.columns.template.setAll({
            tooltipText: "{name}, {categoryX}:{valueY}",
            width: am5.percent(90),
            tooltipY: am5.percent(10)
        });
        series.data.setAll(data);

        // Make stuff animate on load
        // https://www.amcharts.com/docs/v5/concepts/animations/
        series.appear();

        series.bullets.push(function () {
            return am5.Bullet.new(root, {
                locationY: 0.5,
                sprite: am5.Label.new(root, {
                    text: "{valueY}",
                    fill: root.interfaceColors.get("alternativeText"),
                    centerY: am5.percent(50),
                    centerX: am5.percent(50),
                    populateText: true
                })
            });
        });

        legend.data.push(series);
    }

    makeSeries("Captured", "totalCapturedAmount", false);
    makeSeries("Settled", "totalSettledAmount", false);


    // Make stuff animate on load
    // https://www.amcharts.com/docs/v5/concepts/animations/
    chart.appear(1000, 100);
}

let hitsRootArray = [];

function loadHitsCapturedChart(data) {
    // Private methods
    // Check if amchart library is included
    if (typeof am5 === "undefined") {
        return;
    }

    var element = document.getElementById("kt_charts_widget_42");

    if (!element) {
        return;
    }
    var root;

    // Create root element
    // https://www.amcharts.com/docs/v5/getting-started/#Root_element
    if (hitsRootArray.length == 0) {
        root = am5.Root.new(element);
        // Set themes
        // https://www.amcharts.com/docs/v5/concepts/themes/
        root.setThemes([am5themes_Animated.new(root)]);
        hitsRootArray.push(root);
    } else {
        root = hitsRootArray[0];
        root.container.children.pop();
    }

    var chart = root.container.children.push(
        am5xy.XYChart.new(root, {
            panY: false,
            layout: root.verticalLayout,
            maxTooltipDistance: 0
        })
    );

    // Define data
    var data = [{
        category: "Total Hits",
        value: parseInt(data.totalTxnCount)
    }, {
        category: "Success",
        value: parseInt(data.successTxnCount)
    }, {
        category: "Failed",
        value: parseInt(data.failedTxnCount)
    }, {
        category: "Cancelled",
        value: parseInt(data.cancelledTxnCount)
    }, {
        category: "Invalid",
        value: parseInt(data.invalidTxnCount)
    }];

    // Create Y-axis
    let yAxis = chart.yAxes.push(
        am5xy.ValueAxis.new(root, {
            strictMinMaxSelection: true,
            baseValue: 0,
            min: 0,
            extraMax: 0.1,
            renderer: am5xy.AxisRendererY.new(root, {})
        })
    );

    // Create X-Axis
    var xAxis = chart.xAxes.push(
        am5xy.CategoryAxis.new(root, {
            maxDeviation: 0.2,
            renderer: am5xy.AxisRendererX.new(root, {
                minGridDistance: 10
            }),

            categoryField: "category"
        })
    );
    xAxis.data.setAll(data);

    // Create series
    function createSeries(name, field) {
        var series = chart.series.push(
            am5xy.ColumnSeries.new(root, {
                name: name,
                xAxis: xAxis,
                yAxis: yAxis,
                valueYField: "value",
                categoryXField: "category",
                tooltip: am5.Tooltip.new(root, {})
            })
        );

        series.bullets.push(function () {
            return am5.Bullet.new(root, {
                sprite: am5.Label.new(root, {
                    text: "{valueY}",
                    centerX: am5.percent(50),
                    centerY: am5.percent(90),
                    populateText: true
                })
            });
        });

        series.get("tooltip").label.set("text", "[bold]{category}: {valueY}")
        series.data.setAll(data);
    }

    createSeries("Series #1", "value");
}