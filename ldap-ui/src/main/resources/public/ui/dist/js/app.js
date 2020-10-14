if (!String.prototype.includes) {
	String.prototype.includes = function(search, start) {
		if (typeof start !== 'number') {
			start = 0;
		}
		if (start + search.length > this.length) {
			return false;
		} else {
			return this.indexOf(search, start) !== -1;
		}
	};
}
if (!Array.prototype.includes) {
	Array.prototype.includes = function(search, start) {
		if (typeof start !== 'number') {
			start = 0;
		}
		if (start + search.length > this.length) {
			return false;
		} else {
			return this.indexOf(search, start) !== -1;
		}
	}; 
}  


var app = angular.module("rooster",["templates","ngRoute","angular-plugin","angular-select","restangular","angularMoment"]); 

angular.module("rooster").factory("OptionsRestangular", ["Restangular" , function(Restangular) {
	out = Restangular.withConfig(function(RestangularConfigurer) {
	    RestangularConfigurer.setFullResponse(true);
	});
	
	out.options = function(path,success) {
		path.options().then(
			function(response) {
				var x = response.headers('Allowed-Methods');
				out = [];
				if(x) {
					x = x.split(",");
				}
				console.log("OPTIONS RESPONSE: ",path,x);
				success(x);
			},
			function() { success([]); }
		);
	};
	
	
	return out;
}]);
angular.module("rooster").filter('escape', function() {
	  return window.encodeURIComponent;
});
angular.module("rooster").config(
	[ '$httpProvider', function($httpProvider) {
		/**
		$httpProvider.interceptors.push(function($timeout,AlertService,$q){
			return {
				request : function(config) {
					return config;
				},
				response : function(response) {
					return response;
				},
				responseError: function(response) {
					
					if(response.status==401) {
						
					} else if(response.status!=502) {
						AlertService.alert("ERROR",response.data.message, response.headers("x-sleuth-request-id"), response.headers("date"));
					}
					return $q.reject(response);
			    }
			};
		});
		**/
	}]
);
angular.module("rooster").run(
	[ 'PluginMenuService','$location', '$rootScope', 
	function(PluginMenuService,$location,$rootScope) {

		$rootScope.fade = true;
		// state change decoration 
		$rootScope.$on('$routeChangeStart', function() { $rootScope.fade = false; } );
		$rootScope.$on('$routeChangeSuccess', function() { $rootScope.fade = true; $("html, body").animate({ scrollTop: 0 }, "fast"); } );
		
		//PluginMenuService.addItem("/areas/top_bar","/language",{"title" : "Language Selection", "id" : "TOP_BAR_LANGUAGE", "visible" : true, "templateUrl" : "language.html", "order" : -8});
	}]
);
angular.module("rooster").filter('join', function () {
    return function (input,delimiter) {
        return (input || []).join(delimiter || ', ');
    };
});

