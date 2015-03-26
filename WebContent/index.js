/**
 * 
 */

/* GLOBALS */
var  dialog = $('#createVisitDialog').dialog({
	autoOpen: false,
	height: 600,
	width: 800,
	modal: true,
	hide: 'clip',
	show: 'clip',
	buttons: {
		'Create Visit': createVisitRecord,
		'Cancel': function() {
			dialog.dialog('close');
		}
	},
	close: function() {
		form[0].reset();
	}
});

var deleteConfirmationDialog = $('#deleteConfirmationDialog').dialog({
	autoOpen: false,
	resizable: false,
	height: 240,
	width: 400,
	hide: 'clip',
	show: 'clip',
	modal: true,
	buttons: [{
		text: "Delete all selected",
		click: function() {
			deleteSelectedVisitRecords();
			$(this).dialog( "close" );
		}},
		{
		text: "Cancel",	
		click: function() {
			$(this).dialog( "close" );
		}
	}]
});


var  form = dialog.find('form').on('submit', function(evt) {
	evt.preventDefault();
	createVisitRecord();
});

/*
 * Global functions
 */

// Given an Array of Date objects, this function returns the lowest date
function findMinDate(dates){
	var minDate = null;
	
	console.log('*********** Inside findMinDates function ***********');
	console.log('Received dates array: ', dates);
	
	if ( dates instanceof Array ) {
		if ( dates[0] != null 
				&& dates[0] != undefined ) {
			
			minDate = new Date(dates[0]);
			
		} else {
			return null;
		}
			
		for ( var i = 1; i < dates.length; i++ ) {
			if ( dates[i] != null 
					&& dates[i] != undefined ) {
				
				var curDate = new Date(dates[i]);
				if ( minDate > curDate ) minDate = curDate;
				console.log('sorting min date = ' + minDate);
			}
		}
	}
	
	if ( minDate != null && minDate != undefined && minDate instanceof Date ) {
		minDate = minDate.toDateString();
	}
	
	return minDate;
}

function openVisitDialog() {
	dialog.dialog('open');
}

function openDeleteConfirmationDialog() {
	deleteConfirmationDialog.dialog('open');
}

// This function is used to retrieve all visit records from the backend upon startup.
function fetchAllVisitRecords() {
	$('#messagesDiv').empty().append($('<p>Retrieving Visit Records ... ' + 
		'<img src="./images/spinner.gif"></p>')).show('scale');
	
	$.ajax({
		url: './api/visit',
		type: 'GET',
		traditional: true,
		success: function(data, status, xhr) {
			console.log('Success', data, status, xhr);
			$('#messagesDiv').empty();
			$('#messagesDiv').empty().append(
					$('<p><img src="./images/complete_status.gif">'
					+ 'Retrieved visit records successfully.</p>')).show('scale')
				.delay(2000)
				.hide('scale');
			
			$.each(data.results, function(i, item){
				console.log('ITEM ID# ', item._id.$oid);
				console.log('VISIT CHOICE: ', item.visitTypeChoice);
				console.log('INDUSTRY: ', item.industry);
				console.log('ACCOUNT NAME: ', item.accName);
				console.log('PAL/LFE:', item.palLFE);
				console.log('CBC:', item.cbc);
				console.log('HOSTING MANAGER: ', item.hostMgr);
				console.log('PRIMARY AGENDA:', item.visitAgenda);
//				console.log(i, item);
				
				// find the minimum of the Itinerary dates
				var itiDates = new Array();
				$.each(item.itineraryRecords, function(idx, itiRec){
					itiDates.push(itiRec.itiStart);
				});
				var visitStartDate = findMinDate(itiDates);
				
				var insertRow = $('<tr></tr>').append(createCheckBox())
								.append($('<td>' + item._id.$oid + '</td>'))
								.append($('<td>' + visitStartDate + '</td>'))
								.append($('<td>' + item.industry + '</td>'))
								.append($('<td>' + item.accName + '</td>'))
								.append($('<td>' + item.palLFE + '</td>'))
								.append($('<td>' + item.cbc + '</td>'))
								.append($('<td>' + item.hostMgr + '</td>'))
								.append($('<td>' + item.visitAgenda + '</td>'));
				
				$('#visitRecordsTable').append(insertRow);
			});
			
			
			
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$('#messagesDiv').empty();
			$('#messagesDiv').empty().append(
					$('<p><img src="./images/complete_error.gif">'
					+ 'Operation Failed: ' 
					+ errorThrown + '</p>')).show('scale')
				.delay(2000)
				.hide('scale');
		}
	});
}

