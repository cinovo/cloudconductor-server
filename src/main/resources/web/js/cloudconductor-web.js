function reloadContent() {
	// check if autorefresh is active
	if (!($("#autorefresh_button").hasClass("active"))) {
		return;
	}
	// check if modal is open
	if ($("#popModal").hasClass('in')) {
		return;
	}
	var path = $(location).prop("pathname") + "/";
	window.location.assign(path);
	window.location.reload(true);
}

function initTooltips() {
	$("[rel=tooltip]").tooltip({
		placement : 'right'
	});
	$("[rel=tooltip-left]").tooltip({
		placement : 'left'
	});
	$("[rel=tooltip-down]").tooltip({
		placement : 'bottom'
	});
}

function enableCheckboxToggle() {
	$("input[behavior=toggle]").click(
			function() {
				$(this).closest("form").find(
						"input:checkbox[name=" + $(this).prop("value") + "]")
						.filter(function() {
							return !this.disabled;
						}).prop("checked", $(this).prop("checked"));
			});

	$("input[behavior=toggle-exclusive-all]").click(
			function() {
				var grp = $(this).prop("value").split(",");
				var element = $(this);
				grp.forEach(function(elem, index) {
					if (index < 1) {
						element.closest("form").find(
								"input:checkbox[name=" + elem + "]").filter(
								function() {
									return !this.disabled;
								}).prop("checked", element.prop("checked"));
					} else if (element.prop("checked")) {
						element.closest("form").find(
								"input:checkbox[name=" + elem + "]").filter(
								function() {
									return !this.disabled;
								}).prop("checked", !element.prop("checked"));
					}
				});

			});

	$("input[behavior=toggle-exclusive]").click(
			function() {
				var isSelected = $(this).prop("checked");
				var element = $(this);
				element.prop("checked", isSelected);
				if (isSelected) {
					element.closest("form").find(
							"input:checkbox[value=" + $(this).prop("value")
									+ "]").filter(
							function() {
								return (this.name != element.prop("name"))
										&& !this.disabled;
							}).prop("checked", !isSelected);
				}
			});
}

function initButtons() {
	$("button[data-toggle='modal']").openmodal();
	$("a[data-toggle='modal']").openmodal();
	$("button[data-toggle='ajax']").ajaxCall();
	$("button[data-toggle='update-modal']").openmodal();
}

function ajaxCall(object, target) {
	var pathname = $(location).prop("pathname");
	var path = pathname + "/";
	var ref = object.data('ref');
	var search = $(location).prop("search");
	var hash = $(location).prop("hash")
	var type = object.data('call-type');
	var form = object.data('form');
	var targetUrl = path + ref;
	if (ref.substring(0, 4) == "/web") {
		targetUrl = ref;
	}
	$
			.ajax({
				type : type,
				url : targetUrl,
				data : $('form#' + form).serialize(),
				success : function(data) {
					var reloaded = object.data('reloaded');
					if (data.type && data.type == "REFRESH") {
						var reload = data.path;
						if (reload == pathname) {
							reload += search;
							reload += hash;
						}
						window.location.assign(data.path);
						window.location.reload(true);
					} else if (data.type && data.type == "GET") {
						object.data('call-type', data.type);
						object.data('ref', data.path);
						object.data('reloaded', data);
						ajaxCall(object, target);
					} else if (target) {
						$(target).empty();
						$(target).html(data);
						initButtons();
						initTooltips();
						enableCheckboxToggle();
					}
					if ($("#popModal").hasClass('in')) {
						if (reloaded.info || data.info)
							$("#popModal")
									.find('.modal-body')
									.prepend(
											'<div class="alert alert-success"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>'
													+ reloaded.info + '</div>');
					}
					return 0;
				},
				error : function(xhr, ajaxOption, thorwnError) {
					console.log(xhr);
					console.log(ajaxOption);
					return 1;
				},
				processData : false,
				async : true
			});
	return 0;
}

