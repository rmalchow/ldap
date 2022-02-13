angular.module("angular-plugin",["ngRoute"]);
angular.module("angular-plugin")
.config(function($routeProvider,$locationProvider) {
		$locationProvider.hashPrefix('');
		// rember the route provider so we can retrieve it later ... 
		angular.module("angular-plugin").routeProvider = $routeProvider;
});
