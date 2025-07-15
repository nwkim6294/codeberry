//frontend/assets/js/index_login.js
document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("login-form");
    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const email  = document.getElementById("email").value;
            const password = document.getElementById("password").value;
            const errorDiv = document.getElementById("login-error");
            errorDiv.style.display = "none";
            try {
                const resp = await fetch("/api/auth/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password }),
                });
                if (resp.ok) {
                    window.location.href = "home.html"; // 성공시 페이지 이동
                } else {
                    const { detail } = await resp.json();
                    errorDiv.innerText = detail || "로그인 실패. 아이디/비밀번호 확인";
                    errorDiv.style.display = "block";
                }
            } catch (err) {
                errorDiv.innerText = "서버 오류! 잠시 후 다시 시도해주세요.";
                errorDiv.style.display = "block";
            }
        });
    }
});