angular.module("rooster").run(
		['PluginMenuService', 'AuthenticationService', '$location', '$rootScope',
		function(PluginMenuService, AuthenticationService, $location, $rootScope) {

			var loginForm = {
					"order": 999, 
					"title" : "login",
					"id" : "LOGIN",
					"visible" :true, 
					"active" :false,
					"controller" : "LoginController",
					"controllerAs" : "login",
					"templateUrl" : "/auth/templates/login.html",
					"reloadOnSearch" : false
				};
			PluginMenuService.addItem("","/login", loginForm);
			
			var loginForm = {
					"order": 999, 
					"title" : "reset",
					"id" : "RESET",
					"visible" :true, 
					"active" :false,
					"controller" : "ResetController",
					"controllerAs" : "reset",
					"templateUrl" : "/auth/templates/reset.html",
					"reloadOnSearch" : false
				};
			PluginMenuService.addItem("","/reset", loginForm);
			
			console.log("main menu area ... ")
			
			var mainMenuArea = {
					"order": 999, 
					"title" : "main",
					"id" : "AREA_MAIN",
					"visible" :true, 
					"active" :false,
					"controller" : "MainMenuController",
					"templateUrl" : "/auth/templates/_main.html",
					"reloadOnSearch" : false
				};
			PluginMenuService.addItem("/area/top","/main", mainMenuArea);
			
			console.log("main menu ... ")
			
			var mainMenu = {
					"order": 999, 
					"title" : "main",
					"id" : "MAIN",
					"visible" :true, 
					"active" :false,
					"reloadOnSearch" : false
				};
			PluginMenuService.addItem("","/main",mainMenu);
			
			var ldapList = {
					"order": 999, 
					"title" : "LDAP",
					"id" : "LDAP_LIST",
					"visible" :true, 
					"active" :false,
					"controller" : "LdapListController",
					"controllerAs" : "ldap",
					"templateUrl" : "/auth/templates/ldap_list.html",
					"icon": "fas fa-address-book",
					"reloadOnSearch" : false
				};
			PluginMenuService.addItem("/main","/ldap", ldapList);


			var entryDetail = {
					"order": 999, 
					"title" : "Detail",
					"id" : "LDAP_DETAIL",
					"visible" :true, 
					"active" :false,
					"controller" : "LdapDetailController",
					"controllerAs" : "ldap",
					"templateUrl" : "/auth/templates/ldap_detail.html",
					"reloadOnSearch" : false
				};
			
			PluginMenuService.addItem("/main/ldap","/:objectId",entryDetail);

			PluginMenuService.setDefault("/main/ldap");
			
			console.log("update function ... ")

			update = function() {
				console.log("auth module <update>: ",AuthenticationService.user);
				var u = AuthenticationService.user;
				if(u.id) {
					mainMenuArea.visible = true;
				} else {
					mainMenuArea.visible = false;
				}
			}

			$rootScope.$on("auth",update);

			console.log(" ... done!")
			
		}]

);

