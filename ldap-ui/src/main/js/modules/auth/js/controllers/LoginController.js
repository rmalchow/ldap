angular.module("rooster").controller(
	"LoginController", 
	[ '$timeout', '$routeParams', '$rootScope', 'AuthenticationService', 'Restangular', function($timeout,$routeParams,$rootScope,AuthenticationService,Restangular) {

		var login = this;
		login.status =  1;
		login.username = "";
		login.password = "";

		login.redirect = $routeParams["redirect"]
		
		Restangular.all("ui/doors/doors.json").getList().then(
			function(doors) {
				if (doors.length == 0) return;
				rand = Math.floor(doors.length * Math.random());
				login.door = doors[rand];
				login.update();
			}
		);
		
		login.check = function() {
			login.status = 1;
			if(login.username.length < 2) {
				return;
			}
			if(login.password.length < 5) {
				return;
			}
			login.status = 0;
		}
		
		
		login.login = function() {
			login.status = -1;
			AuthenticationService.login(login.username, login.password, 
				function(user) {
					console.log("200: ",user);
					if(!user.id) {
						login.status = -2;
						login.password = "";
					}
				},
				function(x) {
					console.log("NOT 200: ",x);
					login.status = -2;
					login.password = "";
				}
			);
			
		}
		
		
		login.update = function() {

			var height = $(window).height();
			var width = $(window).width();
			
			var hr = login.door.height / height;
			var wr = login.door.width / width;
			
			var r = Math.min(wr,hr);
			
			var w = login.door.width / r;
			var h = login.door.height / r;
			
			$('#login-hero').width(w);
			$('#login-hero').height(h);
			
		}
		
		$("#username").focus();
		
		$( window ).resize(login.update);
		
		
		console.log("login controller <init> - ",AuthenticationService);
	}]
	
);
