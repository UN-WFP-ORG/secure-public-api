<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Send OTP</title>
    <script src="https://www.google.com/recaptcha/api.js?render=${siteKey}"></script>
</head>
<body>
<h1>Send OTP</h1>

<div id="successMessage" style="color: green; display: none;"></div>
<div id="errorMessage" style="color: red; display: none;"></div>

<!-- Form to send OTP -->
<form id="otpForm">
    <label for="mobileNumber">Mobile Number:</label>
    <input type="text" id="mobileNumber" name="mobileNumber" required pattern="[0-9]{10,15}" placeholder="Enter Mobile Number">

    <label for="nid">NID:</label>
    <input type="text" id="nid" name="nid" required pattern="[a-zA-Z0-9]{10,17}" placeholder="Enter NID">

    <label for="dob">Date of Birth:</label>
    <input type="text" id="dob" name="dob" required>

    <button type="submit">Send OTP</button>
</form>

<!-- OTP Validation Form -->
<form id="otpValidationForm" style="display: none;">
    <label for="otp">Enter OTP:</label>
    <input type="text" id="otp" name="otp" required pattern="[0-9]{6}" placeholder="Enter OTP">

    <button type="submit">Validate OTP</button>
</form>

<!-- Container to display backend response -->
<div id="responseContainer" style="display: none; margin-top: 10px;"></div>

<script>
    const siteKey = '${siteKey}';

    // Send OTP handler
    document.getElementById('otpForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        const mobileNumber = document.getElementById('mobileNumber').value.trim();
        const nid = document.getElementById('nid').value.trim();
        const dob = document.getElementById('dob').value.trim();

        const token = await grecaptcha.execute(siteKey, { action: 'submit' });
        if (!token) {
            showError("CAPTCHA verification failed.");
            return;
        }

        const payload = { mobileNumber, nid, dob, captchaToken: token };

        try {
            const response = await fetch('/secure-public/otp/sendOtp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });

            if (response.ok) {
                showSuccess("OTP sent successfully!");
                document.getElementById('otpValidationForm').style.display = 'block';
            } else {
                showError("Failed to send OTP.");
            }
        } catch (error) {
            showError("An error occurred while sending OTP.");
        }
    });

    // Validate OTP handler
    document.getElementById('otpValidationForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        const otp = document.getElementById('otp').value.trim();
        const mobileNumber = document.getElementById('mobileNumber').value.trim();
        const nid = document.getElementById('nid').value.trim();
        const dob = document.getElementById('dob').value.trim();

        const payload = { mobileNumber, otp, nid, dob };

        try {
            const response = await fetch('/secure-public/otp/validateOtp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });

            const responseContainer = document.getElementById('responseContainer');
            if (response.ok) {
                const backendResponse = await response.json();
                responseContainer.textContent = `Validation Successful: ${JSON.stringify(backendResponse)}`;
                responseContainer.style.display = 'block';
            } else {
                const errorMessage = await response.text();
                showError(errorMessage || "OTP validation failed.");
            }
        } catch (error) {
            showError("An error occurred during OTP validation.");
        }
    });

    function showSuccess(message) {
        const successMessage = document.getElementById('successMessage');
        const errorMessage = document.getElementById('errorMessage');

        successMessage.textContent = message;
        successMessage.style.display = 'block';
        errorMessage.style.display = 'none';
    }

    function showError(message) {
        const successMessage = document.getElementById('successMessage');
        const errorMessage = document.getElementById('errorMessage');

        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
        successMessage.style.display = 'none';
    }
</script>
</body>
</html>
