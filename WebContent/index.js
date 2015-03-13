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
	createVisitDialog();
});

function openVisitDialog() {
	dialog.dialog('open');
}

function createVisitRecord() {
	
}

$(function() {
	// bootstrap bill :)
	$('#messagesDiv').hide('slide');
	$('#createVisitBtn').button().click(openVisitDialog);
	$('#searchVisitBtn').button();
	$('#addVisitorBtn').button();
	$('#removeVisitorsBtn').button();
	$('#itiAddBtn').button();
	$('#itiRemoveBtn').button();
	$('#ldrPartAdd').button();
	$('#ldrPartRemove').button();
	$('#createVisitTabs').tabs();
	$('#visitTypeRadio').buttonset();
});