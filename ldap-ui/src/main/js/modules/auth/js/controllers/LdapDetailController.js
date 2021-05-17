angular.module("rooster").controller(
	"LdapDetailController", 
	[ '$timeout', '$rootScope', '$location', '$routeParams', 'LdapService', 'AuthenticationService',  function($timeout,$rootScope,$location,$routeParams, LdapService, AuthenticationService) {

		var ldap = this;

		ldap.deleteSuccess = 0;

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
		
		ldap.delete = function() {
			console.log("deleting: "+ldap.id);
			id = ldap.entry.parentId;
			ldap.deleteSuccess = -1;
			LdapService.delete(ldap.id, function() {
				ldap.deleteSuccess = 1;
				$timeout(function() {
					ldap.open(id);
				}, 3000);
			}, function() {
				ldap.deleteSuccess = 2;
				$timeout(function() {
					ldap.deleteSuccess = 0;
				},3000);
			});
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