angular.module("rooster").directive(
	"ldapTree",
	['Restangular','LdapService','AuthenticationService','$location',
	function(Restangular,LdapService,AuthenticationService, $location) {
		return {
	        restrict: 'A',
			templateUrl : "/auth/templates/ldap_tree.html",
			scope : {},
			link : function(scope, element, attrs, ctrl) {
				
		        scope.update = function() {
	        		LdapService.list({parentId: attrs.id, permission: [ "READ" ], includeIgnored : true },
		        			function(entries) {
	        					_.each(entries,function(e) {
	        						e.expanded = e.id == "00000000-0000-0000-0000-000000000000"
	        					})
		        				scope.entries = entries 
		        			}
		        		);
	        		LdapService.getPermissions(attrs.id,
		        			function(permissions) { 
		        				scope.permissions = permissions;
		        				scope.admin = AuthenticationService.user.admin; 
		        			}
		        		);
		        }
		        
				scope.open = function(id) {
					$location.path("/main/ldap/"+id);
				}

		        scope.ignore = function(entry) {
		        	console.log("ignore: ",entry)
		        	LdapService.ignore(entry.id,!entry.ignored,scope.update);
		        }
		        
		        scope.update();

			}
		};
	}]
);
angular.module("rooster").controller(
	"LdapDetailController", 
	[ '$timeout', '$rootScope', '$location', '$routeParams', 'LdapService', 'AuthenticationService',  function($timeout,$rootScope,$location,$routeParams, LdapService, AuthenticationService) {

		var ldap = this;

		ldap.updateChildrenInternal = function() {
			LdapService.list({parentId:ldap.id,permission:"READ", filter: ldap.childFilter}, function(children) { ldap.children = children; });
		}
		
		ldap.updateChildren = _.debounce(ldap.updateChildrenInternal, 400);
			
		ldap.setpw = {
			pw1 : "",
			pw2 : "",
			show : false,
			progress : 0,
			oldPw : "",
			state : 1
		};
		
		ldap.waitForMove = false;
		
		ldap.moveEntry = function() {
			console.log("entry .... ",ldap.entry);
			if(!ldap.entry.newParentId) {
				console.log("no new parent: going back .... ");
				ldap.entry.newParentId = ldap.entry.parentId;
				return;				
			}
			if(ldap.entry.newParentId == ldap.entry.parentId) {
				console.log("no change: ignoring .... ");
				return;				
			}
			ldap.waitForMove = true;
			console.log("move "+ldap.entry.entryId+" to: "+ldap.entry.newParentId);
			LdapService.moveTo(
				ldap.entry.id, ldap.entry.newParentId, 
				function() {
					ldap.waitForMove = false;
					ldap.update();
				}, 
				function() {
					ldap.entry.newParentId = ldap.entry.parentId;
					ldap.waitForMove = false;
					ldap.update();
				}, 
			);
		}
		
		ldap.update = function() {
			console.log("ldap user list controller: update()");
			ldap.entries = undefined;
			ldap.error = false;
			LdapService.get( 
				ldap.id,
				function(e) {
					e.newParentId = e.parentId;
					ldap.updateChildrenInternal();
					LdapService.getMembers(ldap.id, function(members) { ldap.members = members; });
					LdapService.getMemberships(ldap.id, function(memberships) { ldap.memberships = memberships; });
					LdapService.getPermissions(ldap.id, function(permissions) { 
						ldap.permissions = permissions;
						if(permissions.includes("ACL_READ")) {
							LdapService.getAcls(ldap.id, function(acls) {
								ldap.acls = acls;
							});
						}
					});
					if(e.system) {
						ldap.child.type = "UNIT";
					} else {
						ldap.child.type = "USER";
					}
					ldap.entry = e; 
					ldap.types = [e.type]; 
				},
				function(e) { ldap.error = true; }
			)
		}
		
		ldap.self = false;
		
		
		ldap.open = function(id) {
			
			if(!id) {
				console.log("LDAP DETAIL: open /// uid is empty");
				return;
			}

			ldap.self = AuthenticationService.user.id == id;
			console.log("LDAP DETAIL: "+AuthenticationService.user.id+" / "+id+" == "+ldap.self);
			
			console.log("LDAP DETAIL: open "+id);
			ldap.modified = false;
			//ldap.choseOtherId = id;
			if(ldap.id != id) {
				$location.path("/main/ldap/"+id);
				ldap.edit = false;
				ldap.showDangerZone = false;
				ldap.dangerZoneUnlocked = false;
				ldap.nameToUnlock = "";
				ldap.pw = {
					pw1 : "",
					pw2 : "",
					state : 0,
					ok : false
				} 
			}
			ldap.id = id;
			ldap.show = {};
			ldap.child = {
					type : "",
					name : ""
			}
			ldap.update();
		}
		
		ldap.saveInternal = function() {
			LdapService.save(ldap.id,ldap.entry,function(e) {ldap.open(e.id);});
		}
		
		ldap.save = _.debounce(ldap.saveInternal, 1000);
		
		ldap.modify = function() {
			ldap.modified = true;
			ldap.save();
		}
		
		ldap.toggleEdit = function() {
			if(ldap.edit) {
				if(ldap.modified) ldap.saveInternal();
				ldap.edit = false;
			} else if (ldap.permissions.includes('WRITE')) {
				ldap.edit = true;
			}
		}

		ldap.updateDisplayName = function(entry) {
			entry.displayname = [ entry.givenname, entry. familyname ].join(" ");
		} 
		
		ldap.checkCreateInternal = function(entry) {
			LdapService.checkCreate(
				ldap.id,entry,
				function(complaints) {
					console.log("ldap create: complaints = ",complaints);
					ldap.createComplaints = complaints;
				}
			)
		};
		
		
		ldap.toggleShowPassword = function(pw) {
			
			pw.visiblePw = !pw.visiblePw;
			
			if(pw.visiblePw) {
				console.log("ldap detail: show pw: start timer");
				pw.visibleTime = $timeout(function() { ldap.toggleShowPassword(pw); }, 3000) ;
			} else { 
				console.log("ldap detail: show pw: cancel timer");
				$timeout.cancel(pw.visibleTime);
			} 

		}
		
		ldap.generatePassword = function(pw) {
			pw.pw1 = "";
			pw.pw2 = "";
			$timeout(function () {
				ldap.generatePasswordInternal(pw);
			},300);
		}
		
		
		ldap.generatePasswordInternal = function(pw) {
			pw.pw1 = "";
			pw.pw2 = "";
			var a = "abcdefghijkmnopqrstuvwxyz";
			var b = "ABCDEFGHJKLMNPQRSTUVWXYZ";
			var c = "123456789";
			
			var h = [];
			
			h.push(a.charAt(Math.random()*a.length));
			h.push(b.charAt(Math.random()*b.length));
			h.push(c.charAt(Math.random()*c.length));

			var t = [a,b,c];
			
			while(h.length < 12) {
				var ti = Math.floor(Math.random()*3); 
				h.push(t[ti].charAt(Math.random()*t[ti].length));
			}
			
			_.shuffle(h);

			pw.pw1 = h.join("");
			pw.pw2 = pw.pw1;
			ldap.checkPassword(pw);
			
		}
		
		ldap.checkPassword = function(pw) {
			
			$timeout.cancel(pw.hideTime);
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
				$timeout.cancel(pw.hideTime);
				pw.ok = true;
			} else {
				pw.hideTime = $timeout(function() { pw.show = false; }, 8000) ;
			}
		}
		
		ldap.setPassword = function(pw) {
			pw.state = -1;
			LdapService.setPassword(ldap.entry.id, pw.oldPw, pw.pw1,
				function(x) {
					pw.state = -2;
					$timeout(
							function() {
								pw.oldPw = "";
								pw.pw1 = "";
								pw.pw2 = "";
								pw.progress = 0;
								pw.show = false;
								ldap.checkPassword(pw);
							},
							2000
						);	
				},
				function(x) {
					pw.state = -3;
				}
			);
		}
		
		ldap.checkCreate = _.debounce(ldap.checkCreateInternal, 400);

		ldap.createChild = function(entry) {
			LdapService.create(
				ldap.id,entry,
				function(entry) {
					ldap.open(entry.id);
					//ldap.updateChildren();
				}
			)
		};
		
		ldap.addMember = function(groupId, principalId) {
			LdapService.addMember(groupId, principalId,ldap.update);
		};
		
		ldap.removeMember = function(groupId, principalId) {
			LdapService.removeMember(groupId, principalId,ldap.update);
		};
		
		ldap.acl = {
				recursive : true,
				entryId : "",
				principalId : "",
				permission : ""
		};
		
		ldap.addAcl = function(id,acl) { LdapService.addAcl(id,acl,ldap.update);}
		ldap.removeAcl = function(id,aclId) { LdapService.removeAcl(id,aclId,ldap.update);}
		
		ldap.enableUser = function(id,enabled) { LdapService.enableUser(id,enabled,ldap.update);}
		
		ldap.resolve = LdapService.resolve();

		ldap.queryMoveTo = LdapService.query(["UNIT"],["CREATE"]);
		
		ldap.queryUsers = LdapService.query(["USER"],["READ"]);
		
		ldap.queryGroups = LdapService.query(["GROUP"],["EDIT_MEMBERS"]);
		
		ldap.queryPrincipals = LdapService.query([],["READ"]);
		
		console.log("ldap detail controller <init>  --- "+$routeParams["objectId"]);
		
		ldap.open($routeParams["objectId"]);
		
	}]
	
);

