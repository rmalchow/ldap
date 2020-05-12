a few helper classes to make UIs more pluggable.

the first relevant component is the PluginMenuService, which 
has the method that is probably the most interesting one:

	PluginMenuService.addItem(PATH,NAME,ITEM);
	
for example:

	PluginMenuService.addItem(
		"/main",
		"/users",
		{
			"title" : "Users",
			"id" : "USERS", // default is same as title
			"controller", "UserListController",
			"controllerAs", "ctrl",
			"templateUrl", "userlist.html",
			// special configs:
			"visible" : true,   // this is used by the directives to automatically show/hide items
			"active" : true,   // this is true if $route.beginsWith($PATH+$NAME)
			"order" : 1,   // children are sorted by this
		}
	);
	
you can add anything you want and then use it in your rendering. two directives can help with 
rendering:

	<div include-components="/main"> 
	</div>
	
will iterate all components directly under "/main" (not recursively) and create a copy of the element 
the attribute is on for each item found. these are also automatically hidden and shown according to the 
value of the visible key.

	<ul>	 
		<li menu-items="/main">
			<a href="#{{item.path}}">{{item.title}}</a>
			<ul>
				<li menu-items="{{item.path}}">
					<a href="#{{item.path}}">{{item.title}}</a>
				</li>
			</ul>	
		</li>
	</ul>	
			
			
this is similar to include components, except that it only iterates and puts the current item on the scope. 
it has to be rendered manually. this example would do two levels.

	
	