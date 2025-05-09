<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>Chatbot</title>
  <style>
    /* ──────────────────────────────────────────────
       ▶ Chatbot Container
    ────────────────────────────────────────────── */
    #chatbot {
      position: fixed;
      bottom: 20px;
      right: 20px;
      width: 320px;
      max-height: 500px;
      font-family: sans-serif;
      z-index: 9999;
    }
    #chatbot header {
      background-color: #007bff;
      color: #fff;
      padding: 12px;
      border-radius: 10px 10px 0 0;
      cursor: pointer;
      user-select: none;
    }
    #chatbot .body {
      background: #fff;
      border: 1px solid #ccc;
      border-top: none;
      border-radius: 0 0 10px 10px;
      display: none;
      box-shadow: 0 0 10px rgba(0,0,0,0.2);
      display: flex;
      flex-direction: column;
      height: 100%;
    }

    /* ──────────────────────────────────────────────
       ▶ Chat Sections
    ────────────────────────────────────────────── */
    #chatbot fieldset {
      border: none;
      margin: 10px 0;
      padding: 0;
      display: flex;
      gap: 8px;
      justify-content: space-between;
    }
    #chatbot fieldset legend {
      font-size: 0.9em;
      margin-bottom: 4px;
    }
    #chatbot .chat-window {
      flex: 1;
      display: flex;
      flex-direction: column;
      padding: 0 12px;
      margin-bottom: 10px;
    }
    #chatbot .history {
  flex: 1 1 auto;           /* 남은 공간 모두 차지 */
  height: 300px;            /* 고정 높이 설정 */
  overflow-y: auto;         /* 스크롤 활성화 */
  border: 1px solid #ddd;
  padding: 8px;
  margin-bottom: 8px;
  background: #fafafa;
  border-radius: 4px;
}
#chatbot .history .message {
  position: relative;
  max-width: 95%;
  margin-bottom: 10px;
  padding: 8px 12px;
  border-radius: 16px;
  line-height: 1.4;
}
#chatbot .history .bot {
  align-self: flex-start;
  background-color: #f1f0f0;
}
#chatbot .history .bot::before {
  content: "";
  position: absolute;
  top: 12px;
  left: -8px;
  border: 8px solid transparent;
  border-right-color: #f1f0f0;
}
#chatbot .history .user {
  align-self: flex-end;
  background-color: #007bff;
  color: #fff;
}
#chatbot .history .user::before {
  content: "";
  position: absolute;
  top: 12px;
  right: -8px;
  border: 8px solid transparent;
  border-left-color: #007bff;
}
    #chatbot .input-group {
      display: flex;
      gap: 8px;
    }
    #chatbot .input-group input[type="text"] {
      flex: 1;
      padding: 6px 8px;
      border: 1px solid #ccc;
      border-radius: 4px;
    }
  </style>
</head>
<body>

<aside id="chatbot">
  <header>챗봇</header>
  <div class="body">
    <section class="chat-window">
      <fieldset>
        <legend>엔드포인트 선택</legend>
        <label><input type="radio" name="endpoint" value="tool"> 시간관리</label>
        <label><input type="radio" name="endpoint" value="member" checked> 멤버관리</label>
        <label><input type="radio" name="endpoint" value="vector"> Vector</label>
      </fieldset>

      <div class="history" id="chatHistory"></div>

      <div class="input-group">
        <input type="text" id="message" placeholder="메시지를 입력하고 Enter">
      </div>
    </section>
  </div>
</aside>

<script>
  (function() {
    const chatbot = document.getElementById('chatbot');
    const header  = chatbot.querySelector('header');
    const body    = chatbot.querySelector('.body');
    const history = document.getElementById('chatHistory');
    const input   = document.getElementById('message');

    header.addEventListener('click', () => {
      body.style.display = body.style.display === 'flex' ? 'none' : 'flex';
    });

    input.addEventListener('keyup', async (e) => {
      if (e.key !== 'Enter') return;
      const text = input.value.trim();
      if (!text) return;

      appendMessage('user', text);
      input.value = '';

      const endpoint = chatbot.querySelector('input[name=endpoint]:checked').value;
      console.log("Selected endpoint:", endpoint);
      try {
        const res = await fetch(`/ai/\${endpoint}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ message: text })
        });
        const { data } = await res.json();
        appendMessage('bot', data.message);
      } catch (err) {
        console.error(err);
        appendMessage('bot', '오류가 발생했습니다.');
      }
    });

    function appendMessage(who, text) {
      const div = document.createElement('div');
      div.className = `message ${who}`;
      div.textContent = (who === 'user' ? '사용자: ' : '봇: ') + text;
      history.appendChild(div);
      history.scrollTop = history.scrollHeight;
    }
  })();
</script>

</body>
</html>
