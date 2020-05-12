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