angular.module("rooster").controller(
	"LdapListController", 
	[ '$timeout', '$rootScope', '$location', 'LdapService', function($timeout,$rootScope,$location,LdapService) {

		var ldap = this;

		ldap.search = {
			offset : 0,
			max : 25,
			filter: "",
			permission : [ "READ" ],
			type : [],
			users: true,
			groups: true,
			units: true,
		}

		ldap.openFirst = function() {
			if(ldap.entries && ldap.entries.length > 0) {
				ldap.open(ldap.entries[0].id);
			}
		}
		
		ldap.updateInternal = function() {
			console.log("ldap user list controller: update()");
			ldap.entries = undefined;
			ldap.error = false;
			ldap.search.type = [];
			if(ldap.search.units) ldap.search.type.push("UNIT"); 
			if(ldap.search.groups) ldap.search.type.push("GROUP"); 
			if(ldap.search.users) ldap.search.type.push("USER"); 
			
			LdapService.list(
				ldap.search,
				function(e) { ldap.entries = e; },
				function(e) { ldap.error = true; }
			)
		}
		
		ldap.open = function(id) {
			$location.path("/main/ldap/"+id);
		}

		
		ldap.update = _.debounce(ldap.updateInternal, 400);
		
		ldap.updateInternal();

		$("#filter").focus();

		console.log("ldap group list controller <init>");
	}]
	
);

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

