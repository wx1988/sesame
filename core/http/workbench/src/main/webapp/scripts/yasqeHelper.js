/// <reference path="template.ts" />
/// <reference path="jquery.d.ts" />
// WARNING: Do not edit the *.js version of this file. Instead, always edit the
// corresponding *.ts source in the ts subfolder, and then invoke the
// compileTypescript.sh bash script to generate new *.js and *.js.map files.
var workbench;
(function (workbench) {
    (function (yasqeHelper) {
        function setupCompleters(namespaces) {
            var newPrefixCompleterName = "customPrefixCompleter";

            //take the current prefix completer as base, to present our own namespaces for prefix autocompletion
            YASQE.registerAutocompleter(newPrefixCompleterName, function (yasqe, name) {
                //also, autoappend prefixes if needed
                yasqe.on("change", function () {
                    YASQE.Autocompleters.prefixes.appendPrefixIfNeeded(yasqe, name);
                });
                return {
                    bulk: true,
                    async: false,
                    autoShow: true,
                    get: function () {
                        var completerArray = [];
                        for (var key in namespaces) {
                            completerArray.push(key + " <" + namespaces[key] + ">");
                        }
                        return completerArray;
                    },
                    isValidCompletionPosition: function () {
                        return YASQE.Autocompleters.prefixes.isValidCompletionPosition(yasqe);
                    },
                    preProcessToken: function (token) {
                        return YASQE.Autocompleters.prefixes.preprocessPrefixTokenForCompletion(yasqe, token);
                    }
                };
            });

            YASQE.defaults.autocompleters = [newPrefixCompleterName, "variables"]; //i.e., disable the property/class autocompleters
        }
        yasqeHelper.setupCompleters = setupCompleters;
    })(workbench.yasqeHelper || (workbench.yasqeHelper = {}));
    var yasqeHelper = workbench.yasqeHelper;
})(workbench || (workbench = {}));
//# sourceMappingURL=yasqeHelper.js.map
