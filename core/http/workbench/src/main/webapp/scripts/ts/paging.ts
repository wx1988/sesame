/// <reference path="template.ts" />
/// <reference path="jquery.d.ts" />

// WARNING: Do not edit the *.js version of this file. Instead, always edit the
// corresponding *.ts source in the ts subfolder, and then invoke the
// compileTypescript.sh bash script to generate new *.js and *.js.map files.

module workbench {

    export module paging {

        var KT = 'know_total';

        var OFFSET = 'offset';

        export var LIMIT = 'limit';

        export var LIM_ID = '#' + LIMIT;

        var AMP = decodeURIComponent('%26');

        /**
         * Invoked in graph.xsl and tuple.xsl for download functionality. Takes a
         * document element by name, and creates a request with it as a parameter.
         */
        export function addGraphParam(name: string) {
            var value = $('#' + name).val();
            var url = document.location.href;
            if (url.indexOf('?') + 1 || url.indexOf(';') + 1) {
                document.location.href = url + decodeURIComponent('%26') + name
                + '=' + encodeURIComponent(value);
            } else {
                document.location.href = url + ';' + name + '='
                + encodeURIComponent(value);
            }
        }
        
        class StringMap {
            [key: string]: string;
        }

        /**
         * Scans the given URI for duplicate query parameter names, and removes
         * all but the last occurrence for any duplicate case.
         * 
         * @param {String}
         *            href The URI to simplify.
         * @returns {String} The URI with only the last occurrence of any given
         *          parameter name remaining.
         */
        function simplifyParameters(href: string) {
            var params:StringMap = {};
            var rval = '';
            var queryString = getQueryString(href);
            var start = href.substring(0, href.indexOf(queryString));
            var elements = queryString.split(decodeURIComponent('%26'));
            for (var i = 0; elements.length - i; i++) {
                var pair = elements[i].split('=');
                params[pair[0]] = pair[1];
                // Keep looping. We are interested in the last value.
            }
            for (var name in params) {
                // use hasOwnProperty to filter out keys from the
                // Object.prototype
                if (params.hasOwnProperty(name)) {
                    rval += name + '=' + params[name] + AMP;
                }
            }
            rval = start + rval.substring(0, rval.length - 1);
            return rval;
        }


        /**
         * First, adds the given parameter to the URL query string. Second, adds a
         * 'know_total' parameter if its current value is 'false' or non-existent.
         * Third, simplifies the URL. Fourth, sends the browser to the modified URL.
         * 
         * @param {String}
         *            name The name of the query parameter.
         * @param {number
         *            or string} value The value of the query parameter.
         */
        export function addPagingParam(name: string, value: number) {
            var url = document.location.href;
            var hasParams = (url.indexOf('?') + 1 || url.indexOf(';') + 1);
            var sep = hasParams ? AMP : ';';
            url = url + sep + name + '=' + value;
            if (!hasQueryParameter(KT) || 'false' == getQueryParameter(KT)) {
                url += AMP + KT + '=' + getTotalResultCount();
            }
            if (!hasQueryParameter('query')) {
                url += AMP + 'query=' + workbench.getCookie('query');
                url += AMP + 'ref=' + workbench.getCookie('ref');
            }
            document.location.href = simplifyParameters(url);
        }

        /**
         * Invoked in tuple.xsl and explore.xsl. Changes the limit query parameter,
         * and navigates to the new URL.
         */
        export function addLimit() {
            addPagingParam(LIMIT, $(LIM_ID).val());
        }

        /**
         * Increments the offset query parameter, and navigates to the new URL.
         */
        export function nextOffset() {
            addPagingParam(OFFSET, getOffset() + getLimit());
        }

        /**
         * Decrements the offset query parameter, and navigates to the new URL.
         */
        export function previousOffset() {
            addPagingParam(OFFSET, Math.max(0, getOffset() - getLimit()));
        }

        /**
         * @returns {number} The value of the offset query parameter.
         */
        export function getOffset() {
            var offset = getQueryParameter(OFFSET);
            return ('' == offset) ? 0 : parseInt(offset, 10);
        }

        /**
         * @returns {number} The value of the limit query parameter.
         */
        export function getLimit() {
            return parseInt($(LIM_ID).val(), 10);
        }

