// frontend/assets/js/home.js
document.addEventListener("DOMContentLoaded", async () => {
  const logoutBtn      = document.getElementById("logout-btn");
  const greeting       = document.getElementById("greeting");
  const nameEl         = document.getElementById("profile-name");
  const bioEl          = document.getElementById("profile-bio");
  const imgEl          = document.getElementById("profile-img");
  const statDiary      = document.getElementById("stat-diary");
  const recentBlogList = document.getElementById("recent-blog-list");
  const todayDateEl    = document.getElementById("today-date");
  const tagContainer   = document.getElementById("tag-list");

  let currentUser;

  // 0) 프로필 정보 로드
  try {
    const res = await fetch("/api/auth/me", { credentials: "include" });
    if (res.status === 401) {
      alert("로그인이 필요합니다.");
      return window.location.href = "index.html";
    }
    currentUser = await res.json();
    greeting.innerText = `안녕하세요! ${currentUser.username}님`;
    nameEl.innerText   = `${currentUser.username}님`;
    bioEl.innerText    = currentUser.bio || "소개가 없습니다.";
    imgEl.src          = currentUser.profile_image;
  } catch (err) {
    console.error("[home.js] 프로필 정보를 불러오지 못했습니다.", err);
  }

  // 오늘 날짜 표시
  {
    const today = new Date();
    todayDateEl.innerText = `${today.getFullYear()}년 ${today.getMonth()+1}월 ${today.getDate()}일`;
  }

  // 1) 블로그 통계 & 최근 3개 글
  try {
    const res2 = await fetch(
      `/api/blog/articles?owner_id=${currentUser.id}&page=1&size=3`,
      { credentials: "include" }
    );
    if (!res2.ok) throw new Error(`블로그 API 에러 ${res2.status}`);
    const { total, items } = await res2.json();

    statDiary.innerText = total;

    if (items.length > 0) {
      recentBlogList.innerHTML = items.map(post => {
        const d = new Date(post.create_at);
        const dateStr = `${d.getFullYear()}.${d.getMonth()+1}.${d.getDate()}`;
        return `
          <li class="recent-post">
            <a href="blog.html?id=${post.id}">
              <span class="rp-title">${post.title}</span>
              <span class="rp-date">${dateStr}</span>
            </a>
          </li>
        `;
      }).join("");
    } else {
      recentBlogList.innerHTML = `<li>아직 작성된 글이 없습니다.</li>`;
    }
  } catch (err) {
    console.error("[home.js] 블로그 데이터 로드 실패:", err);
    recentBlogList.innerHTML = `<li>불러오는 중 오류가 발생했습니다.</li>`;
  }

  // 2) 태그 클라우드 생성 (최근 50개 글에서 추출)
  try {
    const resAll = await fetch(
      `/api/blog/articles?owner_id=${currentUser.id}&page=1&size=50`,
      { credentials: "include" }
    );
    if (!resAll.ok) throw new Error(`태그용 글 목록 조회 실패 (${resAll.status})`);
    const { items: allItems } = await resAll.json();

    const tagSet = new Set();
    allItems.forEach(item => {
      if (item.tags) {
        item.tags.split(",").forEach(t => tagSet.add(t.trim()));
      }
    });
    const tags = Array.from(tagSet);

    tagContainer.innerHTML = tags
      .map(tag => `<button class="tag-btn" data-tag="${tag}">#${tag}</button>`)
      .join("");

    tagContainer.querySelectorAll(".tag-btn").forEach(btn => {
      btn.addEventListener("click", () => {
        const t = encodeURIComponent(btn.dataset.tag);
        window.location.href = `blog.html?tag=${t}#tag-${t}`;
      });
    });
  } catch (e) {
    console.error("[home.js] 태그 로드 실패:", e);
  }

  // 3) 로그아웃
  logoutBtn.addEventListener("click", async () => {
    logoutBtn.disabled  = true;
    const originalText  = logoutBtn.innerText;
    logoutBtn.innerText = "로그아웃 중…";

    try {
      const res = await fetch("/api/auth/logout", {
        method:      "POST",
        credentials: "include"
      });
      if (!res.ok) throw new Error(`Logout failed (${res.status})`);
      alert("로그아웃 되었습니다.");
      window.location.href = "index.html";
    } catch (err) {
      console.error("[home.js] 로그아웃 중 오류 발생", err);
      alert("로그아웃에 실패했습니다.");
      logoutBtn.disabled  = false;
      logoutBtn.innerText = originalText;
    }
  });
});
