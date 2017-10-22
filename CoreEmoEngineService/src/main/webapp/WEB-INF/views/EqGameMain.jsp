<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>The EQ Game</title>
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
<br/><br/><br/><br/><br/>
	<div class="container-fluid" id="google_translate_element">
		<form method = "post" action = "/CoreEmoEngineService/game/view/open">
			<div class="row">
				<div class="col-sm-4 col-md-4 col-lg-4">
					Enter Unique User Name : <input type="text" name="playerId">
				</div>
				<div class="col-sm-4 col-md-4 col-lg-4">
					Select Game: <select id="game_selection" name = "simName">
						<c:forEach var="gameName" items="${games}">
							<option>${gameName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-4 col-md-4 col-lg-4">
					<input type="submit" value="Create Game">
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