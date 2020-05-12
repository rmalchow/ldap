angular.module("angular-plugin",["ngRoute"]);
angular.module("angular-plugin")
.config(function($routeProvider,$locationProvider) {
		$locationProvider.hashPrefix('');
		// rember the route provider so we can retrieve it later ... 
		angular.module("angular-plugin").routeProvider = $routeProvider;
});

angular.module("angular-plugin").directive(
	"breadCrumb",
	function(PluginMenuService,$location,$templateCache,$controller,$compile) {
		return {
			scope: {
				skip : "@",
				chop : "@"
			},
			link: function(scope, el, attr, ctrl, transclude) {
				console.log($location.path()+" : "+scope.skip);
				
				var items = PluginMenuService.getAncestors($location.path(),scope.skip,scope.chop);
				console.log(items);
				items.forEach(
					function(each) {
						console.log("each:",each);
						cEl = el.clone();
						console.log(el);
						console.log(el.html());
						console.log(cEl);
						console.log(cEl.html());
						
						var link = $compile(cEl.contents());
						cScope = scope.$new();
						cScope["item"] = each;
						link(cScope);
						cEl.insertBefore(el);
					}
				);
				el.hide();
			}
		}
	});
angular.module("angular-plugin").directive(
	"includeComponents",
	function(PluginMenuService,$route,$templateCache,$controller,$compile) {
		return {
			scope: {
				includeComponents : "@"
			},
			link: function(scope, el, attr, ctrl,transclude) {
				var items = PluginMenuService.get(scope.includeComponents);
				if(items.length > 0) {
					items.forEach(
						function(each) {
							
							templ = $templateCache.get(each.templateUrl);
							var child = $(templ)							
							el.append(child);
							var link = $compile(child.contents());

							childScope = scope.$new();
							
							if(each.controller) {
								var controller = $controller(each.controller, {});
								if(each.controllerAs) {
									childScope[each.controllerAs] = controller;
								}
							}
							childScope["component"] = each;
							childScope.$watch("component.visible", function(a,b,c,d) { if(a) { child.show() } else { child.hide() } });
							
							child.data('$ngControllerController', controller);
							
							console.log(child);
							
							link(childScope);
						}
					);
				}
			}
		}
	});
angular.module("angular-plugin").directive(
	"menuItem",
	function(PluginMenuService) {
		return {
			transclude: 'element',
			scope: {
				menuItem : "@"
			},
			link: function(scope, el, attr, ctrl, transclude) {
				var items = PluginMenuService.get(scope.menuItem);
				if(items.length > 0) {
					items.forEach(function(each){
						transclude(function(transEl,transScope) {
							transScope.item = each;
							transScope.children = PluginMenuService.get(each.path);
							transScope.$watch("item.visible", function(a,b,c,d) { if(a) { transEl.show() } else { transEl.hide() } });
							el.parent().append(transEl);
						});
					});
				} else {
					transclude(function(transEl,transScope) {
						el.hide();
					});
				}
			}
		}
	});
angular.module("angular-plugin").service("PluginMenuService" , function($route,$rootScope,$location) {
    	
    	var routeProvider = angular.module("angular-plugin").routeProvider;
    	
    	var menus = {};
    	var s = {
        	goto : function(route) {
        		if($location.path!=route) {
        			$location.path(route);
        		}
        	},
        	get : function(path) {
        		if(!menus[path]) {
        			return [];
        		}
        		return menus[path].children;
        	},
        	getItems : function() {
        		out = _.keys(menus);
        		return out;
        	},
        	getItem : function(path) {
        		return menus[path];
        	},
        	getAncestors : function(path,skip,chop) {
        		pe = path.split("/");
        		out = [];
        		a = [];
        		
        		pe.forEach(function(each) {
        			a.push(each);
        			p = a.join("/");
        			item = s.getItem(p);
        			console.log(p,item);
        			out.push(item);
        		});
        		
        		out = out.splice(skip);
        		if(chop>0) {
        			out.splice(chop*-1);
        		}
        		return out;
        	},
        	addItem : function(path,name,item) {

        		item.path = path+name;

        		$rootScope.$on("$locationChangeSuccess", function(e,u) { item.active = ($location.path().indexOf(item.path) == 0) ;});
        		routeProvider.when(item.path,item);
        		
        		item.order = item.order | 0;
        		
        		if(item['visible'] == 'undefined') {
        			item.visible = true;
        		}

        		item.active = ($location.path().indexOf(item.path) == 0);
        		
        		menus[path] = menus[path] || {children:[]};
        		
        		
        		var count = 0;
        		
        		// remove any old items with the same ID
        		_.each(menus[path].children, function(c) {
        			if(c && item && c.id && item.id && c.id==item.id) {
        				menus[path].children.splice(count,1);
        			}
        			count++;
        		});
	    		menus[path].children.push(item);
	    		menus[path].children = _.sortBy(
	    						menus[path].children,
	    						function(child){
	    							return child.order;
	    						}
	    					);
	    		menus[path+name] = menus[path+name] || {children:[], item : item};
        	},
	    	setDefault : function(item) {
	    		routeProvider.otherwise(item);
	    	},
        	addRoute : function(path,item) {
        		routeProvider.when(path,item);
        	},
	    	setDefault : function(item) {
	    		routeProvider.otherwise(item);
	    	}
    	}
    	
    	return s;
    	
     });
