// frontend/assets/js/new_blog.js
document.addEventListener("DOMContentLoaded", async () => {
  const logoutBtn = document.getElementById("logout-btn");
  const greeting  = document.getElementById("greeting");
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
      console.error("[new_blog.js] /api/auth/me 응답 실패", res.status, await res.text());
      throw new Error(`HTTP ${res.status}`);
    }
    const user = await res.json();
    greeting.innerText = `안녕하세요! ${user.username}님`;
  } catch (err) {
    console.error("[new_blog.js] 프로필 정보를 불러오지 못했습니다.", err);
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
        console.error("[new_blog.js] 로그아웃 실패", res.status, await res.text());
        throw new Error(`Logout failed: ${res.status}`);
      }

      // 성공 시 alert 띄우고 리다이렉트
      alert("로그아웃 되었습니다.");
      window.location.href = "index.html";

    } catch (err) {
      console.error("[new_blog.js] 로그아웃 중 오류 발생", err);
      alert("로그아웃에 실패했습니다. 잠시 후 다시 시도해주세요.");
      logoutBtn.disabled  = false;
      logoutBtn.innerText = originalText;
    }
  });
 // ─── 새 글 작성 & 이미지 업로드 ───────────────────────────────────────────
  const form       = document.getElementById("new-article-form");
  const inputFile  = document.getElementById("image-input");
  const container = document.getElementById("preview-container");

  // 이미지 미리보기
  inputFile.addEventListener("change", () => {
    const files = Array.from(inputFile.files);

    // 1) 기존에 있던 미리보기 초기화
    container.innerHTML = "";

    if (files.length > 0) {
      //컨테이너 보이기
      files.forEach(file => {
        const box = document.createElement("div");
        box.classList.add("image-preview");  // CSS 규칙 그대로 적용
        box.style.display = "inline-block";  // 또는 "block"/"flex"
        
        const img = document.createElement("img");
        img.src = URL.createObjectURL(file);
        img.alt = "이미지 미리보기";

        box.appendChild(img);
        container.appendChild(box);
      });
    }
  });

  // 폼 제출 처리
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    // 1) 폼 값 추출
    const title   = form.title.value.trim();
    const content = form.content.value.trim();
    const tags    = form.tags.value.trim();
    const files   = inputFile.files;

    if (!title || !content) {
      return alert("제목과 본문은 필수 입력입니다.");
    }

    try {
      // 2) 글 메타데이터 저장
      const res1 = await fetch("/api/blog/articles", {
        method:      "POST",
        headers:     { "Content-Type": "application/json" },
        credentials: "include",
        body:        JSON.stringify({ title, content, tags })
      });
      if (!res1.ok) {
        const errText = await res1.text();
        throw new Error(`글 작성 실패 (${res1.status}): ${errText}`);
      }
      const article = await res1.json();

      // 3) 이미지 업로드 (파일이 하나라도 있으면)
      if (files.length > 0) {
        const formData = new FormData();
        for (let file of files) {
          formData.append("files", file);
        }
        const res2 = await fetch(
          `/api/blog/articles/${article.id}/upload-images`,
          {
            method:      "POST",
            credentials: "include",
            body:        formData
          }
        );
        if (!res2.ok) {
          console.warn(
            `[blog.js] 이미지 업로드 일부 실패 (${res2.status}):`,
            await res2.text()
          );
        }
      }

      // 4) 완료 알림 및 리다이렉트
      alert("글이 성공적으로 등록되었습니다!");
      window.location.href = "blog.html";

    } catch (err) {
      console.error("[blog.js] 글 작성 중 오류 발생:", err);
      alert(err.message || "글 작성 중 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.");
    }
  });
});
