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