angular.module("rooster").controller(
	"MainMenuController", 
	[ '$rootScope', function( $rootScope) {
		console.log("main menu controller <init>");
	}]
	
);

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

angular.module("rooster").controller(
	"RootController", 
	[ '$timeout', '$rootScope', function($timeout,$rootScope) {

		var root = this;
		root.show = false;
		
		$rootScope.$on('auth', function() {
			console.log("root controller - received auth event ... ");
			root.show = true;
		});
		
		console.log("root controller <init> - ");
	}]
	
);

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
						if($location.path().startsWith("/login")) {
							// nope
						} else if ($location.path().startsWith("/reset")) {
							// nope
						} else {
							console.log("authentication service renew(): redirecting to /login");
							$location.path("/login");
						}
					} else {
						console.log("authentication service renew(): user id: "+u.id, u);
						if(u.needsReset) {
							$location.path("/reset");
						} else if(
								$location.path().startsWith("/login") ||
								$location.path().startsWith("/reset")
							) {
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
		
		s.updatePassword = function(password, success, error) {
			Restangular.all("api/authenticate/update").customPOST({},"",{newPassword:password}).then(success,error);
		};
		
		s.initiateReset = function(name, success, error) {
			Restangular.all("api/authenticate/reset").customPOST({},"",{username:name}).then(success,error);
		}
		
		s.login = function(username, password, success, error) {
			Restangular.all("api/authenticate").customPOST({},"",{username:username, password:password}).then(
					function(x) {
						$timeout(s.renew,2000);
						console.log("auth service: login: success ... ");
						if(success) { success(x); }
					},
					function(x) {
						$timeout(s.renew,2000);
						console.log("auth service: login: error ... ");
						if(error) { error(x); }
					}
			);
		}

		s.logout = function() {
			Restangular.all("api/authenticate").customDELETE().then(
				function() {
					$location.path("/login");
				},
				function() {
					$location.path("/login");
				}
			);
		}
		
		
		$interval(s.renew,60000);
		
		s.renew();
		
		return s;
		 
	}]
	
);

