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