angular.module("rooster").factory(
	"AuthenticationService", 
	[ '$interval', '$timeout', '$location', 'Restangular', '$rootScope', function($interval,$timeout,$location,Restangular,$rootScope) {

		console.log("authentication service <init>");
		
		var s = {};
		
		s.checked = false;
		
		s.current = "-NONE-";
		s.user = {};

		s.renew = function() {
			
			Restangular.all("api/authenticate").customPOST().then(
				function(u) {
					console.log("authentication service renew(): finished: ",u);
					var c = JSON.stringify(u);
					if(!u.id) {
						if(!$location.path().startsWith("/login")) {
							console.log("authentication service renew(): redirecting to /login");
							$location.path("/login");
						} else {
							console.log("authentication service renew(): already on: "+$location.path());
						}
					} else {
						console.log("authentication service renew(): user id: "+u.id, u);
						if($location.path().startsWith("/login")) {
							console.log("authentication service renew(): redirecting to /");
							$location.path("/");
						}
					} 
					
					if(s.current==c) {
						console.log("authentication service renew(): same, ignore");
						return; 
					} else {
						console.log("authentication service renew(): user changed, broadcasting ... ");
						s.current = c;
						s.user = u;
						console.log("auth service - sending auth event ... ");
						$rootScope.$broadcast("auth", u);
					}
					
				}
			); 
			console.log("authentication service renew()");
		}
		

		s.login = function(username, password, success, error) {
			Restangular.all("api/authenticate").customPOST({},"",{username:username, password:password}).then(
					function(x) {
						$timeout(s.renew,2000);
						if(success) { success(x); }
					},
					function(x) {
						$timeout(s.renew,2000);
						if(error) { error(x); }
					}
			);
		}

		s.logout = function() {
			Restangular.all("api/authenticate").customDELETE().then(s.renew,s.renew);
		}
		
		
		$interval(s.renew,60000);
		
		s.renew();
		
		return s;
		 
	}]
	
);