// This function is used to rub a validation error on the user's face :P
function showValidationError(selectTabId, msgBoxId, errMsg) {
	console.log('inside validation logic now with params: ', selectTabId, msgBoxId, errMsg);
	
//	var selectTab = $('#' + selectTabId);
	var msgBox = $('#' + msgBoxId);
	
	console.log('Message Box object: ', msgBox);
//	console.log('Select tab Object: ', selectTab);
	
	// activate the tab
//	var selectTabIdx = $('#createVisitTabs').parent().index(selectTab.parent());
	
	var selectTabIdx = 0;
	
	if ( selectTabId == 'visitItineraryTab' ) {
		selectTabIdx = 2;
	} else if ( selectTabId == 'leadershipParticipationTab' ) {
		selectTabIdx = 3;
	} else if ( selectTabId == 'visitorsListTab' ) {
		selectTabIdx = 1;
	}
	
	console.log('Activating tab with index # ' + selectTabIdx + ' ...'); 
	$('#createVisitTabs').tabs({active: selectTabIdx});
	
	// Do the harlem shake :P
	console.log('Nudging...');
	dialog.effect('shake', {times: 3}, 80);
	
	// Display the error
	console.log('Displaying error...');
	msgBox.empty()
		.append($('<p><img src="./images/complete_error.gif">'
					+ 'Operation Failed: ' 
					+ errMsg + '</p>'))
		.show('scale')
		.delay(2000)
		.hide('scale');
}

