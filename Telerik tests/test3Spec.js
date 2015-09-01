/**

    OnShift Android Available Shifts Specs
    
    Test cases for the available shifts view
    
**/

spec(function() {
    // queries for this suite, structured in a way
    // that makes sense for this view
    var queries = {
        menuInfo: {
            menu: {
                className: "menu-toggle"
            },
            btnAvailableShifts: {
                id: "btnAvailableShifts"
            },
            btnFilter: {
                className: "dropdown-btn dropdown-toggle"
            },
            btnFilterAll: {
                id: "All"
            },
            btnFilterOpen: {
                id: "OpenShifts"
            },
            btnFilterFillin: {
                id: "Fill-inRequests"
            },
            btnLoadMore: {
                className: "load-more"
            },
            btnRequestShift: {
                className: "btn-bid main-btn"
            },
            btnCancelShift: {
                className: "btn-withdraw secondary-btn"
            },
            btnCancelFillin: {
                className: "btn-withdraw main-btn"
            },
            btnBackToShifts: {
                className: "icon-prev"
            },
            optionFilter: {
                className: "state"
            },
            sectionTitle: {
                className: "navbar-brand"
            }
        },
    };

    // define all steps here so they can be reused, and so that
    // the tests read in plain language
    var stepRepository = {
        'Launch App': {
			'android': [
				android.launch('com.onshift.mobileqa'),
                android.wait(8000)
			]
        },

        'Navigate to available shifts': {
            'android': [
                //navigate to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                })
            ]
        },

        'Test filter': {
            'android': [
                //navigate to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                }),
                //Fill-in filter test (should give fillins only)
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterFillin),
                web.wait(2000),
                web.getScriptResult('$(".badge:first").html()', null, function(res) {
                    assert(res).contains("Fill-in");
                }),
                //Open shift filter test (should give open shifts only)
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterOpen),
                web.wait(2000),
                web.getScriptResult('$(".badge:first").html()', null, function(res) {
                    assert(res).contains("Open Shift");
                }),
                //All filter test (show give fillins and open shifts)
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterAll),
                web.wait(2000),
                web.getScriptResult('$(".badge:first").html()', null, function(res) {
                    assert(["Fill-in", "Open Shift"]).contains(res);
                })
            ]
        },

        'Load shifts': {
            'android': [
                //navigate to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                }),
                web.getScriptResult('$(".available-shift").length', null, function(res) {
                    assert(res).lessThanOrEqualTo(10);
                }),
                web.tap(queries.menuInfo.btnLoadMore),
                web.wait(2000),
                web.getScriptResult('$(".available-shift").length', null, function(res) {
                    assert(res).lessThanOrEqualTo(20);
                }),
            ]
        },

        'Request open shift': {
            'android': [
                //navigate to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterOpen),
                web.wait(2000),
                web.executeScript('$(".badge:first").click();'),
                web.wait(2000),
                web.tap(queries.menuInfo.btnRequestShift),
                web.wait(2000),
                web.tap(queries.menuInfo.btnBackToShifts),
                web.wait(2000),
                web.getScriptResult('$(".available-shift:first").find("span:first").html()', null, function(res) {
                    assert(res).contains("Open Shift: Requested");
                }),
            ]
        },

        'Cancel open shift': {
            'android': [
                //navigate to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterOpen),
                web.wait(2000),
                web.executeScript('$(".badge-requested:first").click();'),
                web.wait(2000),
                web.tap(queries.menuInfo.btnCancelShift),
                web.wait(2000),
                web.tap(queries.menuInfo.btnBackToShifts),
                web.wait(2000),
                web.getScriptResult('$(".available-shift:first").find("span:first").html()', null, function(res) {
                    assert(res).contains("Open Shift");
                }),
            ]
        },

        'Request fill in': {
            'android': [
                //navigate to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterFillin),
                web.wait(2000),
                web.executeScript('$(".badge:first").click();'),
                web.wait(2000),
                web.tap(queries.menuInfo.btnRequestShift),
                web.wait(2000),
                web.tap(queries.menuInfo.btnBackToShifts),
                web.wait(2000),
                web.getScriptResult('$(".available-shift:first").find("span:first").html()', null, function(res) {
                    assert(res).contains("Fill in: Requested");
                }),
            ]
        },

        'Cancel fill in': {
            'android': [
                //navigate to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterFillin),
                web.wait(2000),
                web.executeScript('$(".badge-requested:first").click();'),
                web.wait(2000),
                web.tap(queries.menuInfo.btnCancelFillin),
                web.wait(2000),
                web.tap(queries.menuInfo.btnBackToShifts),
                web.wait(2000),
                web.getScriptResult('$(".available-shift:first").find("span:first").html()', null, function(res) {
                    assert(res).contains("Fill-in");
                }),
            ]
        }

    };

    describe("Available shifts tests", function() {

        test("Filter test", function() {
            step('Launch App');
            step("Navigate to available shifts");
            step("Test filter");
        });

        test("Load shifts test", function() {
            step('Launch App');
            step("Navigate to available shifts");
            step("Load shifts");
        });

        test("Request open shift test", function() {
            step('Launch App');
            step("Navigate to available shifts");
            step("Request open shift");
        });

        test("Cancel open shift test", function() {
            step('Launch App');
            step("Navigate to available shifts");
            step("Cancel open shift");
        });

        test("Request fill in test", function() {
            step('Launch App');
            step("Navigate to available shifts");
            step("Request fill in");
        });

        test("Cancel fill in test", function() {
            step('Launch App');
            step("Navigate to available shifts");
            step("Cancel fill in");
        });

    }, stepRepository);
});
