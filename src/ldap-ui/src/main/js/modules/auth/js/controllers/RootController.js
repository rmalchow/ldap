angular.module("rooster").controller(
	"RootController", 
	[ '$timeout', '$rootScope', 'AuthenticationService', function($timeout,$rootScope,AuthenticationService) {

		var root = this;
		root.show = true;
			
		console.log("root controller <init> - ");
	}]
	
);