// This function is used to actually create a visit record in the system.
function createVisitRecord() {
	console.log('Inside create visit record');
	var visitTypeChoice = $('input[name="visitTypeChoice"]:checked').val();
	var industry = $('#industry').val();
	var accName = $('#accName').val();
	var palLFE = $('#palLFE').val();
	var cbc = $('#cbc').val();
	var hostMgr = $('#hostMgr').val();
	var visitAgenda = $('#visitAgenda').val();
	
	// nested table fields
	// visitor fields
	visitorName = [];
	visitorRole = [];
	visitorPrimary = [];
	
	// itinerary fields
	itiLoc = [];
	itiStart = [];
	itiEnd = [];
	
	// leadership fields
	ldrLNID = [];
	ldrBU = [];
	ldrAttnd = [];
	ldrLoc = [];
	ldrDate = [];
	
	// collected nested table data in array objects
	// visitor data
	$('#visitorsList > tbody > tr').each(function(index) {
		var rowIdx = index;
		$(this).find('td').each(function(index){
			if ( index == 1 ) {
				visitorName[rowIdx] = $(this).text();
			} else if ( index == 2 ) {
				visitorRole[rowIdx] = $(this).text();
			} else if ( index == 3 ) {
				visitorPrimary[rowIdx] = $(this).text();
			}
		});
	});
	
	// itinerary data
	$('#visitItineraryList > tbody > tr').each(function(index) {
		var rowIdx = index;
		$(this).find('td').each(function(index){
			if ( index == 1 ) {
				itiLoc[rowIdx] = $(this).text();
			} else if ( index == 2 ) {
				itiStart[rowIdx] = $(this).text();
			} else if ( index == 3 ) {
				itiEnd[rowIdx] = $(this).text();
			}
		});
	});
	
	// leadership data
	$('#leadershipParticipationList > tbody > tr').each(function(index) {
		var rowIdx = index;
		$(this).find('td').each(function(index){
			if ( index == 1 ) {
				ldrLNID[rowIdx] = $(this).text();
			} else if ( index == 2 ) {
				ldrBU[rowIdx] = $(this).text();
			} else if ( index == 3 ) {
				ldrAttnd[rowIdx] = $(this).text();
			} else if ( index == 4 ) {
				ldrLoc[rowIdx] = $(this).text();
			} else if ( index == 5 ) {
				ldrDate[rowIdx] = $(this).text();
			}
		});
	});
	
	// Okay, now that we have all the data, do some more validation here please
	console.log('Validating gathered data');
	// Validate visitOverviewTab data first
	if ( accName == null || accName == undefined || accName.trim().length <= 0 ) {
		showValidationError('visitOverviewTab', 'visitOverviewMsgBox', 'You did not enter an Account Name');
		return;
	}
	
	if ( palLFE == null || palLFE == undefined || palLFE.trim().length <= 0 ) {
		showValidationError('visitOverviewTab', 'visitOverviewMsgBox', 'You did not enter a PAL/LFE name');
		return;
	}
	
	if ( hostMgr == null || hostMgr == undefined || hostMgr.trim().length <= 0 ) {
		showValidationError('visitOverviewTab', 'visitOverviewMsgBox', 'You did not enter a Hosting Manager name');
		return;
	}
	
	if ( visitAgenda == null || visitAgenda == undefined || visitAgenda.trim().length <= 0 ) {
		showValidationError('visitOverviewTab', 'visitOverviewMsgBox', 'You did not enter a Visit Agenda');
		return;
	}
	
	// Now the visitorsListTab
	if ( visitorName.length == undefined || visitorName.length <= 0 ) {
		showValidationError('visitorsListTab', 'visitorsListTabMsgBox', 'You did not enter any Visitors');
		return;
	}
	
	// Now the visitItineraryTab, visitItineraryTabMsgBox
	if ( itiLoc.length == undefined || itiLoc.length <= 0 ) {
		showValidationError('visitItineraryTab', 'visitItineraryTabMsgBox', 'You did not enter any Visit Itineraries');
		return;
	}
	
	// Now the leadershipParticipationTab, ldrPartMsgBox
	if ( ldrLNID.length == undefined || ldrLNID.length <= 0 ) {
		showValidationError('leadershipParticipationTab', 'ldrPartMsgBox', 'You did not enter any Leadership Visit Records');
		return;
	}
	
	// find the minimum of the Itinerary dates
	var visitStartDate = findMinDate(itiStart);
	
	dialog.dialog('close');
	
	$('#messagesDiv').empty().append($('<p>Creating Visit Record ... ' + 
			'<img src="./images/spinner.gif"></p>'));
	
	postData = {
			'visitTypeChoice': visitTypeChoice,
			'industry': industry,
			'accName': accName,
			'palLFE': palLFE,
			'cbc': cbc,
			'hostMgr': hostMgr,
			'visitAgenda': visitAgenda,
			'visitorName': visitorName,
			'visitorRole': visitorRole,
			'visitorPrimary': visitorPrimary,
			'itiLoc': itiLoc,
			'itiStart': itiStart,
			'itiEnd': itiEnd,
			'ldrLNID': ldrLNID,
			'ldrBU': ldrBU,
			'ldrAttnd': ldrAttnd,
			'ldrLoc': ldrLoc,
			'ldrDate': ldrDate
		};
	
	console.log('data values to be sent: ', postData);
	
	$.ajax({
		url: './api/visit',
		type: 'POST',
		traditional: true,
		data: postData,
		dataType: 'json',
		success: function(data, status, xhr) {
			console.log('Success', data, status, xhr);
			$('#messagesDiv').empty();
			$('#messagesDiv').empty().append(
					$('<p><img src="./images/complete_status.gif">'
					+ 'Visit record #'
					+ data._id.$oid
					+ ' created successfully.</p>')).show('scale')
				.delay(2000)
				.hide('scale');
			
			var insertRow = $('<tr></tr>').append(createCheckBox())
							.append($('<td>' + data._id.$oid + '</td>'))
							.append($('<td>' + visitStartDate + '</td>'))
							.append($('<td>' + industry + '</td>'))
							.append($('<td>' + accName + '</td>'))
							.append($('<td>' + palLFE + '</td>'))
							.append($('<td>' + cbc + '</td>'))
							.append($('<td>' + hostMgr + '</td>'))
							.append($('<td>' + visitAgenda + '</td>'));
			
			$('#visitRecordsTable').append(insertRow);
			
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$('#messagesDiv').empty();
			$('#messagesDiv').empty().append(
					$('<p><img src="./images/complete_error.gif">'
					+ 'Operation Failed: ' 
					+ errorThrown + '</p>')).show('scale')
				.delay(2000)
				.hide('scale');
		}
	});
}

