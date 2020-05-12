angular.module("angular-select",["ngRoute"]);
angular.module("angular-select")
.config(function() {});

angular.module("angular-select").directive(
	"anSelect",
	[ 
		"$compile","$templateCache","$timeout","$interval",
		function($compile,$templateCache,$timeout,$interval) {
			return {
				require : 'ngModel',
				transclude: false,
				scope : {
					ngDisabled : "=?",
					placeholder : "=?",
					allowReset : "=?",
					resolve : "&",
					query : "&",
					map : "&",
					ngChange : "&",
					model : "=ngModel"
				},
				link : function(scope, element, attrs, ngModelCtrl, transclude) {
					
					scope.placeholder = scope.placeholder || "Please select"; 
					
					scope.search = "";
					scope.current = {};
					scope.active = false;

					if(angular.isUndefined(scope.resolve)) {
						scope.resolve = function(a,callback) {
							console.log("anSelect:ctrl: no resolve function defined!");
							callback(undefined);
						}
					} else {
						scope.resolve = scope.resolve();
					}
					
					if(angular.isUndefined(scope.query)) {
						scope.query = function(a,callback) {
							console.log("anSelect:ctrl: no query function defined!");
							callback(a,[]);
						}
					} else {
						scope.query = scope.query();
					}

					if(angular.isUndefined(scope.ngChange)) {
						scope.ngChange = function() {
							console.log("anSelect:ctrl: no ngChange function defined!");
							callback(a,[]);
						}
					} else {
						scope.ngChange = scope.ngChange();
					}

					scope.select = function(id) {
						console.log("anSelect:ctrl: select: resolve "+id);
						scope.resolve(id, scope.set);
					}
					
					scope.deselect = function() {
						console.log("anSelect:ctrl: deselect: {}");
						scope.set({});
					}
					
					scope.set = function(obj) {
						var obj = obj || {};
						console.log("anSelect:ctrl: watch current: resolved object: ", obj);
						scope.model = obj.id;
						scope.current = obj;
						
						scope.$evalAsync(function() {
							if(scope.ngChange) {
								console.log("anSelect:ctrl: watch current: calling ng-change "+scope.model,scope.ngChange);
								scope.ngChange(scope.model);
							} 
						});
						
						scope.active = true;
						scope.toggle();

					};
					
					scope.queryCallback = function(term,results) {
						if(term!=scope.search) return;
						console.log("anSelect:ctrl: query callback: "+term);
						scope.results = results;
						if(results.length > 12) {
							results.length = 12;
							scope.hasMore = true;
						} else {
							scope.hasMore = false;
						}
					}
					
					scope.toggle = function() {
						scope.active = !scope.active;
						console.log("anSelect:ctrl: toggle "+!scope.active+" ---> "+scope.active);
						console.log("toggle: "+scope.active);
						elements["empty"].hide();
						elements["inactive"].hide();
						elements["overlay"].hide();
						if(scope.active) {
							elements["overlay"].show();
							elements["query"].children("input").focus();
							scope.runQuery();
						} else {
							if(!angular.isUndefined(scope.current.id)) {
								elements["inactive"].show();
							} else {
								elements["empty"].show();
							}
						}
					};

					scope.runQuery = function() {
						scope.query(scope.search, scope.queryCallback);
					}

					var states = [ "inactive", "active", "empty", "query", "list" , "arrow" , "remove", "overlay" ];


					var templates = {};
					var elements = {};
					var ids = {};

					_.each(states,function(state) {
						var x = element.children("."+state);
						if(x.length > 0) {
							templates[state] = x[0].outerHTML;
						} else {
							templates[state] = $templateCache.get("/select_"+state+".html");
						}
						link = $compile(templates[state]);
						elements[state] = link(scope);
					});

					element.css("padding","0");
					element.css("border-width","0");
					
					element.append(elements["empty"]);
					element.append(elements["inactive"]);
					element.append(elements["overlay"]);
					elements["overlay"].append(elements["query"]);
					elements["overlay"].append(elements["list"]);
					
					element.children().hide();

					elements["empty"].show();
					elements["inactive"].hide();
					elements["inactive"].on("click", function() { console.log("click on inactive"); scope.toggle(); });
					elements["overlay"].hide();

					scope.$watch("model",function(a,b,c) {
						console.log("anSelect:ctrl: watch model: "+b+" ---> "+a);
						scope.resolve(a,scope.set);
					});

					scope.$on('$destroy', function() {
				        _.each(
				        		elements,
				        		function(e) {
				        			e.remove();
				        		}
				        	);
					});
					
					$(document).on("click", function(e,a,b) {
						if(!scope.active) return;
						var inside = $(element).has(e.target).length > 0;
						if(inside) {
							return;
						} else {
							scope.toggle();
						}
					});

					scope.resolve(scope.model,scope.set);

				}
			}
		}
	]
);