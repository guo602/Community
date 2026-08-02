var CONTEXT_PATH = "/community"

window.alert = function(message) {
	if(!$(".alert-box").length) {
		$("body").append(
			'<div class="modal alert-box" tabindex="-1" role="dialog">'+
				'<div class="modal-dialog modal-dialog-centered" role="document">'+
				'<div class="modal-content alert-content">'+
					'<div class="modal-header alert-header">'+
						'<h5 class="modal-title alert-title">操作提示</h5>'+
						'<button type="button" class="close" data-dismiss="modal" aria-label="Close">'+
							'<span aria-hidden="true">&times;</span>'+
						'</button>'+
					'</div>'+
					'<div class="modal-body alert-body">'+
						'<p class="alert-message"></p>'+
					'</div>'+
					'<div class="modal-footer alert-footer">'+
						'<button type="button" class="btn btn-primary alert-confirm-btn" data-dismiss="modal">我知道了</button>'+
					'</div>'+
					'</div>'+
				'</div>'+
			'</div>'
		);
	}
	
	$(".alert-box .alert-message").text(message);
	$(".alert-box").modal("show");
}
