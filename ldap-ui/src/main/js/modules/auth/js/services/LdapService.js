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
