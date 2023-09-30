// For Firebase JS SDK v7.20.0 and later, measurementId is optional
import { initializeApp } from "https://www.gstatic.com/firebasejs/9.18.0/firebase-app.js";
import {
	getAuth,
	RecaptchaVerifier,
	signInWithPhoneNumber,
} from "https://www.gstatic.com/firebasejs/9.18.0/firebase-auth.js";
const firebaseConfig = {
	apiKey: "AIzaSyBrJ5HUMleKRQq7DzW5Vy12uaPHd95LHMg",
	authDomain: "otp-verification-app-56d91.firebaseapp.com",
	projectId: "otp-verification-app-56d91",
	storageBucket: "otp-verification-app-56d91.appspot.com",
	messagingSenderId: "488853124474",
	appId: "1:488853124474:web:d44937fcb49ef6bf1f12c3",
	measurementId: "G-Q12EVN4RG8",
};

const firebase = initializeApp(firebaseConfig);
const auth = getAuth();

window.recaptchaVerifier = null;

function render() {
	window.recaptchaVerifier = new RecaptchaVerifier(
		"recaptcha-container",
		{},
		auth
	);
	recaptchaVerifier.render();
}

//checking if phone number is verified
function phoneAuth() {
	var number = "+91" + document.getElementById("phone").value;
	console.log("working..");
	signInWithPhoneNumber(auth, number, window.recaptchaVerifier)
		.then((confirmationResult) => {
			window.confirmationResult = confirmationResult;
			console.log("OTP Sent");
			document.getElementById("verifier").style.display = "block";
		})
		.catch(function(error) {
			// error in sending OTP
			alert(error.message);
		});
}


var validnum = [document.getElementById("phone").value];
var count = 0;

//checking if code is verified
function codeverify() {
	var code = document.getElementById("otpverify").value;

	confirmationResult
		.confirm(code)
		.then(function() {
			console.log("OTP matched");
			document.getElementById("recaptcha-container").style.display = "none";
			document.getElementById("sendotp").style.display = "none";
			document.getElementById("verifier").style.display = "none";
			document.getElementsByClassName("p-conf")[0].style.display = "block";
			validnum.push(document.getElementById('phone').value);
		})
		.catch(function() {
			document.getElementsByClassName("p-conf")[0].style.display = "none";
			document.getElementsByClassName("n-conf")[0].style.display = "block";
		});
}


function phoneChange() {
	var phone = document.getElementById("phone");
	console.log(validnum);

	for (var i = 0; i < validnum.length; i++) {
		if (phone.value == validnum[i]) {

			document.getElementById("updatebtn").disabled = false;
			document.getElementById("updatebtn").title = "";
			document.getElementById("sendotp").style.display = "none";
			document.getElementById("recaptcha-container").style.display = "none";
			if (count > 0) {
				document.getElementsByClassName("p-conf")[0].style.display = "block";
			}
			count++;
			return true;
		}
	}
	document.getElementById("updatebtn").disabled = true;
	document.getElementById("updatebtn").title = "Verify number before updating profile";
	document.getElementById("sendotp").style.display = "block";
	document.getElementById("recaptcha-container").style.display = "block";
	document.getElementsByClassName("p-conf")[0].style.display = "none";
}

function validateEmail() {
	event.preventDefault();
	const oldEmail = document.getElementById("oldEmail").value;
	const newEmail = document.getElementById("email").value;
	console.log(oldEmail + " " + newEmail);
	if (oldEmail !== newEmail) {
		return Swal.fire({
			text: 'You will need to re-login with your updated email id',
			icon: 'info',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: 'Go Ahead'
		}).then((result) => {
			if (result.isConfirmed) {
				// User clicked "Go Ahead," so return true
				const form = document.getElementById("edit-form");
				form.submit();
				return true;
			} else {
				// User canceled, return false
				return false;
			}
		});
	} else {
		// Emails are the same, no need for confirmation, return true
		event.target.submit();
		return true;
	}
}


function validatePassword() {
	event.preventDefault();
	const currPass = document.getElementById("cpassword").value;
	const newPass = document.getElementById("npassword").value;
	const conPass = document.getElementById("ccpassword").value;

	if (currPass == newPass) {
		alert("New password cannot be current password");
		return false;
	}
	if (newPass != conPass) {
		alert("Passwords don't match");
		return false;
	}
	
	if (currPass!=newPass && newPass==conPass) {
		return Swal.fire({
			text: 'You will need to re-login with your updated password',
			icon: 'info',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: 'Go Ahead'
		}).then((result) => {
			if (result.isConfirmed) {
				// User clicked "Go Ahead," so return true
				const form = document.getElementById("edit-pass");
				form.submit();
				return true;
			} else {
				// User canceled, return false
				return false;
			}
		});
	} else {
		// Emails are the same, no need for confirmation, return true
		event.target.submit();
		return true;
	}
}



window.addEventListener("load", phoneChange);
export { phoneAuth, codeverify, render, phoneChange, validateEmail, validatePassword};