        /**
         * Retrieves the URL query parameter with the given name.
         * 
         * @param {String}
         *            name The name of the parameter to retrieve.
         * @returns {String} The value of the given parameter, or an empty string if
         *          it doesn't exist.
         */
        export function getQueryParameter(name: string) {
            var rval = '';
            var elements = getQueryString(document.location.href).split(decodeURIComponent('%26'));
            for (var i = 0; elements.length - i; i++) {
                var pair = elements[i].split('=');
                if (name != pair[0]) {
                    continue;
                }
                rval = pair[1];
                // Keep looping. We are interested in the last value.
            }
            return rval;
        }

        /**
         * Gets whether a URL query parameter with the given name is present.
         * 
         * @param {String}
         *            name The name of the parameter to retrieve.
         * @returns {Boolean} True, if a parameter with the given name is in
         *                    the URL. Otherwise, false.
         */
        export function hasQueryParameter(name: string) {
            var rval = false;
            var elements = getQueryString(document.location.href).split(decodeURIComponent('%26'));
            for (var i = 0; elements.length - i; i++) {
                var pair = elements[i].split('=');
                if (name == pair[0]) {
                    rval = true;
                    break;
                }
            }
            return rval;
        }

        /**
         * Convenience function for returning the tail of a string after a given
         * character.
         *   
         * @param {String}
         *            value The string to get the tail of.
         * @param split
         *            character to give tail after
         * @returns The substring after the 'split' character, or the original
         *          string if 'split' is not found.
         */
        function tailAfter(value: string, split: string) {
            return value.substring(value.indexOf(split) + 1);
        }

        export function getQueryString(href: string) {
            return tailAfter(tailAfter(href, '?'), ';');
        }

        /**
         * Using the value of the 'limit' query parameter, correct the text of the
         * Next and Previous buttons. Makes use of RegExp to preserve any
         * localization.
         */
        export function correctButtons() {
            var buttonWordPattern = /^[A-z]+\s+/;
            var nextButton = $('#nextX');
            var oldNext = nextButton.val();
            var count = parseInt(/\d+$/.exec(oldNext)[0], 10);
            var limit = workbench.paging.getLimit();
            nextButton.val(buttonWordPattern.exec(oldNext)[0] + limit);
            var previousButton = $('#previousX');
            previousButton
                .val(buttonWordPattern.exec(previousButton.val())[0] + limit);
            var offset = workbench.paging.getOffset();
            previousButton.prop('disabled', (offset <= 0 || limit <= 0));
            nextButton.prop('disabled',
                (count < limit || limit <= 0 || (offset + count) >= getTotalResultCount()));
        }

        /**
         * Gets the total result count, preferably from the 'know_total' query
         * parameter. If the parameter doesn't exist, get it from the
         * 'total_result_count' cookie.
         * 
         * @returns {Number} The given total result count, or zero if it isn't
         *          given.
         */
        export function getTotalResultCount() {
            var total_result_count = 0;
            var s_trc = workbench.paging.getQueryParameter(KT);
            if (s_trc.length == 0) {
                s_trc = workbench.getCookie('total_result_count');
            }

            if (s_trc.length > 0) {
                total_result_count = parseInt(s_trc, 10);
            }

            return total_result_count;
        }

        module DataTypeVisibility {
            function setCookie(c_name: string, value: boolean, exdays: number) {
                var exdate = new Date();
                exdate.setDate(exdate.getDate() + exdays);
                document.cookie = c_name + "=" + value + 
                    ((exdays == null) ? "" : 
                    "; expires=" + exdate.toUTCString());
            }

            export function setShow(show: boolean) {
                setCookie('show-datatypes', show, 365);
                var data = show ? 'data-longform' : 'data-shortform';
                $('div.resource[' + data + ']').each(function() {
                    var me = $(this);
                    me.find('a:first').text(decodeURIComponent(me.attr(data)));
                });
            }
        }

        export function setShowDataTypesCheckboxAndSetChangeEvent() {
            var hideDataTypes = (workbench.getCookie('show-datatypes') == 'false');
            var showDTcb = $("input[name='show-datatypes']");
            if (hideDataTypes) {
                showDTcb.prop('checked', false);
                DataTypeVisibility.setShow(false);
            }
            showDTcb.on('change', function() {
                DataTypeVisibility.setShow(showDTcb.prop('checked'));
            });
        }
    }
}