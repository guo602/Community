$(function(){
	$("#publishBtn").click(publish);
	$("#recipient-name, #message-text").on("input", togglePublishButton);
	$("#publishModal").on("shown.bs.modal", togglePublishButton);
	togglePublishButton();
});

function publish() {
	if ($("#publishBtn").prop("disabled")) {
		return;
	}
	$("#publishModal").modal("hide");
	//获取标题 内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	//异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{
			"title":title,
			"content":content,
		},
		function(data){
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新页面
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000)

		}
	);


}

function togglePublishButton() {
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	var canPublish = $.trim(title).length > 0 && $.trim(content).length > 0;
	$("#publishBtn").prop("disabled", !canPublish);
}