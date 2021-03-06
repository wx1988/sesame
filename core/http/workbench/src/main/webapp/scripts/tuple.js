/// <reference path="template.ts" />
/// <reference path="jquery.d.ts" />
/// <reference path="paging.ts" />
// WARNING: Do not edit the *.js version of this file. Instead, always edit the
// corresponding *.ts source in the ts subfolder, and then invoke the
// compileTypescript.sh bash script to generate new *.js and *.js.map files.
workbench.addLoad(function () {
    if (workbench.paging.hasQueryParameter(workbench.paging.LIMIT)) {
        var limit = parseInt(0 + workbench.paging.getQueryParameter(workbench.paging.LIMIT), 10);
        $(workbench.paging.LIM_ID).val(String(limit));
    }
    workbench.paging.correctButtons();
    limit = workbench.paging.getLimit(); // Number

    // Modify title to reflect total_result_count cookie
    if (limit > 0) {
        var h1 = document.getElementById('title_heading');
        var total_result_count = workbench.paging.getTotalResultCount();
        var have_total_count = (total_result_count > 0);
        var offset = workbench.paging.getOffset();
        var first = offset + 1;
        var last = offset + limit;
        last = have_total_count ? Math.min(total_result_count, last) : last;
        var newHTML = /^.*\(/.exec(h1.innerHTML)[0] + first + '-' + last;
        if (have_total_count) {
            newHTML = newHTML + ' of ' + total_result_count;
        }
        newHTML = newHTML + ')';
        h1.innerHTML = newHTML;
    }
    workbench.paging.setShowDataTypesCheckboxAndSetChangeEvent();
});
//# sourceMappingURL=tuple.js.map
