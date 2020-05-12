angular.module("rooster").controller(
	"StatusController", 
	[ '$interval', '$rootScope', 'AuthenticationService', function($interval, $rootScope, AuthenticationService) {

		var status = this;

		
		status.update = function() {
			console.log("status controller update()");
			status.user = AuthenticationService.user;
		}
		
		status.logout = function() {
			AuthenticationService.logout();
		}
		
		$rootScope.$on("auth", status.update);
		
		status.update();
		console.log("status controller <init>");
	}]
	
);
