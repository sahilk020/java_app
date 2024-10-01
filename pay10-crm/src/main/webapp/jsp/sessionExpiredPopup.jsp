<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Session Expiry Popup</title>

    <!-- Include SweetAlert library -->
    <link rel="stylesheet" type="text/css" href="path/to/sweetalert2.min.css">
    <script type="text/javascript" src="path/to/sweetalert2.all.min.js"></script>

    <!-- JavaScript for pop-up -->
    <script type="text/javascript">
        // Function to show a pop-up when the page loads
        function showSessionExpiryPopup() {
            Swal.fire({
                icon: 'warning',
                title: 'Session Expiry Warning',
                html: 'Your session has expired. Please choose an action:' +
                    '<br>' +
                    '<button onclick="restartSession()">Restart Session</button>' +
                    '<button onclick="acknowledgeExpiry()">Acknowledge Expiry</button>',
                showConfirmButton: false,
                allowOutsideClick: false
            });
        }

        // Function to restart the session
        function restartSession() {
            // Add logic here to restart the session if needed
            alert('Session restarted!');
            // You can redirect to the login page or perform other actions
        }

        // Function to acknowledge the session expiry
        function acknowledgeExpiry() {
            // Add logic here to handle acknowledging session expiry if needed
            alert('Session expiry acknowledged!');
            // You can redirect to the login page or perform other actions
        }

        // Call the function when the page loads
        window.onload = function() {
            showSessionExpiryPopup();
        };
    </script>
</head>
<body>

    <!-- Your other HTML content -->

</body>
</html>
