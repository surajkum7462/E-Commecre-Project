/*$(function() {
	var $userRegister = $("#userRegister");

	$.validator.addMethod('lettersonly', function(value, element) {
		   return /^[A-Za-z\s]+$/.test(value);
	   }, 'Only letters and spaces are allowed.');
	
	
	$userRegister.validate({
		rules: {
			name: {
				required: true,
				lettersonly:true
			}

		},
		messages: {
			name: {
				required: 'Name is required',
				lettersonly:'Invalid Name'
			}
		}

	})
})

*/


/*$(document).ready(function() {
	// User Registration Form 
	$.validator.addMethod("lettersOnly", function(value, element) {
		return this.optional(element) || /^[A-Za-z\s]+$/.test(value);
	}, "Only letters and spaces are allowed.");

	$.validator.addMethod("numbersOnly", function(value, element) {
		return this.optional(element) || /^[0-9]+$/.test(value);
	}, "Only numbers are allowed.");

	$.validator.addMethod("strongPassword", function(value, element) {
		return this.optional(element) || /^(?=.*[A-Z])(?=.*\d).{6,}$/.test(value);
	}, "Password must be at least 6 characters, include 1 uppercase letter and 1 number.");


	$.validator.addMethod("passwordMatch", function(value, element) {
		var form = $(element).closest("form"); // Get the closest form
		var passwordField = form.find("input[name='password'], input[name='newPassword']").val();
		return value === passwordField;
	}, "Passwords do not match.");


	$.validator.addMethod("validPrice", function(value, element) {
		return this.optional(element) || /^\d+(\.\d{1,2})?$/.test(value);
	}, "Enter a valid price (positive number, max 2 decimal places).");

	$.validator.addMethod("maxFileSize", function(value, element) {
		if (element.files.length === 0) return true; // Skip validation if no file is selected
		return element.files[0].size <= 5 * 1024 * 1024; // 5MB size limit
	}, "File size must not exceed 5MB.");

	
	$.validator.addMethod("passwordMatch", function(value, element) {
		var form = $(element).closest("form"); // Find the closest form
		var newPassword = form.find("input[name='newPassword']").val(); // Get the new password
		return value === newPassword; // Return true if they match
	}, "Passwords do not match.");

	$.validator.addMethod("strongPassword", function(value, element) {
		return this.optional(element) || /^(?=.*[A-Z])(?=.*\d).{6,}$/.test(value);
	}, "Password must contain at least 1 uppercase letter, 1 number, and be at least 6 characters long.");
	
	
	
	
	// Form validation rules
	$("#userRegister").validate({
		rules: {
			name: {
				required: true,
				lettersOnly: true
			},
			mobileNumber: {
				required: true,
				numbersOnly: true,
				minlength: 10,
				maxlength: 10
			},
			email: {
				required: true,
				email: true
			},
			address: "required",
			city: "required",
			state: "required",
			country: "required",
			pincode: {
				required: true,
				numbersOnly: true
			},
			password: {
				required: true,
				strongPassword: true
			},
			confirm_password: {
				required: true,
				passwordMatch: true
			},
			img: "required"
		},
		messages: {
			name: "Name doesn't contain number",
			mobileNumber: "Please enter a valid 10-digit mobile number",
			email: "Please enter a valid email",
			password: "Password must contain 1 uppercase, 1 number, and be at least 6 characters",
			confirm_password: "Passwords do not match",
			img: "Please upload a profile image"
		}
	});

	// For reset password


	$("#resetPassword").validate({
		rules: {
			password: {
				required: true,
				strongPassword: true
			},
			confirm_password: {
				required: true,
				equalTo: "#resetPassword input[name='password']"
			}


			password: {
				required: true,
				strongPassword: true
			},
			confirm_password: {
				required: true,
				passwordMatch: true
			}
		},
		messages: {
			password: "Password must contain 1 uppercase, 1 number, and be at least 6 characters",
			confirm_password: "Passwords do not match"
		}
	});


	// For orders validation


	$("#orders").validate({
		rules: {
			firstName: {
				required: true,
				lettersOnly: true
			},
			lastName: {
				required: true,
				lettersOnly: true
			},
			email: {
				required: true,
				email: true
			},
			mobileNo: {
				required: true,
				numbersOnly: true,
				minlength: 10,
				maxlength: 10
			},
			address: {
				required: true
			},
			city: {
				required: true,
				lettersOnly: true
			},
			state: {
				required: true,
				lettersOnly: true
			},
			pincode: {
				required: true,
				numbersOnly: true,
				minlength: 6,
				maxlength: 6
			},
			paymentType: {
				required: true,
				lettersOnly: true,
			},

		},
		messages: {
			firstName: "Please enter a valid first name",
			lastName: "Please enter a valid last name",
			email: "Please enter a valid email address",
			mobileNo: "Please enter a valid 10-digit mobile number",
			address: "Address is required",
			city: "Please enter a valid city name",
			state: "Please enter a valid state name",
			pincode: "Please enter a valid 6-digit pincode",
			paymentType: "Please select a payment method"
		},
		errorPlacement: function(error, element) {
			error.addClass("text-danger");
			error.insertAfter(element);
		},
		highlight: function(element) {
			$(element).addClass("is-invalid");
		},
		unhighlight: function(element) {
			$(element).removeClass("is-invalid");
		}
	});


	// For Admin Add Products

	$("#addProduct").validate({
		rules: {
			title: {
				required: true,
				minlength: 3
			},
			description: {
				required: true,
				minlength: 10
			},
			category: {
				required: true
			},
			price: {
				required: true,
				validPrice: true
			},
			stock: {
				required: true,
				numbersOnly: true
			},
			isActive: {
				required: true
			},
			file: {
				required: true,
				maxFileSize: true
			}
		},
		messages: {
			title: "Title must be at least 3 characters long.",
			description: "Description must be at least 10 characters long.",
			category: "Please select a valid category.",
			price: "Enter a valid price (e.g., 49.99).",
			stock: "Enter a valid stock quantity (only numbers).",
			isActive: "Please select the product status.",
			file: "Please upload a product image (Max 5MB)."
		},
		errorPlacement: function(error, element) {
			error.addClass("text-danger");
			error.insertAfter(element);
		},
		highlight: function(element) {
			$(element).addClass("is-invalid");
		},
		unhighlight: function(element) {
			$(element).removeClass("is-invalid");
		}
	});

	// For Changing the profile password




	$("#profilePassword").validate({
		rules: {
			currentPassword: {
				required: true
			},
			newPassword: {
				required: true,
				strongPassword: true
			},
			confirmPassword: {
				required: true,
				equalTo: "input[name='newPassword']"  // Fixed selector
			}
			newPassword: {
				required: true,
				strongPassword: true
			},
			confirmPassword: {
				required: true,
				passwordMatch: true
			}
		},
		messages: {
			currentPassword: "Please enter your current password.",
			newPassword: "Password must contain 1 uppercase letter, 1 number, and be at least 6 characters.",
			confirmPassword: "Passwords do not match."
		},
		errorPlacement: function(error, element) {
			error.addClass("text-danger");
			error.insertAfter(element);
		},
		highlight: function(element) {
			$(element).addClass("is-invalid");
		},
		unhighlight: function(element) {
			$(element).removeClass("is-invalid");
		}
	});
});

*/


