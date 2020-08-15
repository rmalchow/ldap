
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
