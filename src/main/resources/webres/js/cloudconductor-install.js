$('#repo-scan').change(function() {
    opt = $(this).val();
    if (opt=="true") {
        $('#repo-scan-detail').show();
    }else if (opt == "false") {
        $('#repo-scan-detail').hide();
    }
});

$('#repo-provider').change(function() {
    opt = $(this).val();
    if (opt=="FileProvider") {
        $('#repo-basedir').show();
        $('#repo-baseurl').hide();
        $('#repo-aws').hide();
    }else if (opt == "HTTPProvider") {
    	$('#repo-basedir').hide();
        $('#repo-baseurl').show();
        $('#repo-aws').hide();
    }else if (opt == "AWSS3Provider") {
    	$('#repo-basedir').hide();
        $('#repo-baseurl').hide();
        $('#repo-aws').show();
    }else {
    	$('#repo-basedir').hide();
        $('#repo-baseurl').hide();
        $('#repo-aws').hide();
    }
});

$(document).ready(function(){
	var maxWidth = $('.progress').width();
	var found = 0;
	var maxTime = 1250000;
	var intervalTime = 5000;
	var tickCount = maxTime/intervalTime;
	var progress = setInterval(function() {
    var bar = $('.progress-bar');
    if (bar.width()>=maxWidth) {
        clearInterval(progress);
    } else {
        bar.width(bar.width()+(maxWidth/tickCount));
    }
    
    $.ajax({
		type : "GET",
		url : "/web",
		success : function(data) {
			found = found +1;
			if(found > 5) {
				window.location.href = "/web";
			}
			return 0;
		},
		error : function(xhr, ajaxOption, thorwnError) {
			return 1;
		},
		processData : false,
		async : true
	});
}, intervalTime);

});
