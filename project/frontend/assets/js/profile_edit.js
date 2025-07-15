// frontend/assets/js/profile_edit.js
document.addEventListener("DOMContentLoaded", () => {
  // ————————————————— 탭 전환 로직 —————————————————
  const tabs     = document.querySelectorAll(".tab-btn");
  const contents = document.querySelectorAll(".tab-content");
  tabs.forEach(tab => {
    tab.addEventListener("click", () => {
      tabs.forEach(t => t.classList.remove("active"));
      tab.classList.add("active");
      const target = tab.dataset.target;
      contents.forEach(c => {
        if (c.id === target) c.classList.add("active");
        else c.classList.remove("active");
      });
    });
  });
  // 기본 탭
  document.getElementById("profile-form").classList.add("active");

  // ————————————————— 공통 요소 —————————————————
  const logoutBtn     = document.querySelector(".logout-btn");
  const greetingEl    = document.querySelector(".greeting");

  // ————————————————— 프로필 요소 —————————————————
  const formProfile   = document.getElementById("profile-form");
  const fileInput     = document.getElementById("profile_image");
  const imgPreview    = document.getElementById("profile-img");
  const usernameInput = document.getElementById("username");
  const bioInput      = document.getElementById("bio");
  const cancelBtns    = document.querySelectorAll(".btn.cancel");

  // 미리보기
  fileInput.addEventListener("change", () => {
    const f = fileInput.files[0];
    if (f) imgPreview.src = URL.createObjectURL(f);
  });

// 초기 데이터 로드
fetch("/api/auth/me", { credentials: "include" })
  .then(res => {
    if (res.status === 401) {
      alert("로그인이 필요합니다.");
      window.location.href = "index.html";
      throw new Error("Unauthorized");
    }
    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`);
    }
    return res.json();
  })
  .then(user => {
    greetingEl.innerText   = `안녕하세요! ${user.username}님`;
    imgPreview.src         = user.profile_image;
    usernameInput.value    = user.username;
    bioInput.value         = user.bio || "";
  })
  .catch(err => {
    if (err.message !== "Unauthorized") {
      alert("프로필 정보를 불러올 수 없습니다.");
      console.error(err);
    }
  });

// 프로필 폼 제출
formProfile.addEventListener("submit", async e => {
  e.preventDefault();

  try {
    // 사진 업로드
    if (fileInput.files[0]) {
      const formData = new FormData();
      formData.append("file", fileInput.files[0]);
      const resImg = await fetch("/api/users/me/upload_image", {
        method:      "POST",
        body:        formData,
        credentials: "include"
      });
      if (resImg.status === 401) {
        alert("로그인이 필요합니다.");
        window.location.href = "index.html";
        return;
      }
      if (!resImg.ok) {
        alert("사진 업로드에 실패했습니다.");
        return;
      }
    }

    // 닉네임·bio 업데이트
    const payload = {
      username: usernameInput.value.trim(),
      bio:      bioInput.value.trim()
    };
    const resInfo = await fetch("/api/users/me", {
      method:      "PATCH",
      headers:     { "Content-Type": "application/json" },
      body:        JSON.stringify(payload),
      credentials: "include"
    });
    if (resInfo.status === 401) {
      alert("로그인이 필요합니다.");
      window.location.href = "index.html";
      return;
    }
    if (resInfo.ok) {
      alert("프로필 정보가 저장되었습니다.");
    } else {
      alert("프로필 저장에 실패했습니다.");
    }
  } catch (err) {
    console.error(err);
    alert("프로필 저장 중 오류가 발생했습니다.");
  }
});


  // ————————————————— 비밀번호 요소 —————————————————
  const formPwd    = document.getElementById("password-form");
  const currentPw  = document.getElementById("current_password");
  const newPw      = document.getElementById("new_password");
  const confirmPw  = document.getElementById("new_password_confirm");

  formPwd.addEventListener("submit", async e => {
    e.preventDefault();
    if (newPw.value !== confirmPw.value) {
      alert("새 비밀번호가 일치하지 않습니다.");
      return;
    }
    const body = {
      current_password: currentPw.value,
      new_password:     newPw.value
    };
    // 백엔드 엔드포인트 이름에 오타가 있으니 그대로 chage_password 사용
    const res = await fetch("/api/auth/chage_password", {
      method:      "POST",
      headers:     { "Content-Type": "application/json" },
      body:        JSON.stringify(body),
      credentials: "include"
    });
    if (res.status === 204) {
      alert("비밀번호가 변경되었습니다. 다시 로그인해주세요.");
      window.location.href = "index.html";
    } else {
      const err = await res.json();
      alert("변경 실패: " + (err.detail || res.status));
    }
  });

  // ————————————————— 로그아웃 & 취소 —————————————————
  logoutBtn.addEventListener("click", async () => {
    await fetch("/api/auth/logout", { method: "POST", credentials: "include" });
    alert("로그아웃 되었습니다.");
    window.location.href = "index.html";
  });

  cancelBtns.forEach(btn => {
    btn.addEventListener("click", () => window.location.href = "home.html");
  });
});
