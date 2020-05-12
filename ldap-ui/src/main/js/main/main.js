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