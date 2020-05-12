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