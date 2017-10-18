<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Game Selection</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
</head>
<body>

	<div class="container-fluid" id="google_translate_element">
		<h2>About the Game</h2>
		<br />
		<p>${description}</p>
		<br /> <br /> <br /> <br /> <br />
		<div class="row">
			<div class="col-sm-6 col-md-6 col-lg-6">
				<form method="get" action="/CoreEmoEngineService/game/view/home">
					<input type="submit" value="New Player">
				</form>
			</div>
			<div class="col-sm-6 col-md-6 col-lg-6">
				<form method="get" action="/CoreEmoEngineService/game/view/existing">
					<input type="submit" value="Existing Player">
				</form>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		function googleTranslateElementInit() {
			new google.translate.TranslateElement({
				pageLanguage : 'en'
			}, 'google_translate_element');
		}
	</script>

	<script type="text/javascript"
		src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>

</body>
</html>