angular.module("rooster").factory(
	"EntryService", 
	[ '$interval', '$timeout', '$location', 'Restangular', 'AuthenticationService', function($interval,$timeout,$location,Restangular,AuthenticationService) {
		
		
		
	}]
);
angular.module("rooster").factory(
	"LdapService", 
	[ '$interval', '$timeout', '$location', 'Restangular', function($interval,$timeout,$location,Restangular) {

		console.log("ldap service <init>");
		
		var s = {};
		
		s.list = function(params,success,error) {
			Restangular.all("api/ldap/entries").getList(params).then(
				function(x) {
					console.log("ldap service: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service: error ",x);
					if(error) error(x);
				}
			);
		}
		
		s.get = function(id,success,error) {
			Restangular.one("api/ldap/entries",id).get().then(
				function(x) {
					console.log("ldap service: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service: error ",x);
					if(error) error(x);
				}
			);
		}
		
		s.setPassword = function(id,oldPassword,password,success,error) {
			Restangular.one("api/ldap/entries",id).all("password").customPUT({},"",{oldPassword:oldPassword,newPassword:password}).then(
				function(x) {
					console.log("ldap service setpw: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service setpw: error ",x);
					if(error) error(x);
				}
			);
		}
		
		s.checkCreate = function(id,params,success,error) {
			Restangular.one("api/ldap/entries",id).one("create").customPUT({},"",params).then(
				function(x) {
					console.log("ldap service check create: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service check create: error ",x);
					if(error) error(x);
				}
			);
		}

		s.create = function(id,params,success,error) {
			Restangular.one("api/ldap/entries",id).one("create").customPOST({},"",params).then(
				function(x) {
					console.log("ldap service check create: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service check create: error ",x);
					if(error) error(x);
				}
			);
		}

		s.moveTo = function(entryId,newParentId,success,error) {
			console.log("[service] move "+entryId+" to: "+newParentId);
			Restangular.one("api/ldap/entries",entryId).one("move").customPOST({},"",{newParentId:newParentId}).then(
				function(x) {
					console.log("ldap service check create: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service check create: error ",x);
					if(error) error(x);
				}
			);
		}

		s.save = function(id,entry,success,error) {
			Restangular.one("api/ldap/entries",id).customPUT(entry).then(
				function(x) {
					console.log("ldap service save: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service save: error ",x);
					if(error) error(x);
				}
			);
		}
		
		s.enableUser = function(id,enabled,success,error) {
			Restangular.one("api/ldap/entries",id).all("status").customPUT({},"",{enabled:enabled}).then(
				function(x) {
					console.log("ldap service: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service: error ",x);
					if(error) error(x);
				}
			);
		}
		
		
		s.getChildren = function(id,filter,success,error) {
			s.list({parentId:id,permission:"READ", filter},success,error);
		}
		
		s.addMember = function(groupId, principalId,success,error) {
			Restangular.one("api/ldap/entries",groupId).all("members").customPOST({},"",{principalId: principalId}).then(
				function(x) {
					console.log("ldap service addmember: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service addmember: error ",x);
					if(error) error(x);
				}
			);
		}

		s.removeMember = function(groupId, principalId,success,error) {
			Restangular.one("api/ldap/entries",groupId).one("members",principalId).customDELETE().then(
					function(x) {
						console.log("ldap service addmember: success ",x);
						if(success) success(x);
					},
					function(x) {
						console.log("ldap service addmember: error ",x);
						if(error) error(x);
					}
				);
		}
		
		s.getMembers = function(id,success,error) {
			Restangular.one("api/ldap/entries",id).all("members").getList().then(
				function(x) {
					console.log("ldap service: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service: error ",x);
					if(error) error(x);
				}
			);
		}
		
		s.getMemberships = function(id,success,error) {
			Restangular.one("api/ldap/entries",id).all("memberships").getList().then(
				function(x) {
					console.log("ldap service: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service: error ",x);
					if(error) error(x);
				}
			);
		}

		s.getAcls = function(id,success,error) {
			Restangular.one("api/ldap/entries",id).all("acls").getList().then(
				function(x) {
					console.log("ldap service acls: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service acls: error ",x);
					if(error) error(x);
				}
			);
		}

		s.addAcl = function(id,acl,success,error) {
			Restangular.one("api/ldap/entries",id).all("acls").customPOST(acl).then(
				function(x) {
					console.log("ldap service acls: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service acls: error ",x);
					if(error) success(x);
				}
			);
		}

		s.removeAcl = function(id,aclId,success,error) {
			Restangular.one("api/ldap/entries",id).one("acls",aclId).customDELETE().then(
				function(x) {
					console.log("ldap service acls: success ",x);
					if(success) success(x);
				},
				function(x) { 
					console.log("ldap service acls: error ",x);
					if(error) success(x);
				}
			);
		}

		s.getPermissions = function(id,success,error) {
			Restangular.one("api/ldap/entries",id).all("permissions").getList().then(
				function(x) {
					console.log("ldap service: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service: error ",x);
					if(error) success(x);
				}
			);
		}

		s.ignore = function(id,ignore,success,error) {
			var d = {ignore:ignore};
			console.log("ignore: ",id,d);
			Restangular.one("api/ldap/entries",id).all("ignore").customPOST(d,"",d).then(
				function(x) {
					console.log("ldap service: success ",x);
					if(success) success(x);
				},
				function(x) {
					console.log("ldap service: error ",x);
					if(error) error(x);
				}
			);
		}
		
		s.query = function(type,permissions) {
			return function(params,callback) {
				s.list(
					{type: type, permission: permissions, filter: params, offset: 0, max: 16},
					function(results) {
						_.each(results, function(r) {
							r.text = r.hierarchy.join(" | ");
						});
						callback(params,results);	
					}
				);
			}
		};
		
		s.resolve = function() {
			return function(id,callback) {
				s.get(id, 
					function(result) {
						if(result && result.fullPath) {
							result.name = 
							result.text = result.hierarchy.join(" | ");
						}
						callback(result);
					}
				);
			}
		};
		
		
		return s;
		 
	}]
	
);
