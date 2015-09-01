/**

    OnShift Android Messages Specs
    
    Test cases for the messages view
    
**/

spec(function() {
    // queries for this suite, structured in a way
    // that makes sense for this view
    var queries = {
        menuInfo: {
            menu: {
                className: "menu-toggle"
            },
            btnMessages: {
                id: "btnMessages"
            },
            btnFilter: {
                className: "dropdown-btn"
            },
            btnFilterAll: {
                id: "All"
            },
            btnFilterOpen: {
                id: "OpenShift"
            },
            btnFilterDrop: {
                id: "DropShift"
            },
            btnFilterShift: {
                id: "ShiftRequest"
            },
            btnFilterGeneral: {
                id: "General"
            },
            btnFilterResponse: {
                id: "Response"
            },
            btnFilterShowClosedMessages: {
                id: "ShowClosedMessages"
            },
            btnFilterShowArchivedMessages: {
                id: "ShowArchivedMessages"
            },
            btnMsgReply: {
                className: "btn-sm"
            },
            btnMsgReplyFirst: {
                className: "btn-sm",
                index: 0
            },
            btnReply: {
                className: "btn-reply"
            },
            btnReplyYes: {
                className: "btn-reply-yes"
            },
            btnReplyNo: {
                className: "btn-reply-no"
            },
            btnReplyMaybe: {
                className: "btn-reply-conditions"
            },
            btnReplyCancel: {
                className: "btn-cancel"
            },
            btnLoadMore: {
                id: "loadMore"
            },
            bootstrapAlertBox: {
                className: "alert"
            },
            btnCloseBootstrapAlert: {
                className: "close"
            },
            badge: {
                className: "badge",
                index: 0
            },
            messageRows: {
                className: "message-row"
            },
            replyButtonRows: {
                className: "button-row"
            },
            sectionTitle: {
                id: "sectionTitle"
            },
            replyTextArea: {
                className: "removeDouble"
            },
            pillStatus: {
                className: "badge"
            }
        }
    };
    
    // define all steps here so they can be reused, and so that
    // the tests read in plain language
    var stepRepository = {
        'Launch App': {
			'android': [
				android.launch('com.onshift.mobileqa'),
                android.wait(8500)
			]
        },

        'Message filter tests': {
            'android': [
                web.click(queries.menuInfo.btnMessages),
                web.wait(2000),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Messages");
                }),
                //show closed and archived messages
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterShowClosedMessages),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterShowArchivedMessages),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterAll),
                web.wait(1000),
                web.getHtml(queries.menuInfo.badge, function(res) {
                    assert(["All", "Open Shift", "Drop Shift Request", "Shift Request", "General", "Response"]).contains(res);
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterOpen),
                web.wait(1000),
                web.getHtml(queries.menuInfo.badge, function(res) {
                    assert(res).equals("Open Shift");
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterDrop),
                web.wait(1000),
                web.getHtml(queries.menuInfo.badge, function(res) {
                    assert(res).equals("Drop Shift Request");
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterShift),
                web.wait(1000),
                web.getHtml(queries.menuInfo.badge, function(res) {
                    assert(res).equals("Shift Request");
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterGeneral),
                web.wait(1000),
                web.getHtml(queries.menuInfo.badge, function(res) {
                    assert(res).equals("General");
                }),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterResponse),
                web.wait(1000),
                web.getHtml(queries.menuInfo.badge, function(res) {
                    assert(res).equals("Response");
                })
            ]
        },
        
        'Message reply tests': {
            'android': [
                web.click(queries.menuInfo.btnMessages),
                //show closed and archived messages
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterShowClosedMessages),
                web.tap(queries.menuInfo.btnFilter),
                web.tap(queries.menuInfo.btnFilterShowArchivedMessages),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Messages");
                }),
                web.click(queries.menuInfo.btnFilter),
                web.click(queries.menuInfo.btnFilterGeneral),
                web.wait(1000),
                web.click(queries.menuInfo.btnMsgReply),
                web.setValue(queries.menuInfo.replyTextArea, "Test reply"),
                web.click(queries.menuInfo.btnReply),
                web.getHtml(queries.menuInfo.bootstrapAlertBox, function(res) {
                    assert(res).equals('<button data-dismiss="alert" aria-hidden="true" class="close">X</button>Your response has been sent.');
                })
            ]
        }, 
        
        'Load more messages test': {
            'android': [
                web.click(queries.menuInfo.btnMessages),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Messages");
                }),
                web.click(queries.menuInfo.btnFilter),
                web.click(queries.menuInfo.btnFilterShowClosedMessages),
                web.click(queries.menuInfo.btnFilter),
                web.click(queries.menuInfo.btnFilterShowArchivedMessages),
                web.executeScript('$(".message-row").length', queries.menuInfo.messageRows, function(res) {
                    assert(res).lessThanOrEqualTo(10);
                }),
                web.click(queries.menuInfo.btnLoadMore),
                web.wait(2000),
                web.executeScript('$(".message-row").length', queries.menuInfo.messageRows, function(res) {
                    assert(res).lessThanOrEqualTo(20);
                })
            ]
        },
        
        'Drop shift request tests': {
            'android': [
                web.click(queries.menuInfo.btnMessages),
                web.getHtml(queries.menuInfo.sectionTitle, function(res) {
                    assert(res).equals("Messages");
                }),
                web.click(queries.menuInfo.btnFilter),
                web.click(queries.menuInfo.btnFilterShowClosedMessages),
                web.click(queries.menuInfo.btnFilter),
                web.click(queries.menuInfo.btnFilterShowArchivedMessages),
                web.click(queries.menuInfo.btnFilter),
                web.click(queries.menuInfo.btnFilterDrop),
                web.wait(1000),
                web.getHtml(queries.menuInfo.badge, function(res) {
                    assert(res).equals("Drop Shift Request");
                }),
                web.click(queries.menuInfo.btnMsgReplyFirst),
                web.click(queries.menuInfo.btnReplyYes),
                web.getHtml(queries.menuInfo.bootstrapAlertBox, function(res) {
                    assert(res).equals('<button data-dismiss="alert" aria-hidden="true" class="close">X</button>Your response has been sent.');
                }),
                web.click(queries.menuInfo.btnCloseBootstrapAlert),
                web.click(queries.menuInfo.btnMsgReplyFirst),
                web.click(queries.menuInfo.btnReplyNo),
                web.getHtml(queries.menuInfo.bootstrapAlertBox, function(res) {
                    assert(res).equals('<button data-dismiss="alert" aria-hidden="true" class="close">X</button>Your response has been sent.');
                }),
                web.click(queries.menuInfo.btnCloseBootstrapAlert),
                web.click(queries.menuInfo.btnMsgReplyFirst),
                web.click(queries.menuInfo.btnReplyMaybe),
                web.setValue(queries.menuInfo.replyTextArea, "Test reply"),
                web.click(queries.menuInfo.btnReply),
                web.getHtml(queries.menuInfo.bootstrapAlertBox, function(res) {
                    assert(res).equals('<button data-dismiss="alert" aria-hidden="true" class="close">X</button>Your response has been sent.');
                }),
                web.click(queries.menuInfo.btnMsgReplyFirst),
                web.click(queries.menuInfo.btnReplyCancel),
                web.executeScript('$(".message-row").has(".button-row").length', queries.menuInfo.messageRows, function(res) {
                    assert(res).equals(0);
                })
            ]
        }
    };


    describe("Messages view tests", function() {

        test("Message filter tests", function() {
            step('Launch App');
            step("Message filter tests");
        });
        
        test("Message reply tests", function() {
            step('Launch App');
            step("Message reply tests");
        });
        
        test("Load more messages tests", function() {
            step('Launch App');
            step("Load more messages test");
        });
        
        test("Drop shift request tests", function() {
            step('Launch App');
            step("Drop shift request tests");
        });

    }, stepRepository);
});
        
    
        