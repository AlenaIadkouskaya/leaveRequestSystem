const apiBaseUrl = "http://localhost:8787/api/users";

document.getElementById("registerForm").addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const username = document.getElementById("username").value;
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch(`${apiBaseUrl}/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, username, firstName, lastName, password }),
        });

        if (response.ok) {
            alert("User registered successfully!");
        } else {
            const errorData = await response.json();
            alert(`Error: ${errorData.message || response.status}`);
        }
    } catch (error) {
        alert(`Error: ${error.message}`);
    }
});



