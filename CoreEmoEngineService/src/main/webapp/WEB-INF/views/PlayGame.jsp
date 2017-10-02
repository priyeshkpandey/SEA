<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Play Game</title>
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

	<div class="container-fluid">

		<div class="row">
			<div class="col-sm-4 col-md-4 col-lg-4">
				<h6>
					<span class="label label-info">Game Selected: ${simName}</span>
				</h6>
			</div>
			<div class="col-sm-4 col-md-4 col-lg-4">
				<h6>
					<span class="label label-info">Player: ${playerId}</span>
				</h6>
			</div>
			<div class="col-sm-4 col-md-4 col-lg-4">
				<h6>
					<span class="label label-info">Duration Range: 0 to
						${maxIter}</span>
				</h6>
			</div>
		</div>


		<div class="row">
			<div class="col-sm-12 col-md-12 col-lg-12">
				<div class="form-group">
					<label for="statements">Statements:</label>
					<textarea readonly class="form-control" rows="10" id="statements">${statements}</textarea>
					<label for="iteration">Enter duration for which to get
						statement:</label> <input type="text" id="iteration"> <input
						type="button" value="Get Statement"
						onclick="getStatement(document.getElementById('iteration').value);  ">
				</div>
			</div>
		</div>

		<form method="post" action="/CoreEmoEngineService/game/view/question">
			<div class="row">
				<div class="col-sm-6 col-md-6 col-lg-6">
					<input type="hidden" name="simName" value="${simName}" /> <input
						type="hidden" name="playerId" value="${playerId}" />
				</div>
				<div class="col-sm-6 col-md-6 col-lg-6">
					<input type="submit" value="Attempt Question"
						onclick="this.form.submit(); this.disabled=true; this.value='Opening question…'; ">
				</div>

			</div>
		</form>
        <input type="button" value="Get Score" onclick="getScore();"/>
	</div>

	<script type="text/javascript">
function getStatement(iterNo) {
	$.ajax({
	    contentType: 'application/json',
	    data: JSON.stringify({
	        "simId": ${simId},
	        "userId": null,
	        "playerId": "${playerId}",
	        "iterNo": iterNo
	    }),
	    dataType: 'json',
	    success: function(statement){
	    	var line = 'Duration ' + statement.iterNo + ': ' + statement.agentId + ' said "' + statement.statement + '"\n';
	    	$('#statements').append(line); 
	    },
	    error: function(){
	        alert("Getting statement failed");
	    },
	    type: 'POST',
	    url: '/CoreEmoEngineService/game/post/statement'
	});
}

function getScore() {
	$.ajax({
	    url: '/CoreEmoEngineService/game/score?playerId=${playerId}&simId=${simId}',
	    type: 'GET',
	    success: function (score) {
	      alert("Your score till now is: " + score);
	    },
	    error: function () {
	      alert("Error getting score");
	    }
	  });
}

</script>

</body>
</html>