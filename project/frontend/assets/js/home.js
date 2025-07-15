// frontend/assets/js/home.js
document.addEventListener("DOMContentLoaded", async () => {
  const logoutBtn = document.getElementById("logout-btn");
  const greeting  = document.getElementById("greeting");
  const nameEl    = document.getElementById("profile-name");
  const bioEl     = document.getElementById("profile-bio");
  const imgEl     = document.getElementById("profile-img");

  // 프로필 정보 로드
  try {
    const res = await fetch("/api/auth/me", {
      credentials: "include"
    });
    if (res.status === 401) {
      alert("로그인이 필요합니다.");
      // 인증되지 않은 상태면 로그인 페이지로 이동
      window.location.href = "index.html";
      return;
    }
    if (!res.ok) {
      console.error("[home.js] /api/auth/me 응답 실패", res.status, await res.text());
      throw new Error(`HTTP ${res.status}`);
    }
    const user = await res.json();
    greeting.innerText    = `안녕하세요! ${user.username}님`;
    nameEl.innerText      = `${user.username}님`;
    bioEl.innerText       = user.bio || "소개가 없습니다.";
    imgEl.src             = user.profile_image;
  } catch (err) {
    console.error("[home.js] 프로필 정보를 불러오지 못했습니다.", err);
    // 필요한 경우 UI에 표시
    // greeting.innerText = "프로필 정보 로드 실패";
  }

  // 로그아웃 구현
  logoutBtn.addEventListener("click", async () => {
    logoutBtn.disabled    = true;
    const originalText    = logoutBtn.innerText;
    logoutBtn.innerText   = "로그아웃 중…";

    try {
      const res = await fetch("/api/auth/logout", {
        method:      "POST",
        credentials: "include"
      });
      if (!res.ok) {
        console.error("[home.js] 로그아웃 실패", res.status, await res.text());
        throw new Error(`Logout failed: ${res.status}`);
      }

      // 성공 시 alert 띄우고 리다이렉트
      alert("로그아웃 되었습니다.");
      window.location.href = "index.html";

    } catch (err) {
      console.error("[home.js] 로그아웃 중 오류 발생", err);
      alert("로그아웃에 실패했습니다. 잠시 후 다시 시도해주세요.");
      logoutBtn.disabled  = false;
      logoutBtn.innerText = originalText;
    }
  });
});