$(document).ready(function() {
	// Add validation methods
	$.validator.addMethod("lettersOnly", function(value, element) {
		return this.optional(element) || /^[A-Za-z\s]+$/.test(value);
	}, "Only letters and spaces are allowed.");

	$.validator.addMethod("numbersOnly", function(value, element) {
		return this.optional(element) || /^[0-9]+$/.test(value);
	}, "Only numbers are allowed.");

	$.validator.addMethod("strongPassword", function(value, element) {
		return this.optional(element) || /^(?=.*[A-Z])(?=.*\d).{6,}$/.test(value);
	}, "Password must be at least 6 characters, include 1 uppercase letter and 1 number.");

	$.validator.addMethod("passwordMatch", function(value, element) {
		var newPassword = $(element).closest("form").find("input[name='newPassword'], input[name='password']").val();
		return value === newPassword;
	}, "Passwords do not match.");

	$.validator.addMethod("validPrice", function(value, element) {
		return this.optional(element) || /^\d+(\.\d{1,2})?$/.test(value);
	}, "Enter a valid price (positive number, max 2 decimal places).");

	$.validator.addMethod("maxFileSize", function(value, element) {
		if (element.files.length === 0) return true;
		return element.files[0].size <= 5 * 1024 * 1024; // 5MB limit
	}, "File size must not exceed 5MB.");

	// **User Registration Form**
	$("#userRegister").validate({
		rules: {
			name: { required: true, lettersOnly: true },
			mobileNumber: { required: true, numbersOnly: true, minlength: 10, maxlength: 10 },
			email: { required: true, email: true },
			address: "required",
			city: "required",
			state: "required",
			country: "required",
			pincode: { required: true, numbersOnly: true },
			password: { required: true, strongPassword: true },
			confirm_password: { required: true, passwordMatch: true },
			img: "required"
		},
		messages: {
			name: "Name must contain only letters",
			mobileNumber: "Enter a valid 10-digit mobile number",
			email: "Enter a valid email",
			password: "Password must contain 1 uppercase letter, 1 number, and be at least 6 characters.",
			confirm_password: "Passwords do not match",
			img: "Upload a profile image"
		}
	});

	// **Reset Password**
	$("#resetPassword").validate({
		rules: {
			password: { required: true, strongPassword: true },
			confirm_password: { required: true, passwordMatch: true }
		},
		messages: {
			password: "Password must contain 1 uppercase letter, 1 number, and be at least 6 characters.",
			confirm_password: "Passwords do not match."
		}
	});

	// **Orders Validation**
	$("#orders").validate({
		rules: {
			firstName: { required: true, lettersOnly: true },
			lastName: { required: true, lettersOnly: true },
			email: { required: true, email: true },
			mobileNo: { required: true, numbersOnly: true, minlength: 10, maxlength: 10 },
			address: { required: true },
			city: { required: true, lettersOnly: true },
			state: { required: true, lettersOnly: true },
			pincode: { required: true, numbersOnly: true, minlength: 6, maxlength: 6 },
			paymentType: { required: true, lettersOnly: true }
		},
		messages: {
			firstName: "Enter a valid first name",
			lastName: "Enter a valid last name",
			email: "Enter a valid email",
			mobileNo: "Enter a valid 10-digit mobile number",
			address: "Address is required",
			city: "Enter a valid city name",
			state: "Enter a valid state name",
			pincode: "Enter a valid 6-digit pincode",
			paymentType: "Select a payment method"
		}
	});

	// **Admin Add Products**
	$("#addProduct").validate({
		rules: {
			title: { required: true, minlength: 3 },
			description: { required: true, minlength: 10 },
			category: { required: true },
			price: { required: true, validPrice: true },
			stock: { required: true, numbersOnly: true },
			isActive: { required: true },
			file: { required: true, maxFileSize: true }
		},
		messages: {
			title: "Title must be at least 3 characters long.",
			description: "Description must be at least 10 characters long.",
			category: "Select a valid category.",
			price: "Enter a valid price (e.g., 49.99).",
			stock: "Enter a valid stock quantity (only numbers).",
			isActive: "Select the product status.",
			file: "Upload a product image (Max 5MB)."
		}
	});

	// **Profile Password Change**
	$("#profilePassword").validate({
		rules: {
			currentPassword: { required: true },
			newPassword: { required: true, strongPassword: true },
			confirmPassword: { required: true, passwordMatch: true }
		},
		messages: {
			currentPassword: "Enter your current password.",
			newPassword: "Password must contain 1 uppercase letter, 1 number, and be at least 6 characters.",
			confirmPassword: "Passwords do not match."
		}
	});
});





// Smooth Scroll for Links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({ behavior: 'smooth' });
        }
    });
});

// Animation on Scroll
window.addEventListener("scroll", function () {
    let aboutSection = document.querySelector(".about");
    let position = aboutSection.getBoundingClientRect().top;
    let windowHeight = window.innerHeight / 1.5;

    if (position < windowHeight) {
        aboutSection.classList.add("visible");
    }
});

function updateDropdownText(element) {
    var category = element.getAttribute("data-category");
    document.getElementById("categoryDropdownButton").innerText = category;
}






