// This function is used to delete selected visit records from the system
function deleteSelectedVisitRecords() {
	$('#visitRecordsTable > tbody > tr').each(function(index) {
		var chkBox = $(this).find('td > input[type="checkbox"]');
		if ( chkBox.prop('checked') == true ) {
			$(this).find('td').each(function(index){
				if (index == 1) {
					var id = $(this).text();
					// This is where we need to do some server side magic
					$('#messagesDiv').empty().append($('<p>Deleting Visit Record # ' + id + '' + 
					'<img src="./images/spinner.gif"></p>'));
					
					$.ajax({
						url: './api/visit',
						type: 'DELETE',
						traditional: true,
						data: {'id': id},
						contentType: 'text/plain',
						dataType: 'json',
						success: function(data, status, xhr) {
							console.log('Success', data, status, xhr);
							$('#messagesDiv').empty();
							$('#messagesDiv').empty().append(
									$('<p><img src="./images/complete_status.gif">'
									+ 'Deleted visit record #' + id + 'successfully.</p>')).show('scale')
								.delay(2000)
								.hide('scale');
						},
						
						error: function(jqXHR, textStatus, errorThrown) {
							$('#messagesDiv').empty();
							$('#messagesDiv').empty().append(
									$('<p><img src="./images/complete_error.gif">'
									+ 'Operation Failed: ' 
									+ errorThrown + '</p>')).show('scale')
								.delay(2000)
								.hide('scale');
						}
					});
				}
			});
			$(this).remove();
		}
	});
}

