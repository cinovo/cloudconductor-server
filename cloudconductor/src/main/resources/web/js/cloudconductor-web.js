
function initTooltips() {
	$("[rel=tooltip]").tooltip({ placement: 'right'});
	$("[rel=tooltip-left]").tooltip({ placement: 'left'});
	$("[rel=tooltip-down]").tooltip({ placement: 'bottom'});
}

function enableCheckboxToggle() {
	 $("input[name=toggle]").click(function() {
		 console.log("clicked");
	     $(this).closest("form").find("input:checkbox[name="+$(this).prop("value")+"]").prop("checked", $(this).prop("checked"));
	 });
}


$.fn.openmodal = function() {
	$(this).click(function() {
	var modal = $(this);
	var path = $(location).prop("pathname") +"/";
	var target = $(this).data('ref');
	var modal = $(this).data('target');
	var type = $(this).data('call-type');
	var form = $(this).data('form');
	var targetUrl=path+target;
	if(target.substring(0, 4) == "/web") {
		targetUrl=target;
	}
	 $.ajax({
            type: type,
            url: targetUrl,
            data: $('form#'+form).serialize(),
            success: function (data) {
            	if(data.type && data.type == "REDIRECT") {
            		$(modal).modal('hide');
            		window.location.assign(data.path);
            		window.location.reload(true);
				} else {
	            	$(modal).find('.modal-content').empty().html(data);
	            	$(modal).find("button[data-target='"+modal+"']").openmodal();
	            	initTooltips();
	            	enableCheckboxToggle();
            	}
            },
            error: function (xhr, ajaxOption, thorwnError) {
                console.log(xhr);
                console.log(ajaxOption);
            },
            processData: false,
            async: true
	});
});
}

$(document).ready(function () {
	//init tooltips
    initTooltips();
	//init checkbox toggles
    enableCheckboxToggle();
	//do smth on click of button
	$("button[data-toggle='modal']").openmodal();
	$("a[data-toggle='modal']").openmodal();
	
	$('#sidenav').on('activate.bs.scrollspy', function () {
		var data = $('body').data('bs.scrollspy');
	 	if (data) {
	 		var target = data.activeTarget.charAt(1).toUpperCase();
	 		$('span[id^="collapse"]').each(function() {
		 		if($(this).attr('id') == 'collapse'+target) {
		     		$(this).collapse('show'); 		
		 		} else {
			 		$(this).collapse('hide');
			 	}
     		});
  	 	}
	})
});