$.fn.ajaxCall = function() {
	$(this).click(function() {
		ajaxCall($(this), $(this).data('target'));
	});
}

$.fn.openmodal = function() {
	$(this).click(
			function() {
				if ($(this).attr('modal-type') == 'big') {
					$($(this).data('target')).find('.modal-dialog').addClass(
							"modal-lg");
				}
				ajaxCall($(this), $($(this).data('target')).find(
						'.modal-content'));
			});
}

$(document)
		.ready(
				function() {
					// init tooltips
					initTooltips();
					// init checkbox toggles
					enableCheckboxToggle();
					// do smth on click of button
					initButtons();

					$('#sidenav')
							.on(
									'activate.bs.scrollspy',
									function() {
										var data = $('body').data(
												'bs.scrollspy');
										if (data) {
											var target = data.activeTarget
													.charAt(1).toUpperCase();
											$('span[id^="collapse"]')
													.each(
															function() {
																if ($(this)
																		.attr(
																				'id') == 'collapse'
																		+ target) {
																	$(this)
																			.collapse(
																					'show');
																} else {
																	$(this)
																			.collapse(
																					'hide');
																}
															});
										}
									})

					// init reloadContent
					window.setInterval(reloadContent, 15000);

					// init audit-table
					var dataTable = $('#audit-table').DataTable({
						"pagingType" : "simple_numbers",
						"pageLength" : 25,
						"order" : [ [ 0, "desc" ] ],
						"dom" : '<tip>',

					});
					// init custom searchbox
					$('#searchbox').on('keyup', function() {
						dataTable.search(this.value).draw();
					});

					// init custom Category-filter
					$('#inputCategory').on('change', function() {
						dataTable.columns(3).search(this.value).draw();
					});
					// init custom Audittype-filter
					$('#inputAudittype').on('change', function() {
						dataTable.columns(4).search(this.value).draw();
					});

					// init custom username-filter
					$('#inputUsername').on('change', function() {
						dataTable.columns(2).search(this.value).draw();

					});

					// init date-range-picker for audit-table
					$('#daterange').daterangepicker(

							{
								ranges : {
									'Today' : [ moment(), moment() ],
									'Yesterday' : [
											moment().subtract('days', 1),
											moment().subtract('days', 1) ],
									'Last 7 Days' : [
											moment().subtract('days', 6),
											moment() ],
									'Last 30 Days' : [
											moment().subtract('days', 29),
											moment() ],
									'This Month' : [ moment().startOf('month'),
											moment().endOf('month') ],
									'Last Month' : [
											moment().subtract('month', 1)
													.startOf('month'),
											moment().subtract('month', 1)
													.endOf('month') ]
								},
								startDate : moment().subtract('days', 29),
								endDate : moment()
							});

					$('#daterange').on('cancel.daterangepicker',
							function(ev, picker) {
								$('#daterange').val(''); // clearing an input
								dataTable.draw(); // draw the table
							});
					$('#daterange').on('apply.daterangepicker',
							function(ev, picker) {
								dataTable.draw(); // draw the table
							});

					// custom datarange table filter
					$.fn.dataTableExt.afnFiltering
							.push(function(oSettings, aData, iDataIndex) {

								var daterange = document
										.getElementById('daterange').value;
								var iStartDateCol = 0;

								var minDate = daterange.substring(6, 10)
										+ daterange.substring(0, 2)
										+ daterange.substring(3, 5);
								var maxDate = daterange.substring(19, 23)
										+ daterange.substring(13, 15)
										+ daterange.substring(16, 18);

								var objectDate = aData[iStartDateCol]
										.substring(6, 10)
										+ aData[iStartDateCol].substring(3, 5)
										+ aData[iStartDateCol].substring(0, 2);

								if (minDate == "" && maxDate == "") {
									return true;
								}

								if (objectDate == minDate
										|| objectDate == maxDate) {
									return true;
								} else if (objectDate < maxDate
										&& objectDate > minDate) {
									return true;
								}
								return false;

							});
				});
