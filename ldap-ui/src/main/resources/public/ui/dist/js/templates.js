angular.module("templates").run(["$templateCache", function($templateCache) {$templateCache.put("/select_arrow.html","<span id=\"arrow\" style=\"padding-right: 10\">\n	<span ng-show=\"active\"><i class=\"glyphicon glyphicon-chevron-up\"> </i></span>\n	<span ng-show=\"!active\"><i class=\"glyphicon glyphicon-chevron-down\"> </i></span>\n</span>\n");
$templateCache.put("/select_disabled.html","<div>\n	{{current.text}}\n</div>");
$templateCache.put("/select_empty.html","<div class=\"form-control\" style=\"background: white\" ng-click=\"toggle()\">\n	<i>{{placeholder}}</i>\n</div>\n");
$templateCache.put("/select_inactive.html","<div class=\"input-group\">\n	<div class=\"form-control\" style=\"overflow: hidden\">\n		{{current.name || current.displayname}} \n	</div>\n	<div class=\"input-group-append\">\n		<button class=\"btn btn-secondary\" ng-click=\"deselect()\">\n			<i class=\"fas fa-times\"></i>\n		</button>\n	</div>\n</div>");
$templateCache.put("/select_list.html","<div>\n	<table class=\"table table-compact table-hover mb-0\">\n		<tr ng-repeat=\"r in results\">\n			<td ng-click=\"select(r.id)\">\n				<div>\n					<b>{{r.name || r.displayname}}</b> \n				</div>	\n				<div ng-show=\"r.text\">\n					{{r.text}}\n				</div>\n			</td>\n		</tr>\n		<tr ng-if=\"results.length==0\">\n			<td>\n				<i>no results</i>\n			</td>\n		</tr>\n	</table>\n</div>");
$templateCache.put("/select_overlay.html","<div class=\"shadow border border-light\" style=\"z-index: 900; position: relative; background: white\">\n</div>");
$templateCache.put("/select_query.html","<div class=\"p-3\">\n	<input class=\"form-control\" ng-model=\"search\" ng-change=\"runQuery()\">\n</div>");
$templateCache.put("/select_remove.html","<span id=\"remove\">\n	<span><i class=\"glyphicon glyphicon-remove\"> </i></span>\n</span>\n");
$templateCache.put("/auth/templates/_main.html","		<nav class=\"navbar fixed-top navbar-expand-lg navbar-dark bg-dark fixed-top gradient\">\n			<div class=\"container\">\n				<div class=\"navbar\">\n					<!-- \n					<div class=\"navbar-brand\">\n						<img src=\"img/egg_white.png\" height=\"30px\">\n					</div>\n					<ul class=\"navbar-nav bd-navbar-nav flex-row\">\n						<li menu-item=\"/main\" class=\"nav-item\" style=\"padding-right: 40px\" >\n							<a href=\"#{{item.path}}\" class=\"nav-link\" ng-class=\"{\'active\' : item.active}\">\n								<span ng-if=\"item.icon\" class=\"hidden-xs showopacity {{item.icon}}\"></span>							\n								{{item.title}}\n							</a>\n						</li>\n					</ul>\n					 --> \n				</div>\n				<div class=\"navbar-nav ml-auto\" id=\"navbarProfile\" ng-controller=\"StatusController as status\">\n					<ul class=\"navbar-nav bd-navbar-nav flex-row\" ng-if=\"status.user.id\">\n						<li class=\"nav-item dropdown\">\n							<a class=\"nav-link dropdown-toggle\" id=\"navbarDropdownBlog\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">\n								{{status.user.displayname}}\n								<i class=\"fa fa-user-circle\"> </i>\n							</a>\n				            <div class=\"dropdown-menu dropdown-menu-right\" aria-labelledby=\"navbarDropdownBlog\">\n				              <a class=\"dropdown-item\" href=\"#/welcome\">\n				              	<i class=\"fas fa-home\"></i>\n				              	Dashboard\n				              </a>\n							  <div class=\"dropdown-divider\" ng-if=\"status.user.admin\"></div>\n				              <a class=\"dropdown-item\" href=\"#/admin\" ng-if=\"status.user.admin\">\n				              	<i class=\"fas fa-cogs\"></i>\n				              	Administration\n				              </a>\n							  <div class=\"dropdown-divider\"></div>\n\n				              <a class=\"dropdown-item\" href ng-click=\"$event.preventDefault();status.logout()\">\n				              	<i class=\"fas fa-sign-out-alt\"></i>\n				              	Logout\n				              </a>\n				            </div>\n						</li>\n					</ul>\n				</div>\n			</div>\n		</nav>");
$templateCache.put("/auth/templates/ldap_detail.html","<div class=\"col-md-12\">\n	<h1><a href=\"#/main/ldap\">LDAP</a> | \n	<i class=\"fa fa-user\" ng-if=\"ldap.entry.type == \'USER\'\"></i>\n	<i class=\"fa fa-users\" ng-if=\"ldap.entry.type == \'GROUP\'\"></i>\n	<i class=\"fa fa-folder-open\" ng-if=\"ldap.entry.type == \'UNIT\'\"></i>\n	{{ldap.entry.displayname}} </h1>\n</div>\n<div class=\"col-xs-12 col-sm-12 col-md-8\">\n	<span ng-repeat=\"a in ldap.entry.ancestors\">\n		<i class=\"fas fa-chevron-right\"></i>\n		<span class=\"btn btn-sm btn-link\" style=\"font-size: 1.2em\" ng-click=\"ldap.open(a[1])\">{{a[0]}}\n		</span>\n	</span> 	\n</div>\n<div class=\"col-md-4 hidden-sm  hidden-xs\">\n	<span\n		ng-if=\"ldap.entry.type == \'USER\'\"\n		an-select class=\"form-control\"\n		query=\"ldap.queryUsers\"\n		resolve=\"ldap.resolve\"\n		ng-model=\"ldap.choseOtherId\"\n		placeholder=\"\'Jump to user ... \'\"\n		ng-change=\"ldap.open(ldap.choseOtherId)\">\n	</span>\n	<span\n		ng-if=\"ldap.entry.type == \'GROUP\'\"\n		an-select class=\"form-control\"\n		query=\"ldap.queryGroups\"\n		resolve=\"ldap.resolve\"\n		ng-model=\"ldap.choseOtherId\"\n		placeholder=\"\'Jump to group ... \'\"\n		ng-change=\"ldap.open(ldap.choseOtherId)\">\n	</span>\n</div>\n\n<div class=\"col-md-12\">\n	<div class=\"float-right\">\n		&nbsp;<br>\n		<div class=\"input-group\">\n			<button class=\"form-control btn btn-primary float-right\" ng-if=\"!ldap.edit\" ng-click=\"ldap.toggleEdit()\">\n				<i class=\"fas fa-pencil-alt\" ></i>\n			</button>\n			<button class=\"form-control btn float-right\" ng-class=\"{\'btn-danger\' : ldap.modified, \'btn-success\' : !ldap.modified}\" ng-if=\"ldap.edit\" ng-click=\"ldap.toggleEdit()\">\n				<i class=\"fas fa-save\"></i>\n			</button>\n		</div>\n	</div>\n	<h4>\n		Basics\n	</h4>\n</div>\n<div class=\"col-md-5\"> \n	<div class=\"form-group\">\n		<b>Full DN</b><br>\n		{{ldap.entry.dn}}\n	</div>\n</div>\n<div class=\"col-md-4\">\n	<div class=\"form-group\">\n		<b>ID</b><br>\n		{{ldap.entry.id}}\n	</div>\n</div>\n<div class=\"col-md-3\"  ng-if=\"ldap.entry.type == \'USER\'\"> \n	<div class=\"form-group\">\n		<b>Status</b><br>\n		<button class=\"btn\" ng-class=\"{\'btn-success\' : ldap.edit}\"  ng-disabled=\"!ldap.edit || !ldap.permissions.includes(\'SET_USER_STATE\')\" ng-if=\"ldap.entry.enabled\" ng-click=\"ldap.enableUser(ldap.entry.id,false)\">\n			<i class=\"fas fa-toggle-on\"></i> Enabled\n		</button>\n		<button class=\"btn\" ng-class=\"{\'btn-danger\' : ldap.edit}\" ng-disabled=\"!ldap.edit || !ldap.permissions.includes(\'SET_USER_STATE\')\" ng-if=\"!ldap.entry.enabled\" ng-click=\"ldap.enableUser(ldap.entry.id,true)\">\n			<i class=\"fas fa-toggle-off\"></i> Disabled\n		</button>\n	</div>\n</div>\n\n<div class=\"col-md-12\"> \n	<div class=\"form-group\">\n		<b>Name</b><br>\n		{{ldap.entry.name || \"[no UID]\"}}\n		<i class=\"glyphicon glyphicon-pencil\"></i>\n	</div>\n</div>\n<div class=\"col-md-2\" ng-if=\"ldap.entry.type == \'USER\'\"> \n	<div class=\"form-group\">\n		<b>Givenname</b><br>\n		<span ng-if=\"!ldap.edit\">\n			{{ldap.entry.givenname  || \"[no givenname]\"}}\n		</span>\n		<input ng-if=\"ldap.edit\" ng-model=\"ldap.entry.givenname\" placeholder=\"enter givenname\" class=\"form-control\" ng-change=\"ldap.modify()\">\n	</div>\n</div>\n<div class=\"col-md-3\" ng-if=\"ldap.entry.type == \'USER\'\"> \n	<div class=\"form-group\">\n		<b>Familyname</b><br>\n		<span ng-if=\"!ldap.edit\">\n			{{ldap.entry.familyname  || \"[no familyname]\"}}\n		</span>\n		<input ng-if=\"ldap.edit\" ng-model=\"ldap.entry.familyname\" placeholder=\"enter familyname\" class=\"form-control\" ng-change=\"ldap.modify()\">\n	</div>\n</div>\n<div class=\"col-md-4\" ng-if=\"ldap.entry.type == \'USER\'\"> \n	<div class=\"form-group\">\n		<b>Display Name</b><br>\n		<span ng-if=\"!ldap.edit\">\n			{{ldap.entry.displayname  || \"[no display name]\"}}\n		</span>\n		<input ng-if=\"ldap.edit\" ng-model=\"ldap.entry.displayname\" placeholder=\"enter display name\" class=\"form-control\" ng-change=\"ldap.modify()\">\n	</div>\n</div>\n<div class=\"col-md-3\"  ng-if=\"ldap.entry.type == \'USER\'\"> \n	<div class=\"form-group\">\n		<b>E-Mail</b><br>\n		<span ng-if=\"!ldap.edit\">\n			{{ldap.entry.email || \"[no email]\"}}\n		</span>\n		<input ng-if=\"ldap.edit\" ng-model=\"ldap.entry.email\" placeholder=\"enter email\" class=\"form-control\" ng-change=\"ldap.modify()\">\n	</div>\n</div>\n<div class=\"col-md-12\"> \n	<div class=\"form-group\">\n		<b>Description</b><br>\n		<span ng-if=\"!ldap.edit\">\n			{{ldap.entry.description || \"[no description]\" }}\n		</span>\n		<textarea ng-if=\"ldap.edit\" ng-model=\"ldap.entry.description\" class=\"form-control\" rows=\"3\" cols=\"40\" ng-change=\"ldap.modify()\"></textarea>\n	</div>\n</div>\n<div class=\"col-md-12\" ng-if=\"ldap.entry.type == \'USER\' && (ldap.permissions.includes(\'PW_RESET\') || ldap.self)\">\n	<h4>Set Password</h4>\n	<div ng-click=\"ldap.setpw.show = true\" ng-if=\"!ldap.setpw.show\">\n		<i>Click to show</i>\n	</div>\n	<div class=\"row\" ng-if=\"ldap.setpw.show\">\n		<div class=\"col-md-3\" ng-if=\"ldap.self\">\n			<div class=\"form-group\">\n				<input type=\"password\" ng-model=\"ldap.setpw.oldPw\" class=\"form-control\" placeholder=\"Enter current password\" >\n			</div>\n		</div>\n		<div  ng-class=\"{\'col-3\' : ldap.self, \'col-4\' : !ldap.self}\">\n			<div class=\"form-group\">\n				<div class=\"input-group\">\n					<input ng-if=\"!ldap.setpw.visiblePw\" type=\"password\" ng-model=\"ldap.setpw.pw1\" class=\"form-control\" placeholder=\"Enter password (8 chars min)\" ng-change=\"ldap.checkPassword(ldap.setpw)\">\n					<input ng-if=\"ldap.setpw.visiblePw\" ng-model=\"ldap.setpw.pw1\" class=\"form-control\" placeholder=\"Enter password (8 chars min)\" ng-change=\"ldap.checkPassword(ldap.setpw)\">\n				</div>\n			</div>\n			<div class=\"form-group\" ng-if=\"ldap.setpw.pw1.length > 0\">\n				<div class=\"progress\" style=\"height: 3px;\">\n				  <div class=\"progress-bar bg-success\" role=\"progressbar\" ng-style=\"ldap.setpw.progressWidth\" aria-valuenow=\"0\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>\n				</div>\n			</div>\n		</div>\n		<div  ng-class=\"{\'col-3\' : ldap.self, \'col-4\' : !ldap.self}\">\n			<div class=\"form-group\">\n				<input ng-if=\"!ldap.setpw.visiblePw\" type=\"password\" ng-disabled=\"ldap.setpw.progress < 100\" ng-model=\"ldap.setpw.pw2\" class=\"form-control\" placeholder=\"confirm password\" ng-change=\"ldap.checkPassword(ldap.setpw)\">\n				<input ng-if=\"ldap.setpw.visiblePw\" ng-disabled=\"ldap.setpw.progress < 100\" ng-model=\"ldap.setpw.pw2\" class=\"form-control\" placeholder=\"confirm password\" ng-change=\"ldap.checkPassword(ldap.setpw)\">\n			</div>\n		</div>\n		<div ng-class=\"{\'col-3\' : ldap.self, \'col-4\' : !ldap.self}\">\n			<div class=\"form-group float-right\">\n				<div class=\"input-group\">\n					<button class=\"btn btn-dark\" ng-click=\"ldap.generatePassword(ldap.setpw)\">\n						<i class=\"fas fa-dice\"></i>\n					</button>\n					<button class=\"ml-2 btn btn-dark\" ng-click=\"ldap.toggleShowPassword(ldap.setpw)\">\n						<i class=\"fas fa-eye\"></i>\n					</button>\n					<button class=\"ml-2 btn\" \n						ng-class=\"{\'btn-primary\' : ldap.setpw.state>-1,\'btn-success\' : ldap.setpw.state==-2,\'btn-danger\' : ldap.setpw.state==-3}\"\n						ng-disabled=\"!ldap.setpw.ok\" ng-click=\"ldap.setPassword(ldap.setpw)\">\n						Reset\n					</button>\n				</div>\n			</div>\n		</div>\n	</div>\n</div> \n<div class=\"col-md-12\" ng-if=\"ldap.entry.type == \'GROUP\'\"> \n	<h4>Members</h4>\n	<table class=\"table table-compact\">\n		<tr ng-if=\"ldap.members.length==0\" colspan=\"3\">\n			<td><i>No members</i></td>\n		</tr>\n		<tr ng-repeat=\"entry in ldap.members\" ng-mouseover=\"entry.active = true\" ng-mouseout=\"entry.active = false\" ng-class=\"{\'table-active\' : entry.active}\">\n			<td width=\"5%\">\n				<i class=\"fa fa-user\" ng-if=\"entry.type == \'USER\'\"></i>\n				<i class=\"fa fa-users\" ng-if=\"entry.type == \'GROUP\'\"></i>\n				<i class=\"fa fa-folder-open\" ng-if=\"entry.type == \'UNIT\'\"></i>\n			</td>\n			<td width=\"80%\">\n				<b>{{entry.displayname}}</b><span ng-if=\"entry.type == \'USER\'\"> ({{entry.name}})</span><br>\n				<small>{{entry.hierarchy | join:\' | \'}}</small>\n			</td>\n			<td width=\"10%\">\n				<button class=\"btn btn-sm btn-danger float-right\" ng-click=\"ldap.removeMember(ldap.entry.id,entry.id)\">\n					<i class=\"fas fa-minus\"></i>\n				</button>\n			</td>\n		</tr>\n		<tr ng-if=\"ldap.permissions && ldap.permissions.includes(\'EDIT_MEMBERS\')\">\n			<td colspan=\"3\">\n				<b ng-click=\"ldap.addMemberExpanded = !ldap.addMemberExpanded\">\n					<i class=\"fas fa-plus-square\" ng-if=\"!ldap.addMemberExpanded\"></i> \n					<i class=\"fas fa-minus-square\" ng-if=\"ldap.addMemberExpanded\"></i> \n					Add Member\n				</b>\n			</td>\n		</tr>\n		<tr ng-if=\"ldap.addMemberExpanded\">\n			<td colspan=\"2\">\n				<div\n					an-select class=\"form-control\"\n					query=\"ldap.queryUsers\"\n					resolve=\"ldap.resolve\"\n					placeholder=\"\'Select user to add ... \'\"\n					ng-model=\"ldap.membersAddId\">\n				</div>\n			</td>\n			<td>\n				<button class=\"btn btn-primary float-right btn-sm\" ng-disabled=\"ldap.mebersAddId.length < 6\" ng-click=\"ldap.addMember(ldap.entry.id,ldap.membersAddId)\">\n					<i class=\"fas fa-plus\"></i>\n				</button>\n			</td>\n		</tr>\n	</table>\n</div>\n<div class=\"col-md-12\" ng-if=\"ldap.entry.type == \'USER\'\"> \n	<h4>Memberships</h4>\n	<table class=\"table table-compact\">\n		<tr ng-if=\"ldap.memberships.length==0\" colspan=\"3\">\n			<td><i>No memberships</i></td>\n		</tr>\n		<tr ng-repeat=\"entry in ldap.memberships\" ng-mouseover=\"entry.active = true\" ng-mouseout=\"entry.active = false\" ng-class=\"{\'table-active\' : entry.active}\">\n			<td width=\"5%\">\n				<i class=\"fa fa-user\" ng-if=\"entry.type == \'USER\'\"></i>\n				<i class=\"fa fa-users\" ng-if=\"entry.type == \'GROUP\'\"></i>\n				<i class=\"fa fa-folder-open\" ng-if=\"entry.type == \'UNIT\'\"></i>\n			</td>\n			<td width=\"80%\">\n				<b>{{entry.displayname}}</b><span ng-if=\"entry.type == \'USER\'\"> ({{entry.name}})</span><br>\n				<small>{{entry.hierarchy | join:\' | \'}}</small>\n			</td>\n			<td width=\"10%\">\n				<button class=\"btn btn-sm btn-danger float-right\" ng-click=\"ldap.removeMember(entry.id, ldap.entry.id)\">\n					<i class=\"fas fa-minus\"></i>\n				</button>\n			</td>\n		</tr>\n		<tr>\n			<td colspan=\"3\">\n				<b ng-click=\"ldap.addMembershipExpanded = !ldap.addMembershipExpanded\">\n					<i class=\"fas fa-plus-square\" ng-if=\"!ldap.addMembershipExpanded\"></i> \n					<i class=\"fas fa-minus-square\" ng-if=\"ldap.addMembershipExpanded\"></i> \n					Add to Group\n				</b>\n			</td>\n		</tr>\n		<tr ng-if=\"ldap.addMembershipExpanded\">\n			<td colspan=\"2\">\n				<div\n					an-select\n					query=\"ldap.queryGroups\"\n					resolve=\"ldap.resolve\"\n					placeholder=\"\'Select group to add ... \'\"\n					ng-model=\"ldap.mebershipsAddId\">\n				</div>\n			</td>\n			<td>\n				<button class=\"btn btn-primary btn-sm float-right\" ng-disabled=\"ldap.mebershipsAddId.length < 6\" ng-click=\"ldap.addMember(ldap.mebershipsAddId,ldap.entry.id)\">\n					<i class=\"fas fa-plus\"></i> \n 				</button>\n			</td>\n		</tr>\n	</table>\n</div>\n<div class=\"col-md-12\" ng-if=\"ldap.entry.type == \'UNIT\'\"> \n	<h4>Child Entries</h4>\n	<input class=\"form-control\" ng-model=\"ldap.childFilter\" ng-change=\"ldap.updateChildren()\" placeholder=\"type to filter\" ng-if=\"ldap.children.length > 5 || ldap.childFilter.length > 0\">\n	<table class=\"table table-compact\">\n		<tr ng-if=\"ldap.children==0\">\n			<td  colspan=\"3\"><i>No child entries</i></td>\n		</tr>\n		<tr ng-repeat=\"entry in ldap.children\" ng-click=\"ldap.open(entry.id)\" ng-mouseover=\"entry.active = true\" ng-mouseout=\"entry.active = false\" ng-class=\"{\'table-active\' : entry.active}\">\n			<td width=\"5%\">\n				<i class=\"fa fa-user\" ng-if=\"entry.type == \'USER\'\"></i>\n				<i class=\"fa fa-users\" ng-if=\"entry.type == \'GROUP\'\"></i>\n				<i class=\"fa fa-folder-open\" ng-if=\"entry.type == \'UNIT\'\"></i>\n			</td>\n			<td width=\"90%\">\n				<b>{{entry.displayname}}</b><span ng-if=\"entry.type == \'USER\'\"> ({{entry.name}})</span><br>\n				<small>{{entry.hierarchy | join:\' | \'}}</small>\n			</td>\n			<td width=\"5%\">\n				<span class=\"btn btn-sm btn-link\">\n					<i class=\"fas fa-chevron-circle-right\"></i>\n				</span>\n			</td>\n		</tr>\n		<tr ng-if=\"ldap.permissions && ldap.permissions.includes(\'CREATE\')\">\n			<td colspan=\"3\">\n				<b ng-click=\"ldap.addChildExpanded = !ldap.addChildExpanded \">\n					<i class=\"fas fa-plus-square\" ng-if=\"!ldap.addChildExpanded\"></i> \n					<i class=\"fas fa-minus-square\" ng-if=\"ldap.addChildExpanded\"></i> \n					Create child entry\n				</b>\n			</td>\n		</tr>\n		<tr ng-if=\"ldap.addChildExpanded\">\n			<td colspan=\"3\">\n				<div class=\"row\">\n					<div class=\"col-4\">\n						<div class=\"form-group\">\n							<label>Type</label>\n							<select ng-model=\"ldap.child.type\" class=\"form-control\" placeholder=\"please select\" ng-change=\"ldap.checkCreate(ldap.child)\">\n								<option value=\"USER\">User</option>\n								<option value=\"GROUP\">Group</option>\n								<option value=\"UNIT\">Unit</option>\n							</select>\n						</div>\n					</div>\n					<div class=\"col-8\">\n						<div class=\"form-group\">\n							<label>Name</label>\n							<input ng-model=\"ldap.child.name\" class=\"form-control\" ng-change=\"ldap.checkCreate(ldap.child)\" ng-disabled=\"ldap.child.type == \'\'\">\n						</div>\n					</div>\n				</div>\n				<div class=\"row\" ng-if=\"ldap.child.type == \'USER\'\">\n					<div class=\"col-4\">\n						&nbsp;\n					</div>\n					<div class=\"col-4\">\n						<div class=\"form-group\">\n							<label>Givenname</label>\n							<input ng-model=\"ldap.child.givenname\" class=\"form-control\" ng-change=\"ldap.updateDisplayName(ldap.child); ldap.checkCreate(ldap.child)\">\n						</div>\n					</div>\n					<div class=\"col-4\">\n						<div class=\"form-group\">\n							<label>Familyname</label>\n							<input ng-model=\"ldap.child.familyname\" class=\"form-control\" ng-change=\"ldap.updateDisplayName(ldap.child); ldap.checkCreate(ldap.child)\">\n						</div>\n					</div>\n				</div>\n				<div class=\"row\" ng-if=\"ldap.child.type == \'USER\'\">\n					<div class=\"col-4\">\n						<div class=\"form-group\">\n							<label>Email</label>\n							<input ng-model=\"ldap.child.email\" class=\"form-control\" ng-change=\"ldap.checkCreate(ldap.child)\">\n						</div>\n					</div>\n					<div class=\"col-8\">\n						<div class=\"form-group\">\n							<label>Display Name</label>\n							<input ng-model=\"ldap.child.displayname\" class=\"form-control\" ng-change=\"ldap.checkCreate(ldap.child)\">\n						</div>\n					</div>\n				</div>\n				<div class=\"row\" ng-if=\"ldap.child.type == \'USER\'\">\n					<div class=\"col-4\">\n						<div class=\"form-group\">\n							<input type=\"checkbox\" class=\"checkbox\" ng-model=\"ldap.child.setPassword\" ng-change=\"ldap.checkCreate(ldap.child)\">  <label>Set Password</label>\n						</div>\n					</div>\n					<div class=\"col-4\">\n						<div class=\"form-group\">\n							<input type=\"password\" class=\"form-control\" placeholder=\"please enter a password\" ng-if=\"ldap.child.setPassword\" ng-model=\"ldap.child.pass1\" ng-change=\"ldap.checkCreate(ldap.child)\">\n						</div>\n					</div>\n					<div class=\"col-4\">\n						<div class=\"form-group\">\n							<input type=\"password\" class=\"form-control\" placeholder=\"please confirm password\" ng-if=\"ldap.child.setPassword\" ng-model=\"ldap.child.pass2\" ng-change=\"ldap.checkCreate(ldap.child)\">\n						</div>\n					</div>\n				</div>\n				<div class=\"row\" ng-if=\"ldap.createComplaints.length > 0\">\n					<div class=\"col-12\">\n						<div ng-repeat=\"complaint in ldap.createComplaints\" class=\"alert alert-danger\">{{complaint.code}}</div>\n					</div>\n				</div>\n				<div class=\"row\" ng-if=\"ldap.createComplaints.length == 0\">\n					<div class=\"col-12\">\n						<div class=\"form-group\">\n							<button class=\"btn btn-primary float-right\" ng-click=\"ldap.createChild(ldap.child)\">\n								<i class=\"fas fa-plus-circle\"></i> Create \n							</button>\n						</div>\n					</div>\n				</div>\n			</td>\n		</tr>\n	</table>\n\n</div>\n<div class=\"col-md-12\" ng-if=\"ldap.acls\">\n	<h4>\n		ACLs\n	</h4>\n	<table class=\"table table-compact\">\n		<tr ng-if=\"ldap.acls.length==0\">\n			<td  colspan=\"3\"><i>No ACLs</i></td>\n		</tr>\n		<tr ng-repeat=\"acl in ldap.acls\" ng-mouseover=\"acl.active = true\" ng-mouseout=\"acl.active = false\" ng-class=\"{\'table-active\' : acl.active}\">\n			<td width=\"60%\">\n				<b>{{acl.principal.displayname}}</b><br>\n				<small>{{acl.principal.hierarchy | join:\' | \'}}</small>\n			</td>\n			<td width=\"20%\">\n				<b>{{acl.permission}}</b>\n			</td>\n			<td width=\"10%\">\n				<button class=\"btn btn-sm btn-danger float-right\" ng-click=\"ldap.removeAcl(ldap.entry.id,acl.id)\" ng-if=\"acl.entryId == ldap.entry.id\">\n					<i class=\"fas fa-minus\"></i>\n				</button>\n				<button class=\"btn btn-sm btn-secondary float-right\" ng-click=\"ldap.open(acl.entryId)\" ng-if=\"acl.entryId != ldap.entry.id\">\n					<i class=\"fas fa-angle-double-up\"></i>\n				</button>\n			</td>\n		</tr>\n		<tr ng-if=\"ldap.permissions.includes(\'ACL_WRITE\')\">\n			<td colspan=\"3\">\n				<b ng-click=\"ldap.addAclExpanded = !ldap.addAclExpanded\">\n					<i class=\"fas fa-plus-square\" ng-if=\"!ldap.addAclExpanded\"></i> \n					<i class=\"fas fa-minus-square\" ng-if=\"ldap.addAclExpanded\"></i> \n					Add ACL\n				</b>\n			</td>\n		</tr>\n		<tr ng-if=\"ldap.addAclExpanded\">\n			<td width=\"60%\">\n				<input type=\"hidden\" ng-model=\"ldap.acl.recursive\" value=\"true\">\n				<div\n					an-select \n					query=\"ldap.queryPrincipals\"\n					resolve=\"ldap.resolve\"\n					placeholder=\"\'Select who gets access ... \'\"\n					ng-model=\"ldap.acl.principalId\">\n				</div>\n			</td>\n			<td  width=\"30%\">\n				<select ng-model=\"ldap.acl.permission\" class=\"form-control\" placeholder=\"What access\">\n					<option value=\"READ\">READ\n					<option value=\"WRITE\">WRITE\n					<option value=\"CREATE\">CREATE \n					<option value=\"EDIT_MEMBERS\">EDIT_MEMBERS\n					<option value=\"ADMIN\">ADMIN				\n				</select>\n			</td>\n			<td  width=\"10%\">\n				<button class=\"btn btn-primary btn-sm float-right\" ng-disabled=\"ldap.mebershipsAddId.length < 6\" ng-click=\"ldap.addAcl(ldap.entry.id,ldap.acl)\">\n					<i class=\"fas fa-plus\"></i> \n				</button>\n			</td>\n		</tr>\n	</table>\n</div>\n\n<div class=\"col-md-12\" ng-if=\"!ldap.entry.system && ldap.permissions.includes(\'ADMIN\')\">\n	<h4>\n		Danger Zone\n	</h4>\n	<div class=\"input-group\">\n		<input class=\"form-control\" placeholder=\"Enter object name to unlock\" ng-disabled=\"ldap.dangerZoneUnlocked\" ng-model=\"ldap.nameToUnlock\">\n		<div class=\"input-group-append\">\n			<button class=\"btn btn-primary\">\n				<i class=\"fas fa-plus-square\" ng-if=\"ldap.nameToUnlock == ldap.entry.displayname && !ldap.dangerZoneUnlocked\" ng-click=\"ldap.dangerZoneUnlocked = true\"></i> \n				<i class=\"fas fa-minus-square\" ng-if=\"ldap.nameToUnlock == ldap.entry.displayname && ldap.dangerZoneUnlocked\" ng-click=\"ldap.dangerZoneUnlocked = false; ldap.nameToUnlock = \'\';\"></i> \n				<i class=\"fas fa-lock\" ng-if=\"ldap.nameToUnlock != ldap.entry.displayname\"></i>  \n			</button>\n		</div>\n	 </div>\n</div>\n<div class=\"col-md-12\" ng-if=\"ldap.dangerZoneUnlocked\">\n	RENAME <br>\n	DELETE <br>\n	... and other dangerous operations are hidden here.\n</div>\n<div class=\"col-md-12\">\n	<!--  dome padding -->\n	<br>\n	<br>\n	<br>\n	<br>\n	<br>\n	<br>\n</div>");
$templateCache.put("/auth/templates/ldap_list.html","<div class=\"col-md-12\">\n	<h1><a href=\"#/main/ldap\">LDAP</a>\n</div>\n<div class=\"col-md-12\"> \n	<div class=\"form-group\">\n		<form>\n			<div class=\"input-group\">\n				<input class=\"form-control\" id=\"filter\" ng-model=\"ldap.search.filter\" ng-submit=\"ldap.openFirst()\" ng-change=\"ldap.update()\" placeholder=\"Type to filter ... \">\n				<div class=\"input-group-append\">\n					<button class=\"btn\" ng-disabled=\"ldap.search.filter.length <1\"\n							ng-class=\"{\'btn-primary\' : ldap.search.units, \'btn-secondary\' : !ldap.search.units}\" \n							ng-click=\"ldap.search.units=!ldap.search.units;ldap.updateInternal()\">\n						&nbsp;<i class=\"fas fa-folder-open\"></i>&nbsp;\n					</button>\n					<button class=\"btn\" ng-disabled=\"ldap.search.filter.length <1\"\n							ng-class=\"{\'btn-primary\' : ldap.search.groups, \'btn-secondary\' : !ldap.search.groups}\" \n							ng-click=\"ldap.search.groups=!ldap.search.groups;ldap.updateInternal()\">\n						&nbsp;<i class=\"fas fa-users\"></i>&nbsp;\n					</button>\n					<button class=\"btn\" ng-disabled=\"ldap.search.filter.length <1\"\n						ng-class=\"{\'btn-primary\' : ldap.search.users, \'btn-secondary\' : !ldap.search.users}\" \n						ng-click=\"ldap.search.users=!ldap.search.users;ldap.updateInternal()\">\n						&nbsp;<i class=\"fas fa-user\"></i>&nbsp;\n					</button>\n				</div>						\n			</div>\n		</form>\n	</div>\n</div>\n<div class=\"col-md-12\" ng-if=\"ldap.search.filter.length == 0\">\n	<div class=\"form-group\">\n		<div ldap-tree id=\"ROOT\">\n		</div>\n	</div>\n</div>\n<div class=\"col-md-12\" ng-if=\"ldap.search.filter.length > 0\">\n	<div class=\"form-group\">\n		<table class=\"table table-compact\">\n			<tr ng-if=\"!ldap.entries\">\n				<td colspan=\"2\">\n					<i>please wait ... </i>\n				</td>\n			</tr>\n			<tr ng-if=\"ldap.entries && ldap.entries.length == 0\">\n				<td colspan=\"3\" class=\"alert alert-info\">\n					<i>no matching object found ... </i>\n				</td>\n			</tr>\n			<tr ng-repeat=\"entry in ldap.entries\" ng-click=\"ldap.open(entry.id)\" ng-mouseover=\"entry.active = true\" ng-mouseout=\"entry.active = false\" ng-class=\"{\'table-active\' : entry.active}\">\n				<td width=\"5%\">\n					<i class=\"fa fa-user\" ng-if=\"entry.type == \'USER\'\"></i>\n					<i class=\"fa fa-users\" ng-if=\"entry.type == \'GROUP\'\"></i>\n					<i class=\"fa fa-folder-open\" ng-if=\"entry.type == \'UNIT\'\"></i>\n				</td>\n				<td width=\"95%\">\n					<b>{{entry.displayname}}</b><span ng-if=\"entry.type == \'USER\'\"> ({{entry.name}})</span><br>\n					<small>{{entry.hierarchy | join:\' | \'}}</small>\n				</td>\n			</tr>\n		</table>\n	</div>\n</div>");
$templateCache.put("/auth/templates/ldap_tree.html","<div>\n	<div>\n		<div ng-if=\"entries.length == 0\">\n			<div style=\"padding: 5px\">\n				<i class=\"fas fa-times\"></i>\n				<i>No children found</i>\n			</div>\n		</div>\n		<div ng-repeat=\"entry in entries\">\n			<div style=\"padding: 5px\">\n				<span ng-if=\"entry.type == \'UNIT\'\" ng-click=\"entry.expanded=!entry.expanded\">\n					<i class=\"fas fa-plus-square\" ng-if=\"!entry.expanded\"></i> \n					<i class=\"fas fa-minus-square\" ng-if=\"entry.expanded\"></i> \n				</span>\n				<b ng-click=\"open(entry.id)\" ng-if=\"entry.type==\'USER\'\">\n					<i class=\"fas fa-user\"></i> \n					{{entry.displayname}}\n				</b>\n				<b ng-click=\"open(entry.id)\" ng-if=\"entry.type==\'GROUP\'\">\n					<i class=\"fas fa-users\"></i> \n					{{entry.displayname}}\n				</b>\n				<b ng-click=\"entry.expanded=!entry.expanded\" ng-if=\"entry.type==\'UNIT\'\">\n					<i class=\"fas fa-folder-open\" ></i> \n					{{entry.displayname}}\n				</b>\n				<span ng-if=\"admin\" ng-click=\"ignore(entry)\">\n					<i class=\"fas fa-eye\" ng-if=\"!entry.ignored\"></i>\n					<i class=\"fas fa-eye-slash\" ng-if=\"entry.ignored\"></i>\n				</span>\n				<a href=\"#/main/ldap/{{entry.id}}\">\n					<i class=\"fas fa-chevron-right\"></i>\n				</a>\n			</div>\n			<div style=\"padding-left: 15px;\" ng-if=\"entry.expanded\">\n				<div ldap-tree id=\"{{entry.id}}\">\n				</div>\n			</div>\n		</div>	\n	</div>\n</div>");
$templateCache.put("/auth/templates/login.html","\n<div>\n	<div id=\"login-bg\">\n		<img ng-src=\"doors/{{login.door.file}}\" id=\"login-hero\">\n	</div>\n	<div id=\"login-overlay\">\n	</div>\n	<div id=\"login-info-bottom\">\n		<b>{{login.door.author}}</b> <i>{{login.door.title}}</i> \n		<a href=\"{{login.door.link}}\" ng-if=\"login.door.link\">\n			<i class=\"fas fa-external-link-square-alt\"></i> \n		</a>\n	</div>\n	<div id=\"login-row\">\n		<div id=\"login-box\">\n			<div class=\"row\">\n				<div class=\"col-md-12\">\n					<div class=\"form-group\">\n						<B style=\"font-size: 1.5em\">Login</b></h4>\n					</div>\n				</div>\n				<div class=\"col-md-12\">\n					<div class=\"form-group\">\n						<input id=\"username\" class=\"form-control\" autofocus=\"true\" ng-model=\"login.username\" placeholder=\"Username\" ng-change=\"login.check()\" ng-submit=\"login.login()\" ng-init=\"setFocus()\">\n					</div>\n					<div class=\"form-group\">\n						<input class=\"form-control\" type=\"password\"  placeholder=\"Password\" ng-model=\"login.password\" ng-change=\"login.check()\" ng-submit=\"login.login()\">\n					</div>\n				</div>\n				<div class=\"col-md-12\">\n					<div class=\"form-group clearfix\">\n						<div>\n							<div class=\"alert alert-info\" ng-if=\"login.status == -1\">Please wait ... </div>\n							<div class=\"alert alert-danger\" ng-if=\"login.status == -2\">Login failed!</div>\n							<div class=\"alert alert-success\" ng-if=\"login.status == -3\">Login succeeded!</div>\n						</div>\n						<div class=\"float-right\">\n							<a href=\"#/reset\" class=\"btn btn-link\">\n								Forgot Password\n							</a>\n							<button class=\"btn btn-primary\" ng-disabled=\"login.status != 0\" ng-click=\"login.login()\">\n								Login\n								<i class=\"fas fa-sign-in-alt\"></i>\n							</button>\n						</div>\n					</div>\n				</div>\n			</div>\n		</div>\n	</div>\n</div>");
$templateCache.put("/auth/templates/reset.html","\n<div>\n	<div id=\"login-bg\">\n		<img ng-src=\"doors/{{reset.door.file}}\" id=\"login-hero\">\n	</div>\n	<div id=\"login-overlay\">\n	</div>\n	<div id=\"login-info-bottom\">\n		<b>{{reset.door.author}}</b> <i>{{reset.door.title}}</i> \n		<a href=\"{{reset.door.link}}\" ng-if=\"reset.door.link\">\n			<i class=\"fas fa-external-link-square-alt\"></i> \n		</a>\n	</div>\n	<div id=\"login-row\">\n		<div id=\"login-box\">\n			<div class=\"row\" ng-if=\"!reset.user.id\">\n				<div class=\"col-md-12\">\n					<div class=\"form-group\">\n						<B style=\"font-size: 1.5em\">Reset Password</b></h4>\n					</div>\n				</div>				\n				<div class=\"col-md-12\">\n					<div class=\"form-group\">\n						Please enter your username. If we find a matching account, we will send you a temporary token.\n					</div>\n				</div>\n				<div class=\"col-md-12\">\n					<div class=\"form-group\">\n						<div class=\"input-group\">\n							<input class=\"form-control\" placeholder=\"Enter username\" ng-model=\"reset.username\" ng-disabled=\"reset.initiateStep != 1\">\n							<div class=\"input-group-append\">\n								<button class=\"btn btn-primary\" ng-click=\"reset.initiateReset()\" ng-disabled=\"reset.initiateStep != 1\">\n									Get token\n									<i class=\"fas fa-paper-plane\"></i>\n								</button>\n							</div>\n						</div>\n					</div>\n				</div>\n				<div class=\"col-md-12\">\n					<div class=\"form-group\">\n						<input class=\"form-control\" placeholder=\"Enter token\" ng-model=\"reset.token\" ng-disabled=\"reset.initiateStep != 2\">\n					</div>\n				</div>\n				<div class=\"col-md-12\">\n					<div class=\"form-group clearfix\">\n						<div>\n							<div class=\"alert alert-info\" ng-if=\"reset.initiateStep == -1\">Please wait ... </div>\n							<div class=\"alert alert-danger\" ng-if=\"reset.initiateStep == -2\">Reset failed!</div>\n						</div>\n						<div class=\"float-right\">\n							<a href=\"#/reset\" class=\"btn btn-link\" ng-click=\"reset.cancel()\">\n								Cancel\n							</a>\n							<button \n								class=\"btn\" \n								ng-class=\"{\'btn-primary\' : reset.initiateStep == 2 }\" \n								ng-class=\"{\'btn-danger\' : reset.initiateStep == -2 }\" \n								ng-click=\"reset.completeReset()\" ng-disabled=\"reset.initiateStep != 2\">\n								Confirm\n								<i class=\"fas fa-envelope-open-text\"></i>\n							</button>\n						</div>\n					</div>\n				</div>\n			</div>\n			<div class=\"row\" ng-if=\"reset.user.id\">\n				<div class=\"col-md-12\">\n					<div class=\"col-md-12\">\n						<div class=\"form-group\">\n							<B style=\"font-size: 1.5em\">Update Password</b></h4>\n						</div>\n					</div>				\n				</div>				\n				<div class=\"col-md-12\">\n					<div class=\"form-group\">\n						Your password needs to be updated. Please enter a new password below, then enter it again to confirm.\n					</div>\n				</div>\n				<div class=\"col-md-12\">\n					<div class=\"form-group\">\n						<input class=\"form-control\" type=\"password\"  placeholder=\"Password\" ng-model=\"reset.pw.pw1\" ng-change=\"reset.check(reset.pw)\">\n						<div class=\"progress\" style=\"height: 3px;\">\n						  <div class=\"progress-bar bg-success\" role=\"progressbar\" ng-style=\"reset.pw.progressWidth\" aria-valuenow=\"0\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>\n						</div>\n					</div>\n					<div class=\"form-group\">\n						<input class=\"form-control\" type=\"password\" ng-disabled=\"reset.pw.progress < 100\" placeholder=\"Repeat\" ng-model=\"reset.pw.pw2\" ng-change=\"reset.check(reset.pw)\">\n					</div>\n					<div class=\"form-group clearfix\">\n						<div>\n							<div class=\"alert alert-info\" ng-if=\"reset.step == -1\">Please wait ... </div>\n							<div class=\"alert alert-danger\" ng-if=\"reset.step == -2\">Reset failed!</div>\n						</div>\n						<div class=\"float-right\">\n							<a href=\"#/reset\" class=\"btn btn-link\" ng-click=\"reset.cancel()\">\n								Cancel\n							</a>\n							<button class=\"btn btn-primary\" ng-disabled=\"!reset.pw.ok\" ng-click=\"reset.updatePassword()\">\n								Update\n								<i class=\"fas fa-sign-in-alt\"></i>\n							</button>\n						</div>\n					</div>\n				</div>\n			</div>\n		</div>\n	</div>\n</div>");
$templateCache.put("/auth/templates/welcome.html","<h1>Welcome</h1> ");}]);