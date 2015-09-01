/**

    OnShift Android Login Specs
    
    Test cases for the login view
    
**/

spec(function(){
	// queries for this suite, structured in a way
	// that makes sense for this web page
	var queries = {
		loginInfo: {
			username: {id: "username"},
            password: {id: "password"},
            loginUser: {id: "dvNavbar"},
            registerUser: {className: "register-user"},
            validateEmail: {id: "validateEmail"},
            loginButton: {id: "loginButton"}
		},
	};
    
    var EMAIL_VALIDATE_ERROR = "Please enter a valid email address", 
        AUTHENTICATE_ERROR = "Invalid Email or Password",
        PUSH_NOTIFICATION_PROMPT = "OnShift would like to send you push notifications for your schedule and messages. Is this okay?";

	// define all steps here so they can be reused, and so that
	// the tests read in plain language
	var stepRepository = {
		"Select Web View": {
			'android': [
				android.launch('com.onshift.mobileqa'),
                android.wait(8000)
			]
		},
        //enter valid email test
		"enter email input": {
			'android': [
				web.setValue(queries.loginInfo.username, "onshift.tester@mailinator.com"),
				web.getValue(queries.loginInfo.username, function(res){
					assert(res).equals("onshift.tester@mailinator.com");
				})
			]
		},
        // test entering an invalid email
		"enter invalid email input": {
			'android': [
				web.setValue(queries.loginInfo.username, "gkonchady"),
				web.getValue(queries.loginInfo.username, function(res){
					assert(res).equals("gkonchady");
				})
			]
		},
        // test entering an invalid email
		"enter inactive email input": {
			'android': [
				web.setValue(queries.loginInfo.username, "gkonchady2376@mailinator.com"),
				web.getValue(queries.loginInfo.username, function(res){
					assert(res).equals("gkonchady2376@mailinator.com");
				})
			]
		},
        //enter valid password 
		"enter password": {
			'android': [
				web.setValue(queries.loginInfo.password, "Abcdef1!"),
				web.getValue(queries.loginInfo.password, function(res){
					assert(res).equals("Abcdef1!");
				})
			]
		},
        //enter invalid password 
		"enter invalid password": {
			'android': [
				web.setValue(queries.loginInfo.password, "blahblah"),
				web.getValue(queries.loginInfo.password, function(res){
					assert(res).equals("blahblah");
				})
			]
		},
        //test validating the invalid email
		"validate invalid email": {
			'android': [
				web.dialogs.prepare(web.dialogs.ALERT, { dialogResult: EMAIL_VALIDATE_ERROR }),
				web.click(queries.loginInfo.validateEmail),
				web.dialogs.getState(function(state){
					assert(state[web.dialogs.ALERT].length).greaterThan(0);    
				})
			]
		},
        //test validating the valid email
		"validate email": {
			'android': [
				web.click(queries.loginInfo.validateEmail),
				web.setValue(queries.loginInfo.password, "Abcdef1!"),
				web.getValue(queries.loginInfo.password, function(res){
					assert(res).equals("Abcdef1!");
				})
			]
		},
        //test validating a valid email but inactive account
		"validate email to complete setup": {
			'android': [
				web.click(queries.loginInfo.validateEmail),
				web.getStyle(queries.loginInfo.registerUser, 'display', function(res) {
					assert(res).equals('')						
				})
			]
		},
        //test authentication with invalid password
		"attempt invalid login": {
			'android': [
                web.dialogs.prepare(web.dialogs.ALERT, { dialogResult: AUTHENTICATE_ERROR }),
				web.click(queries.loginInfo.loginButton),
				web.dialogs.getState(function(state){
					assert(state[web.dialogs.ALERT].length).greaterThan(0);    
				})
			]
		},
        //test authentication with invalid password
		"attempt valid login": {
			'android': [
                web.dialogs.prepare(web.dialogs.PROMPT, { dialogResult: PUSH_NOTIFICATION_PROMPT }),
				web.click(queries.loginInfo.loginButton),
                android.tap({ 'class':'android.widget.Button', 'properties': { 'id': 'btnNextSlide' } })
			]
		}
	};
    
	describe("Login view tests", function(){
		
        test("Validate invalid email test", function(){
			step("Select Web View");
			step("enter invalid email input");
            step("validate invalid email");
        });
		
        test("Validate email test 1", function(){
			step("Select Web View");
			step("enter email input");
            step("validate email");
        });
        
        test("Validate email test 2", function(){
			step("Select Web View");
			step("enter inactive email input");
            step("validate email to complete setup");
        });
        
        test("Invalid password login", function(){
			step("Select Web View");
			step("enter email input");
            step("validate email");
            step("enter invalid password");
            step("attempt invalid login");
        });
        
        test("Valid password login", function(){
			step("Select Web View");
			step("enter email input");
            step("validate email");
            step("enter password");
            step("attempt valid login");
        });
    
    }, stepRepository);
});