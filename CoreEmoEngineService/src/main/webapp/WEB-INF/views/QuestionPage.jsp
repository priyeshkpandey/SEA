<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Question</title>
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
		<h2>Question:</h2>
		<p>${question}</p>
		<form>
			<div class="radio">
				<label><input type="radio" name="option"
					value="${optionOne}">${optionOne}</label>
			</div>
			<div class="radio">
				<label><input type="radio" name="option"
					value="${optionTwo}">${optionTwo}</label>
			</div>
			<div class="radio">
				<label><input type="radio" name="option"
					value="${optionThree}">${optionThree}</label>
			</div>
			<div class="radio">
				<label><input type="radio" name="option"
					value="${optionFour}">${optionFour}</label>
			</div>
			<div class="radio">
				<label><input type="radio" name="option"
					value="None of these">None of these</label>
			</div>
			<input type="button" value="Submit Answer"
				onclick="alertAnswerResult(getTheSelectedAnswer()); this.disabled=true; this.value='Submitting answer…'; ">
		</form>

		<form method="get" action="/CoreEmoEngineService/game/view/play">
			<div class="row">

				<div class="col-sm-6 col-md-6 col-lg-6">
					<input type="hidden" name="simName" value="${simName}" /> <input
						type="hidden" name="playerId" value="${playerId}" />
				</div>
				<div class="col-sm-6 col-md-6 col-lg-6">
					<input type="submit" value="Return to Game"
						onclick="this.form.submit(); this.disabled=true; this.value='Loading Game…'; ">
				</div>

			</div>
		</form>
	</div>

	<script type="text/javascript">
function alertAnswerResult(selectedAnswer) {
	$.ajax({
	    contentType: 'application/json',
	    data: JSON.stringify({
	        "qaStateId": ${qaStateId},
	        "question": null,
	        "options": null,
	        "selectedAnswer": selectedAnswer
	    }),
	    dataType: 'json',
	    success: function(result){
	    	if(result) {alert("Answer was correct.")}
	    	else {alert("Answer was incorrect.")}
	    },
	    error: function(){
	        app.log("Validating answer failed");
	    },
	    type: 'POST',
	    url: '/CoreEmoEngineService/game/answer'
	});
}

function getTheSelectedAnswer() {
	return document.querySelector('input[name="option"]:checked').value;
}

</script>

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