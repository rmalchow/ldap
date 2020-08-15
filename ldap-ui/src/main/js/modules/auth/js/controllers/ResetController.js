angular.module("rooster").controller(
	"ResetController", 
	[ '$timeout', '$routeParams', '$rootScope', 'AuthenticationService', 'Restangular', function($timeout,$routeParams,$rootScope,AuthenticationService,Restangular) {

		var reset = this;
		reset.status =  1;
		reset.username = "";
		reset.password = "";
		
		reset.initiateStep = 1;
		
		reset.pw = {
			pw1:"",
			pw2:"",
			ok:false
		}
		
		Restangular.all("ui/doors/doors.json").getList().then(
			function(doors) {
				if (doors.length == 0) return;
				rand = Math.floor(doors.length * Math.random());
				reset.door = doors[rand];
				reset.update();
			}
		);
		
		reset.cancel = function() {
			AuthenticationService.logout();
		}

		reset.initiateReset = function() {
			AuthenticationService.initiateReset(
				reset.username,
				function() {
					reset.initiateStep = 2;
				},
				function () {
					console.log("reset failed ... ");
				}
			)
		}
		
		reset.completeReset = function() {
			reset.initiateStep = -1;
			$timeout(
				function() {
		
					AuthenticationService.login(
						reset.username, 
						reset.token,
						function() {
							console.log("reset controller: login: success ... ");
						},
						function () {
							console.log("reset controller: login: error ... ");
							reset.token = "";
							reset.initiateStep = -2;
							$timeout(
								function() { 
									reset.initiateStep = 1; 
									console.log("reset controller: login: start over ... ");
								}, 
								3000
							);
						}
					)
				},
				1000
			);
		}

		reset.step = 1;
		
		reset.updatePassword = function() {
			reset.step = -1;
			$timeout(
				function() {
					AuthenticationService.updatePassword(
							reset.pw.pw1, 
							function() {
								console.log("reset controller: login: success ... ");
								AuthenticationService.renew();
							},
							function () {
								reset.pw.pw1 = "";
								reset.pw.pw2 = "";
								check(reset.pw);
								reset.step = -2;
								$timeout(
									function() { 
										reset.step = 1; 
										console.log("reset controller: login: start over ... ");
									}, 
									3000
								);
							}
						)
				}, 1000
			);
		}
		
		
		reset.check = function(pw) {
			
			pw.pr = 0;
			pw.state = 0;
			pw.ok = false;

			if(pw.pw1.length == 0 && pw.pw2.length == 0) return;
			
			pw.state = 1;
			
			pr = 0;
			pt = 0;

			pt = pt + 8;
			pr = pr + Math.min(8,pw.pw1.length);
			
			pt++;
			if(pw.pw1.match("[a-z]+")) pr++;

			//pt++;
			//if(pw.pw1.match("[A-Z]+")) pr++;

			pt++;
			if(pw.pw1.match("[0-9]+")) pr++;

			pw.progress = pr / (pt/100);

			$timeout(
				function() {
				    pw.progressWidth = {
				        'width': pw.progress + "%"
				    };
				}
			);
			
			if(pr == pt && pw.pw1 == pw.pw2) {
				pw.ok = true;
			}
		}

		reset.update = function() {
			
			var height = $(window).height();
			var width = $(window).width();
			
			var hr = reset.door.height / height;
			var wr = reset.door.width / width;
			
			var r = Math.min(wr,hr);
			
			var w = reset.door.width / r;
			var h = reset.door.height / r;
			
			$('#login-hero').width(w);
			$('#login-hero').height(h);
			
		}
		
		$( window ).resize(reset.update);

		reset.user = AuthenticationService.user;

		$rootScope.$on("auth", function() {
			reset.user = AuthenticationService.user;
			console.log("reset controller (on auth) - ",reset.user.id);
		});
		
		AuthenticationService.renew(); 
		
		console.log("reset controller <init> - ",reset.user.id);
	}]
	
);
