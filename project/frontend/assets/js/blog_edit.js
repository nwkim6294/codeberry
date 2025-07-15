// frontend/assets/js/blog_edit.js
document.addEventListener("DOMContentLoaded", async () => {
  const logoutBtn = document.getElementById("logout-btn");
  const greeting  = document.getElementById("greeting");
  const form      = document.getElementById("edit-article-form");
  const inputFile = document.getElementById("image-input");
  const container = document.getElementById("preview-container");

  // URL에서 id 가져오기
  const params = new URLSearchParams(location.search);
  const articleId = params.get("id");
  console.log("⚙️ articleId:", articleId);  // <- 여기에 값이 나오는지 확인
  if (!articleId) {
    alert("잘못된 접근입니다.");
    window.location.href = "blog.html";
    return;
  }

  // 프로필 & 사용자 이름 표시 (new_blog.js와 동일)
  try {
    const res = await fetch("/api/auth/me", { credentials: "include" });
    if (res.status === 401) {
      alert("로그인이 필요합니다.");
      return window.location.href = "index.html";
    }
    const user = await res.json();
    greeting.innerText = `안녕하세요! ${user.username}님`;
  } catch {
    console.error("프로필 로드 실패");
  }
  logoutBtn.addEventListener("click", async () => {
    logoutBtn.disabled = true;
    const old = logoutBtn.innerText;
    logoutBtn.innerText = "로그아웃 중…";
    try {
      const r = await fetch("/api/auth/logout", { method:"POST", credentials:"include" });
      if (!r.ok) throw new Error();
      alert("로그아웃 되었습니다.");
      return window.location.href = "index.html";
    } catch {
      alert("로그아웃 실패");
      logoutBtn.disabled = false;
      logoutBtn.innerText = old;
    }
  });

  // 1) 기존 글 데이터 로드
  try {
    const res = await fetch(`/api/blog/articles/${articleId}`, { 
        credentials:"include" 
    });
    if (!res.ok) throw new Error(res.status);

    //응답 전체를 data에 담고,
    // 그 안에서 실제 article과 image_urls를 꺼냅니다.
    const data = await res.json();
    console.log("⚙️ data:", data);  // <- 여기서 전체 구조를 확인

    // 구조분해 할당
    const { article, image_urls } = data;

    form.title.value   = article.title;
    form.content.value = article.content;
    form.tags.value    = article.tags || "";

    // 기존 이미지 미리보기
    if (image_urls && image_urls.length) {
      image_urls.forEach(src => {
        const box = document.createElement("div");
        box.classList.add("image-preview");
        const img = document.createElement("img");
        img.src = src;
        box.appendChild(img);
        container.appendChild(box);
      });
    }
  } catch (err) {
    console.error("글 로드 실패:", err);
    alert("글을 불러오는 중 오류가 발생했습니다.");
    return window.location.href = "blog.html";
  }

  // 이미지 미리보기 (new_blog.js와 동일)
  inputFile.addEventListener("change", () => {
    container.innerHTML = "";
    Array.from(inputFile.files).forEach(file => {
      const box = document.createElement("div");
      box.classList.add("image-preview");
      const img = document.createElement("img");
      img.src = URL.createObjectURL(file);
      box.appendChild(img);
      container.appendChild(box);
    });
  });

  // 2) 수정 폼 제출
  form.addEventListener("submit", async e => {
    e.preventDefault();
    const title   = form.title.value.trim();
    const content = form.content.value.trim();
    const tags    = form.tags.value.trim();
    const files   = inputFile.files;
    if (!title || !content) return alert("제목과 본문은 필수입니다.");

    try {
      // 메타데이터 업데이트
      const res1 = await fetch(`/api/blog/articles/${articleId}`, {
        method: "PATCH",
        headers: { "Content-Type":"application/json" },
        credentials: "include",
        body: JSON.stringify({ title, content, tags })
      });
      if (!res1.ok) throw new Error("업데이트 실패");

      // 이미지 업로드
      if (files.length > 0) {
        const fd = new FormData();
        for (let f of files) fd.append("files", f);
        const res2 = await fetch(
          `/api/blog/articles/${articleId}/upload-images`,
          { method:"POST", credentials:"include", body:fd }
        );
        if (!res2.ok) console.warn("이미지 업로드 일부 실패");
      }

      alert("글이 성공적으로 수정되었습니다!");
      window.location.href = "blog.html";
    } catch (err) {
      console.error("수정 중 오류:", err);
      alert("수정에 실패했습니다.");
    }
  });
});
