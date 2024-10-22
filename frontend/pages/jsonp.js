(function() {
	const response = {
		data: {
			type: "page",
			title: "标题",
			body: "我是jd配置文件"
		},
		status: 0
	}

	window.jsonpCallback && window.jsonpCallback(response);
})();
