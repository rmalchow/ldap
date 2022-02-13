angular.module("rooster").controller(
	"LoginController", 
	[ '$timeout', '$routeParams', '$rootScope', '$scope', 'AuthenticationService', 'Restangular', function($timeout,$routeParams,$rootScope,$scope,AuthenticationService,Restangular) {

		var login = this;
		login.status =  1;
		login.username = "";
		login.password = "";

		login.redirect = $routeParams["redirect"]
		
		$scope.$on("$destroy",function() {
			$("body").attr("style", "");
		});
		
		Restangular.all("ui/doors/doors.json").getList().then(
			function(doors) {
				if (doors.length == 0) return;
				rand = Math.floor(doors.length * Math.random());
				login.door = doors[rand];
				$("body").attr("style", "background: url('doors/"+login.door.file+"') no-repeat; background-size: cover; background-position: center; width: 100%; height: 100%");
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
		
		login.reset = function() {
			$location.path("/reset");
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
		
		$("#username").focus();
		
		console.log("login controller <init> - ",AuthenticationService);
	}]
	
);
