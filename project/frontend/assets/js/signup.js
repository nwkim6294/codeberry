// frontend/assets/js/signup.js
document.addEventListener("DOMContentLoaded", () => {
  const form      = document.getElementById("signup-form");
  const submitBtn = document.getElementById("submit-btn");
  const errorEl   = document.getElementById("signup-error");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    errorEl.style.display = "none";
    submitBtn.disabled    = true;
    submitBtn.innerText   = "처리 중...";

    // 비밀번호 확인
    const pwd  = form.password.value;
    const pwd2 = form.passwordConfirm.value;
    if (pwd !== pwd2) {
      errorEl.innerText     = "비밀번호가 일치하지 않습니다.";
      errorEl.style.display = "block";
      submitBtn.disabled    = false;
      submitBtn.innerText   = "회원가입";
      return;
    }

    try {
      // 회원가입 요청
      const payload = {
        username: form.username.value.trim(),
        email:    form.email.value.trim(),
        password: pwd
      };
      const resp = await fetch("/api/auth/signup", {
        method:      "POST",
        headers:     { "Content-Type": "application/json" },
        body:        JSON.stringify(payload),
        credentials: "include"
      });

      if (!resp.ok) {
        const err = await resp.json();
        throw new Error(err.detail || "회원가입에 실패했습니다.");
      }

      alert("회원가입 완료! 로그인 페이지로 이동합니다.");
      window.location.href = "/index.html";

    } catch (err) {
      errorEl.innerText     = err.message;
      errorEl.style.display = "block";
      console.error(err);
    } finally {
      submitBtn.disabled  = false;
      submitBtn.innerText = "회원가입";
    }
  });
});