//This function is used to open selected visit records from the system in popup dialogs
function openSelectedVisitRecords() {
	$('#visitRecordsTable > tbody > tr').each(function(index) {
		var chkBox = $(this).find('td > input[type="checkbox"]');
		if ( chkBox.prop('checked') == true ) {
			$(this).find('td').each(function(index){
				if (index == 1) {
					var id = $(this).text();
					// This is where we need to do some server side magic
					$('#messagesDiv').empty().append($('<p>Opening Visit Record # ' + id + '' + 
					'<img src="./images/spinner.gif"></p>')).show('scale');
					
					$.ajax({
						url: './api/visit',
						type: 'GET',
						traditional: true,
						data: {'id': id},
						dataType: 'json',
						success: function(data, status, xhr) {
							console.log('Success', data, status, xhr);
							
							// Open the visit record
							var visitRec = data.results[0];	
							var visRecDiv = $('<div id="visRecWin'+ visitRec._id.$oid +'" title="Visit Record # ' + visitRec._id.$oid  + '"></div>');
							var visRecTabDiv = $('<div id="visRecTab' + visitRec._id.$oid + '"></div>');
							var visRecTabUl = $('<ul></ul>');
							var visRecOvrvwDiv = $('<div id="visRecOvrvw' + visitRec._id.$oid + '"></div>');
							var visRecVisDiv = $('<div id="visRecVis' + visitRec._id.$oid + '"></div>');
							var visRecItiDiv = $('<div id="visRecIti' + visitRec._id.$oid + '"></div>');
							var visRecLdrDiv = $('<div id="visRecLdr' + visitRec._id.$oid + '"></div>');
							
							var visRecOvervwTable = $('<table></table>').append(
									$('<tbody></tbody>').append(
											$('<tr></tr>')
										.append(
												$('<td>Visit ID #</td>')	
											).append(
												$('<td>' + visitRec._id.$oid + '</td>')
											)
										)
										.append(
											$('<tr></tr>').append(
												$('<td>Visit Type: </td>')	
											).append(
												$('<td>' + visitRec.visitTypeChoice + '</td>')
											)
										)
										.append(
											$('<tr></tr>').append(
												$('<td>Industry: </td>')	
											).append(
												$('<td>' + visitRec.industry + '</td>')
											)
										)
										.append(
											$('<tr></tr>').append(
												$('<td>Account Name: </td>')	
											).append(
												$('<td>' + visitRec.accName + '</td>')
											)
										)
										.append(
											$('<tr></tr>').append(
												$('<td>PAL/LFE: </td>')	
											).append(
												$('<td>' + visitRec.palLFE + '</td>')
											)
										)
										.append(
											$('<tr></tr>').append(
												$('<td>CBC: </td>')	
											).append(
												$('<td>' + visitRec.cbc + '</td>')
											)
										)
										.append(
											$('<tr></tr>').append(
												$('<td>Hosting Manager: </td>')	
											).append(
												$('<td>' + visitRec.hostMgr + '</td>')
											)
										)
										.append(
											$('<tr></tr>').append(
												$('<td>Primary Agenda of Visit: </td>')	
											).append(
												$('<td>' + visitRec.visitAgenda + '</td>')
											)
										)
									);
							
							var visRecVisTable = $('<table></table>').append(
								$('<thead></thead>').append(
									$('<tr></tr>')
									.append(
										$('<th>Visitor Name</th>')
									)
									.append(
										$('<th>Visitor Role</th>')
									)
									.append(
										$('<th>Is Primary?</th>')
									)
								)
							).append(
								$('<tbody></tbody>')	
							);
							
							var visRecItiTable = $('<table></table>')
							.append(
								$('<thead></thead>')
								.append(
									$('<tr></tr>')
									.append(
										$('<th>Location</th>')
									)
									.append(
										$('<th>Start Date</th>')
									)
									.append(
										$('<th>End Date</th>')
									)
								)
							).append(
								$('<tbody></tbody>')
							);
							
							var visRecLdrTable = $('<table></table>')
							.append(
									$('<thead></thead>')
									.append(
										$('<tr></tr>')
										.append(
											$('<th>Leader Lotus Notes ID</th>')
										)
										.append(
											$('<th>BU</th>')
										)
										.append(
											$('<th>Attend/Inform</th>')
										)
										.append(
											$('<th>Location</th>')
										)
										.append(
											$('<th>Date</th>')
										)
									)
								).append(
									$('<tbody></tbody>')
								);
							
							$.each(visitRec.visitorRecords, function(index, visitorRec){
								visRecVisTable.find('tbody')
								.append(
									$('<tr></tr>')
									.append(
											$('<td>' + visitorRec.visitorName + '</td>')
									)
									.append(
											$('<td>' + visitorRec.visitorRole + '</td>')
									)
									.append(
											$('<td>' + visitorRec.visitorPrimary + '</td>')
									)
								);
							});
							
							$.each(visitRec.itineraryRecords, function(index, itiRec) {
								visRecItiTable.find('tbody')
								.append(
									$('<tr></tr>')
									.append(
										$('<td>' + itiRec.itiLoc + '</td>')
									)
									.append(
										$('<td>' + itiRec.itiStart + '</td>')
									)
									.append(
										$('<td>' + itiRec.itiEnd + '</td>')
									)
								);
							});
							
							$.each(visitRec.leadershipRecords, function(index, ldrRec) {
								visRecLdrTable.find('tbody')
								.append(
									$('<tr></tr>')
									.append(
										$('<td>' + ldrRec.ldrLNID + '</td>')
									)
									.append(
										$('<td>' + ldrRec.ldrBU + '</td>')
									)
									.append(
										$('<td>' + ldrRec.ldrAttnd + '</td>')
									)
									.append(
										$('<td>' + ldrRec.ldrLoc + '</td>')
									)
									.append(
										$('<td>' + ldrRec.ldrDate + '</td>')
									)
								);
							});
							
							visRecOvrvwDiv.append(visRecOvervwTable);
							visRecItiDiv.append(visRecItiTable);
							visRecLdrDiv.append(visRecLdrTable);
							
							
							visRecTabUl.append($('<li><a href="#visRecOvrvw' 
											+ visitRec._id.$oid + '">Visit Overview</a></li>'))
									.append($('<li><a href="#visRecVis' 
											+ visitRec._id.$oid + '">Visitors</a></li>'))
									.append($('<li><a href="#visRecIti' 
											+ visitRec._id.$oid + '">Visit Itinerary</a></li>'))
									.append($('<li><a href="#visRecLdr' 
											+ visitRec._id.$oid + '">IBM India Leadership Participation</a></li>'));
							
							visRecTabDiv.append(visRecTabUl)
								.append(visRecOvrvwDiv)
								.append(visRecVisDiv)
								.append(visRecItiDiv)
								.append(visRecLdrDiv);
							visRecTabDiv.tabs();
							
							visRecDiv.append(visRecTabDiv);
							visRecDiv.dialog({
								height: 600,
								width: 800,
								modal: false,
								hide: 'clip',
								show: 'clip',
								buttons: {
									'Close': function() {
										$(this).dialog('close');
										$(this).dialog('destroy');
									}
								},
								close: function() {
									$(this).dialog('close');
									$(this).dialog('destroy');
								}
							});
							
							$('#messagesDiv').empty();
							$('#messagesDiv').empty().append(
									$('<p><img src="./images/complete_status.gif">'
									+ 'Opened visit record #' + id + 'successfully.</p>')).show('scale')
								.delay(2000)
								.hide('scale');
							
						},
						
						error: function(jqXHR, textStatus, errorThrown) {
							$('#messagesDiv').empty();
							$('#messagesDiv').empty().append(
									$('<p><img src="./images/complete_error.gif">'
									+ 'Operation Failed: ' 
									+ errorThrown + '</p>')).show('scale')
								.delay(2000)
								.hide('scale');
						}
					});
				}
			});
		}
	});
}

