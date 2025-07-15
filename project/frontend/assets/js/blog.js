// frontend/assets/js/blog.js
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
      console.error("[blog.js] /api/auth/me 응답 실패", res.status, await res.text());
      throw new Error(`HTTP ${res.status}`);
    }
    const user = await res.json();
    greeting.innerText = `안녕하세요! ${user.username}님`;
  } catch (err) {
    console.error("[blog.js] 프로필 정보를 불러오지 못했습니다.", err);
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
        console.error("[blog.js] 로그아웃 실패", res.status, await res.text());
        throw new Error(`Logout failed: ${res.status}`);
      }

      // 성공 시 alert 띄우고 리다이렉트
      alert("로그아웃 되었습니다.");
      window.location.href = "index.html";

    } catch (err) {
      console.error("[blog.js] 로그아웃 중 오류 발생", err);
      alert("로그아웃에 실패했습니다. 잠시 후 다시 시도해주세요.");
      logoutBtn.disabled  = false;
      logoutBtn.innerText = originalText;
    }
  });

// 1) API 호출해서 글 목록 가져오기
  let page = 1, size = 20;
  try {
    const res = await fetch(`/api/blog/articles?page=${page}&size=${size}`, {
      credentials: "include"
    });
    if (!res.ok) throw new Error(`목록 조회 실패 (${res.status})`);
    const { items } = await res.json();

    const listEl = document.querySelector(".entries-list");
    listEl.innerHTML = "";

    items.forEach(item => {
      const { id, title, create_at, content, tags, author_username, image_urls } = item;

      // 카드 루트
      const card = document.createElement("div");
      card.className = "entry-card";

      // 1) 헤더
      const header = document.createElement("div");
      header.className = "card-header";
      header.innerHTML = `
        <span class="card-title">${title}</span>
        <span class="card-datetime">${new Date(create_at).toLocaleString()}</span>
      `;
      card.appendChild(header);

      // 2) 이미지 갤러리
      if (image_urls && image_urls.length) {
        const gallery = document.createElement("div");
        gallery.className = "img-gallery";
        image_urls.forEach(src => {
          const img = document.createElement("img");
          img.src = src;
          img.alt = title;
          gallery.appendChild(img);
        });
        card.appendChild(gallery);
      }

      // 3) 본문
      const contentEl = document.createElement("div");
      contentEl.className = "card-content";
      contentEl.textContent = content;
      card.appendChild(contentEl);

      // 4) 태그
      if (tags) {
        const tagsEl = document.createElement("div");
        tagsEl.className = "card-tags";
        tags.split(",").forEach(t => {
          const span = document.createElement("span");
          span.textContent = `#${t.trim()}`;
          tagsEl.appendChild(span);
        });
        card.appendChild(tagsEl);
      }

      // 5) 액션 (수정/삭제) 버튼
      const actions = document.createElement("div");
      actions.className = "card-actions";
      actions.innerHTML = `
        <button data-id="${id}" class="edit-btn">✏️ 수정</button>
        <button data-id="${id}" class="delete-btn">🗑️ 삭제</button>
      `;
      card.appendChild(actions);

      listEl.appendChild(card);
    });

  } catch (err) {
    console.error("[blog.js] 블로그 목록 로드 실패:", err);
    alert("블로그 목록을 불러오는 중 오류가 발생했습니다.");
  }
});