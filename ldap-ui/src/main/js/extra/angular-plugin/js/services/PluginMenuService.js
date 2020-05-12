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
