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

var  form = dialog.find('form').on('submit', function(evt) {
	evt.preventDefault();
	createVisitRecord();
});

function openVisitDialog() {
	dialog.dialog('open');
}

function createVisitRecord() {
	$('#createVisitForm').validate();
	
	dialog.dialog('close');
	
	$('#messagesDiv').empty().append($('<p>Creating Visit Record ... ' + 
			'<img src="./images/spinner.gif"></p>'));
	
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
					+ 'Visit record '
					+ ' created successfully.</p>')).show('scale')
				.delay(2000)
				.hide('scale');
			
			// Now add the new visit record row to the home page list / report
			var insertRow = $('<tr></tr>').append(createCheckBox())
							.append($('<td>' + /* TODO -- ADD VISIT ID LATER */ + '</td>'))
							.append($('<td>' + visitTypeChoice + '</td>'))
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

// This function creates a generic check box html item wrapped in a table cell.
function createCheckBox() {
	return $('<td><input type="checkbox"></td>');
}

// This function is used to add a visitor record line to the create visit form.
function addVisitor() {
	var visitorNameTxt = $('#visitorNameTxt').val();
	var visitorRoleTxt = $('#visitorRoleTxt').val();
	var visitorPrimaryCheck = $('#visitorPrimaryCheck').val();
	
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
	$('#messagesDiv').hide('slide');
	$('#createVisitBtn').button().click(openVisitDialog);
	$('#searchVisitBtn').button();
	$('#addVisitorBtn').button().click(addVisitor);
	$('#deleteVisitBtn').button();
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
});