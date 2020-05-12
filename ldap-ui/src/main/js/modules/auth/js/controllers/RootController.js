angular.module("rooster").controller(
	"RootController", 
	[ '$timeout', '$rootScope', function($timeout,$rootScope) {

		var root = this;
		root.show = false;
		
		$rootScope.$on('auth', function() {
			console.log("root controller - received auth event ... ");
			root.show = true;
		});
		
		console.log("root controller <init> - ");
	}]
	
);
