angular.module("angular-plugin",["ngRoute"]);


angular.module("angular-plugin")
.config(function($routeProvider,$locationProvider) {
		$locationProvider.hashPrefix('');
		angular.module("angular-plugin").routeProvider = $routeProvider;
});