// This function creates a generic check box html item wrapped in a table cell.
function createCheckBox() {
	return $('<td><input type="checkbox"></td>');
}

// This function is used to add a visitor record line to the create visit form.
function addVisitor() {
	var visitorNameTxt = $('#visitorNameTxt').val();
	var visitorRoleTxt = $('#visitorRoleTxt').val();
	var visitorPrimaryCheck = $('#visitorPrimaryCheck').val();
	
	// Do validation here
	// showValidationError('visitorsListTab', 'visitorsListTabMsgBox', '');
	if ( visitorNameTxt == null || visitorNameTxt == undefined || visitorNameTxt.trim().length  <= 0 ) {
		showValidationError('visitorsListTab', 'visitorsListTabMsgBox', 'You did not enter a Visitor Name');
		return;
	}
	
	if ( visitorRoleTxt == null || visitorRoleTxt == undefined || visitorRoleTxt.trim().length <= 0 ) {
		showValidationError('visitorsListTab', 'visitorsListTabMsgBox', 'You did not enter a visitor Role');
		return;
	}
	
	// Turn primary option off if primary visitor is being entered
	if ( visitorPrimaryCheck === "Yes" ) {
		$('#visitorPrimaryCheck > option[value="Yes"]').attr('disabled', 'disabled');
		$('#visitorPrimaryCheck > option[value="No"]').attr('selected', true);
	}
	
	if ( visitorPrimaryCheck != "Yes" ) {
		visitorPrimaryCheck = "No";
	}
	
	var insertRow = $('<tr></tr>').append(createCheckBox())
					.append($('<td>' + visitorNameTxt + '</td>'))
					.append($('<td>'+ visitorRoleTxt + '</td>'))
					.append($('<td>' + visitorPrimaryCheck + '</td>'));
	
	$('#visitorsList > tbody').append(insertRow);
}

// This function is used to remove a visitor record line from the create visit form.
function removeVisitor() {
	// first get the table
	$('#visitorsList > tbody > tr').each(function(index) {
		var chkBox = $(this).find('td > input[type="checkbox"]');
		if ( chkBox.prop('checked') == true ) {
			// okay if this was a primary visitor, let's go ahead and re-enable primary
			$(this).find('td').each(function(index){
				if ( index == 3 ) {
					if ( $(this).text() === "Yes" ) {
						$('#visitorPrimaryCheck > option[value="Yes"]').removeAttr('disabled');
						$('#visitorPrimaryCheck > option[value="Yes"]').attr('selected', true);
					}
				}
			});
			$(this).remove();
		}
	});
}

