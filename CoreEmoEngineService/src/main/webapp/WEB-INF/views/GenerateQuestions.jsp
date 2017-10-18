<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Generate Questions</title>
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

		<div class="row">
			<div class="col-sm-6 col-md-6 col-lg-6">
				<h6>
					<span class="label label-info">Game Selected: ${simName}</span>
				</h6>
			</div>
			<div class="col-sm-6 col-md-6 col-lg-6">
				<h6>
					<span class="label label-info">Player: ${playerId}</span>
				</h6>
			</div>
		</div>

		<form method="post" action="/CoreEmoEngineService/game/view/init">
			<div class="row">

				<div class="col-sm-6 col-md-6 col-lg-6">
					<input type="hidden" name="simName" value="${simName}" /> 
					<input type="hidden" name="playerId" value="${playerId}" />
				</div>
				<div class="col-sm-6 col-md-6 col-lg-6">
					<input type="submit" value="Generate Questions For the game"
						onclick="this.form.submit(); this.disabled=true; this.value='Generating…'; ">
				</div>

			</div>
		</form>
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