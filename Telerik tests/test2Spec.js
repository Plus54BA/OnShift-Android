/**

    OnShift Android Calendar Specs
    
    Test cases for the calendar view
    
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
            btnMessages: {
                id: "btnMessages"
            },
            btnAccount: {
                id: "btnAccount"
            },
            btnTutorial: {
                id: "btnTutorial"
            },
            btnCloseTutorial: {
                id: "btnDone"
            },
            btnNextTutorial: {
                id: "btnNext"
            },
            btnAvailableShiftsTutorial: {
                id: "btnShiftsDone"
            },
            btnMessagesTutorial: {
                id: "btnMessagesDone"
            },
            btnTimeOff: {
                className: "icon-plus"
            },
            selTimeOffType: {
                name: "time_off_type"
            },
            txtTimeOffRepeats: {
                name: "repeat_days"
            },
            txtTimeOffReason: {
                name: "reason"
            },
            btnSubmitTimeOff: {
                id: "requestTimeOff"
            },
            btnRecurringTimeOff: {
                className: "recurring"
            },
            btnDeleteTimeOff: {
                id: "delete-button"
            },
            btnDeleteAllTimeOff: {
                id: "delete-all-button"
            },
            btnConfirmDeleteTimeOff: {
                id: "confirm"
            },
            btnCloseBootstrapAlert: {
                className: "close"
            },
            timeOffAlertBox: {
                className: "alert"
            },
            timeOffItemList: {
                className: "pto-item"
            },
            btnToday: {
                className: "icon-today"
            },
            dateGridDate: {
                className: "datesgrid-date active showMonth"
            },
            calendarMain: {
                className: "calendar-main"
            },
            sectionTitle: {
                id: "sectionTitle"
            }
        },
    };

    var EMAIL_VALIDATE_ERROR = "Please enter a valid email to continue",
        AUTHENTICATE_ERROR = "Invalid Email or Password",
        PUSH_NOTIFICATION_PROMPT = "OnShift would like to send you push notifications for your schedule and messages. Is this okay?";

    // define all steps here so they can be reused, and so that
    // the tests read in plain language
    var stepRepository = {
        'Launch App': {
			'android': [
				android.launch('com.onshift.mobileqa'),
                android.wait(8000)
			]
        },

        'Navigation tests': {
            'android': [
                //navigate to available shifts
                //web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                }),
                //navigate to messages
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnMessages),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Messages");
                }),
                //navigate to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAccount),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Profile");
                })
            ]
        },

        'Tutorial tests': {
            'android': [
                //Open tutorial and close using Skip button
                //web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnTutorial),
                web.tap(queries.menuInfo.btnCloseTutorial),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Calendar");
                }),
                //Open tutorial and navigate through slides and press done on final slide
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnTutorial),
                web.tap(queries.menuInfo.btnNextTutorial),
                web.tap(queries.menuInfo.btnNextTutorial),
                web.tap(queries.menuInfo.btnNextTutorial),
                web.tap(queries.menuInfo.btnNextTutorial),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Calendar");
                }),
                //Navigate to available shifts, press tutorial and done to go back to available shifts
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnAvailableShifts),
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnTutorial),
                web.tap(queries.menuInfo.btnAvailableShiftsTutorial),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Available Shifts");
                }),
                //Navigate to messages, press tutorial and done to go back to messages
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnMessages),
                web.tap(queries.menuInfo.menu),
                web.tap(queries.menuInfo.btnTutorial),
                web.tap(queries.menuInfo.btnMessagesTutorial),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Messages");
                })
            ]
        },

        'Create time off': {
            'android': [
                web.tap(queries.menuInfo.btnTimeOff),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Time Off Request");
                }),
                web.setSelectedText(queries.menuInfo.selTimeOffType, 'EDO'),
                web.setValue(queries.menuInfo.txtTimeOffReason, 'Testing time off'),
                web.tap(queries.menuInfo.btnSubmitTimeOff),
                web.wait(1000),
                web.getHtml(queries.menuInfo.timeOffAlertBox, function(res) {
                    assert(res).equals('<button data-dismiss="alert" aria-hidden="true" class="close">X</button>Time-off request submitted');
                }),
                web.tap(queries.menuInfo.btnCloseBootstrapAlert)
            ]
        },

        'Delete time off': {
            'android': [
                web.tap(queries.menuInfo.timeOffItemList),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Time Off Request");
                }),
                web.tap(queries.menuInfo.btnDeleteTimeOff),
                web.tap(queries.menuInfo.btnConfirmDeleteTimeOff),
                web.getHtml(queries.menuInfo.timeOffAlertBox, function(res) {
                    assert(res).equals('<button data-dismiss="alert" aria-hidden="true" class="close">X</button>Time-off request has been deleted.');
                }),
                web.tap(queries.menuInfo.btnCloseBootstrapAlert)
            ]
        },

        'Create recurring time off': {
            'android': [
                web.tap(queries.menuInfo.btnTimeOff),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Time Off Request");
                }),
                web.setSelectedText(queries.menuInfo.selTimeOffType, 'EDO'),
                web.tap(queries.menuInfo.btnRecurringTimeOff),
                web.setValue(queries.menuInfo.txtTimeOffRepeats, '1'),
                web.setValue(queries.menuInfo.txtTimeOffReason, 'Testing time off'),
                web.tap(queries.menuInfo.btnSubmitTimeOff),
                web.wait(1000),
                web.getHtml(queries.menuInfo.timeOffAlertBox, function(res) {
                    assert(res).equals('<button data-dismiss="alert" aria-hidden="true" class="close">X</button>Time-off request submitted');
                }),
                web.tap(queries.menuInfo.btnCloseBootstrapAlert)
            ]
        },

        'Delete recurring time off': {
            'android': [
                web.tap(queries.menuInfo.timeOffItemList),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Time Off Request");
                }),
                web.tap(queries.menuInfo.btnDeleteAllTimeOff),
                web.tap(queries.menuInfo.btnConfirmDeleteTimeOff),
                web.getHtml(queries.menuInfo.timeOffAlertBox, function(res) {
                    assert(res).equals('<button data-dismiss="alert" aria-hidden="true" class="close">X</button>Time-off request has been deleted.');
                }),
                web.tap(queries.menuInfo.btnCloseBootstrapAlert)
            ]
        },

        'Today button test': {
            'android': [
                web.executeScript('$("#"+(Date.today().addDays(1)).getTime()).click()'),
                web.tap(queries.menuInfo.btnToday),
                web.getScriptResult('(Date.today().addDays(1)).getTime()', null, function(r) {
                    web.getHtml(queries.menuInfo.dateGridDate, function(res) {
                        assert(res).contains(r);
                    })
                }),
            ]
        }
    };

    describe("Calendar view tests", function() {

        test("Navigation test", function() {
            step('Launch App');
            step("Navigation tests");
            step("Tutorial tests");
        });

        test("Time off test", function() {
            step('Launch App');
            step('Create time off');
            step('Delete time off');
        });

        test("Recurring Time off test", function() {
            step('Launch App');
            step('Create recurring time off');
            step('Delete recurring time off');
        });

        test("Today button test", function() {
            step('Launch App');
            step('Today button test');
        });

    }, stepRepository);
});
