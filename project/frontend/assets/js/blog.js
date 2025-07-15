// frontend/assets/js/blog.js
document.addEventListener("DOMContentLoaded", async () => {
  const logoutBtn = document.getElementById("logout-btn");
  const greeting  = document.getElementById("greeting");

  let currentUserId;
  // 0) 프로필 정보 로드
  try {
    const res = await fetch("/api/auth/me", { credentials: "include" });
    if (res.status === 401) {
      alert("로그인이 필요합니다.");
      return window.location.href = "index.html";
    }
    if (!res.ok) throw new Error(`HTTP ${res.status}`);

    const { username, id } = await res.json();
    greeting.innerText = `안녕하세요! ${username}님`;
    currentUserId = id;  // 여기서 ID 저장

  } catch (err) {
    console.error("[blog.js] 프로필 로드 실패", err);
  }

  // 1) 로그아웃
  logoutBtn.addEventListener("click", async () => {
    logoutBtn.disabled = true;
    const orig = logoutBtn.innerText;
    logoutBtn.innerText = "로그아웃 중…";
    try {
      const r = await fetch("/api/auth/logout", { method: "POST", credentials: "include" });
      if (!r.ok) throw new Error(`Logout ${r.status}`);
      alert("로그아웃 되었습니다.");
      window.location.href = "index.html";
    } catch (e) {
      console.error("[blog.js] 로그아웃 실패", e);
      alert("로그아웃에 실패했습니다.");
      logoutBtn.disabled = false;
      logoutBtn.innerText = orig;
    }
  });

  // 2) 게시글 불러오기 & 렌더
  try {
    const res = await fetch(`/api/blog/articles?owner_id=${currentUserId}&page=1&size=20`, { credentials: "include" });
    if (!res.ok) throw new Error(`목록조회 ${res.status}`);
    const { items } = await res.json();
    const listEl = document.querySelector(".entries-list");
    listEl.innerHTML = "";

    items.forEach(({ id, title, create_at, content, tags = "", image_urls = [] }) => {
      const card = document.createElement("div");
      card.className = "entry-card";

      // header
      const hdr = document.createElement("div");
      hdr.className = "card-header";
      hdr.innerHTML = `
        <span class="card-title">${title}</span>
        <span class="card-datetime">${new Date(create_at).toLocaleString()}</span>
      `;
      card.appendChild(hdr);

      // images
      if (image_urls.length) {
        const g = document.createElement("div");
        g.className = "img-gallery";
        image_urls.forEach(src => {
          const img = document.createElement("img");
          img.src = src;
          img.alt = title;
          g.appendChild(img);
        });
        card.appendChild(g);
      }

      // content
      const c = document.createElement("div");
      c.className = "card-content";
      c.textContent = content;
      card.appendChild(c);

      // tags & anchor
      const tagsArr = tags.split(",").map(t => t.trim()).filter(Boolean);
      if (tagsArr.length) {
        const tagsEl = document.createElement("div");
        tagsEl.className = "card-tags";

        tagsArr.forEach(t => {
          const span = document.createElement("span");
          span.className = "post-tag";
          span.textContent = `#${t}`;
          tagsEl.appendChild(span);
        });

        // safeTag 생성 (첫 번째 태그 기준)
        const first = tagsArr[0];
        const safeTag = first.toLowerCase()
          .replace(/[^a-z0-9\s-]/g, "")
          .replace(/\s+/g, "-");
        card.id = `tag-${safeTag}`;

        // data-tags 속성에 전체 태그 저장 (찾기용)
        card.setAttribute("data-tags", tagsArr.join(" "));

        card.appendChild(tagsEl);
      }

      // 수정/삭제 버튼
      const act = document.createElement("div");
      act.className = "card-actions";
      act.innerHTML = `
        <button data-id="${id}" class="edit-btn">✏️ 수정</button>
        <button data-id="${id}" class="delete-btn">🗑️ 삭제</button>
      `;
      act.querySelector(".edit-btn").addEventListener("click", () => {
        window.location.href = `blog_edit.html?id=${id}`;
      });
      card.appendChild(act);

      listEl.appendChild(card);
    });

    // 3) 해시 앵커 스크롤 (렌더 완료 후 지연)
    const hash = window.location.hash;  // "#tag-your-tag"
    if (hash) {
      setTimeout(() => {
        // decodeURIComponent 로 실제 ID 문자열 얻기
        const id = decodeURIComponent(hash.slice(1)); // "tag-your-tag"
        // 1) ID가 있으면 getElementById, 없으면 data-tags 로 검색
        let el = document.getElementById(id);
        if (!el) {
          const targetTag = id.replace(/^tag-/, "");
          el = Array.from(document.querySelectorAll(".entry-card")).find(card => {
            const dt = card.getAttribute("data-tags") || "";
            return dt.split(" ").includes(targetTag);
          });
        }
        if (el) {
          el.scrollIntoView({ behavior: "smooth", block: "start" });
          el.classList.add("highlight");
          setTimeout(() => el.classList.remove("highlight"), 2000);
        } else {
          console.warn("스크롤 대상 없음:", id);
        }
      }, 150);
    }

  } catch (err) {
    console.error("[blog.js] 목록 로드 실패:", err);
    alert("블로그 목록을 불러오는 중 오류가 발생했습니다.");
  }
});
