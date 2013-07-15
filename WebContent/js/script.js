$(function() {
	var activeWindow = 0; //0 == None; 1 == Registration; 2 == Login

	$("#loginButton").hide();
	$("#registerButton").hide();
	$("#loginWindow").hide();
	$("#registerWindow").hide();
	$("#cancelButton").hide();
	$("#greetingHeader").hide();


	$("#loginButton").delay(500).fadeIn(600);
	$("#registerButton").delay(500).fadeIn(600);
	$("#greetingHeader").delay(500).fadeIn(300);

	$("#registerButton").click(function() {
		if(activeWindow == 0) {
			$("#loginButton").fadeOut(300);
			$("#greetingHeader").fadeOut(300);

			$("#registerButton").animate({
				top: '375px',
				left: '50%',
				marginLeft: '-75px'
			});

			$("#cancelButton").delay(400).fadeIn(600);
			$("#registerWindow").delay(400).fadeIn(600);

			activeWindow = 1;

			return;
		} else if(activeWindow == 1) { //User is submitting a registration form
			document.getElementById("registerButton").setAttribute("type","submit");
			document.getElementById("registerButton").setAttribute("form","register");
		}
	});	

	$("#loginButton").click(function() {
		if(activeWindow == 0) {
			$("#registerButton").fadeOut(300);
			$("#greetingHeader").fadeOut(300);

			$("#loginButton").animate({
				top: '375px',
				right: '50%',
				marginRight: '-75px'
			});

			$("#cancelButton").delay(400).fadeIn(600);
			$("#loginWindow").delay(400).fadeIn(600);

			activeWindow = 2;

			return;
		} else if(activeWindow == 2) { //User is submitting a registration form
			document.getElementById("loginButton").setAttribute("type","submit");
			document.getElementById("loginButton").setAttribute("form","login");
		}
	});

	$("#cancelButton").click(function() {
		if(activeWindow == 1) {
			$("#registerWindow").fadeOut(600);
			$("#cancelButton").fadeOut(600);

			$("#registerButton").delay(400).animate({
				top: '300px',
				left: '35%',
				marginLeft: '0px'
			});

			$("#loginButton").delay(700).fadeIn(300);
			$("#greetingHeader").delay(700).fadeIn(300);

			document.getElementById("registerButton").setAttribute("type","button");
		} else if(activeWindow == 2) {			
			$("#loginWindow").fadeOut(600);
			$("#cancelButton").fadeOut(600);

			$("#loginButton").delay(400).animate({
				top: '300px',
				right: '35%',
				marginRight: '0px'
			});

			$("#registerButton").delay(700).fadeIn(300);
			$("#greetingHeader").delay(700).fadeIn(300);
		}

		activeWindow = 0;
	});
});