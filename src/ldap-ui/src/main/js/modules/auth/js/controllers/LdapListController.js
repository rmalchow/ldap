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