// This function is used to add an itinerary item to the create visit record form
function addItinerary() {
	var itiLoc = $('#itiLoc').val();
	var itiStart = $('#itiStart').val();
	var itiEnd = $('#itiEnd').val();
	
	// Do validation here
	// showValidationError('visitItineraryTab', 'visitItineraryTabMsgBox', '');
	
	if ( itiStart == null || itiStart == undefined || itiStart.length <= 0 ){
		showValidationError('visitItineraryTab', 'visitItineraryTabMsgBox', 'You did not select the start date');
		return;
	}
	
	if ( itiEnd == null || itiEnd == undefined || itiEnd.length <= 0 ) {
		showValidationError('visitItineraryTab', 'visitItineraryTabMsgBox', 'You did not select the end date');
		return;
	}
	
	var insertRow = $('<tr></tr>').append(createCheckBox())
				.append($('<td>' + itiLoc + '</td>'))
				.append($('<td>' + itiStart + '</td>'))
				.append($('<td>' + itiEnd + '</td>'));
	
	$('#visitItineraryList > tbody').append(insertRow);
}

// This function is used to remove an itinerary item from the create visit record form
function removeItinerary() {
	$('#visitItineraryList > tbody > tr').each(function(index) {
		var chkBox = $(this).find('td > input[type="checkbox"]');
		if ( chkBox.prop('checked') == true ) {
			$(this).remove();
		}
	});
}

// This function adds a Leadership Participation record to the create Visit form
function addLdrPart() {
	var ldrLNID = $('#ldrLNID').val();
	var ldrBU = $('#ldrBU').val();
	var ldrAttnd = $('#ldrAttnd').val();
	var ldrLoc = $('#ldrLoc').val();
	var ldrDate = $('#ldrDate').val();
	
	// Do validation here
	// showValidationError('leadershipParticipationTab', 'ldrPartMsgBox', '');
	// return;
	
	if ( ldrLNID == null || ldrLNID == undefined || ldrLNID.trim().length <= 0 ) {
		showValidationError('leadershipParticipationTab', 'ldrPartMsgBox', 'You did not enter Leader LN ID');
		return;
	}
	
	if ( ldrDate == null || ldrDate == undefined || ldrDate.trim().length <= 0 ) {
		showValidationError('leadershipParticipationTab', 'ldrPartMsgBox', 'You did not enter Leader Participation Date');
		return;
	}
	
	var insertRow = $('<tr></tr>').append(createCheckBox())
				.append($('<td>' + ldrLNID + '</td>'))
				.append($('<td>' + ldrBU + '</td>'))
				.append($('<td>' + ldrAttnd + '</td>'))
				.append($('<td>' + ldrLoc + '</td>'))
				.append($('<td>' + ldrDate + '</td>'));
	
	$('#leadershipParticipationList > tbody').append(insertRow);
}

// This function removes a Leadership Participation record from the create visit form.
function removeLdrPart() {
	$('#leadershipParticipationList > tbody > tr').each(function(index) {
		var chkBox = $(this).find('td > input[type="checkbox"]');
		if ( chkBox.prop('checked') == true ) {
			$(this).remove();
		}
	});
}

$(function() {
	// bootstrap bill :)
	// initializing GUI components...
	
	// Hide all message boxes
//	$('#messagesDiv').hide('slide');
	
	$.each($('.message-box'), function(index, msgBox) {
		$(msgBox).hide('slide');
	});
	
	// activate tooltips
	$( document ).tooltip();
	
	$('#createVisitBtn').button().click(openVisitDialog);
	$('#openVisitBtn').button().click(openSelectedVisitRecords);
	$('#searchVisitBtn').button();
	$('#addVisitorBtn').button().click(addVisitor);
	$('#deleteVisitBtn').button().click(openDeleteConfirmationDialog);
	$('#exportRptBtn').button();
	$('#removeVisitorsBtn').button().click(removeVisitor);
	$('#itiAddBtn').button().click(addItinerary);
	$('#itiRemoveBtn').button().click(removeItinerary);
	$('#ldrPartAdd').button().click(addLdrPart);
	$('#ldrPartRemove').button().click(removeLdrPart);
	$('#createVisitTabs').tabs();
	$('#visitTypeRadio').buttonset();
	$('#itiStart').datepicker();
	$('#itiEnd').datepicker();
	$('#ldrDate').datepicker();
	
	// loading data now
	fetchAllVisitRecords();
});