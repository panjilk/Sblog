<script setup lang='ts'>

</script>

<template>
  <div class="container">
    <div class="title">
      <div class="avatar-wrapper">
        <img src="/avator.jpg" alt="头像" class="avatar">
      </div>
      <h1 class="name">Panjilk</h1>
      <p class="bio">热爱编程，追求卓越</p>
      <div class="social-links">
        <a href="https://github.com/panjilk" class="social-link">GitHub</a>
        <a href="/index" class="social-link">博客</a>
        <a href="/index" class="social-link">联系我</a>
      </div>
    </div>

    <div class="content">
      <div class="content-1">
        <h1 class="section-title">关于我</h1>
        <p class="description">大家好！我是一名练习时长两年半的实习生。我喜欢唱，跳，rap，篮球。</p>
        <h2 class="subsection-title">兴趣爱好</h2>
        <div class="hobbies">
          <span class="hobby-tag">💻 编程</span>
          <span class="hobby-tag">🎮 CF:GO</span>
          <span class="hobby-tag">🏎️ QQ飞车</span>
          <span class="hobby-tag">📚 阅读</span>
        </div>
      </div>

      <div class="conten-2">
        <h1 class="section-title">技能专长</h1>
        <div class="skills">
          <div class="skill-item">
            <span class="skill-name">Vibe Coding</span>
            <div class="skill-bar"><div class="skill-fill" style="width: 99%;"></div></div>
          </div>
          <div class="skill-item">
            <span class="skill-name">JAVA</span>
            <div class="skill-bar"><div class="skill-fill" style="width: 5%;"></div></div>
          </div>
          <div class="skill-item">
            <span class="skill-name">Springboot</span>
            <div class="skill-bar"><div class="skill-fill" style="width: 5%;"></div></div>
          </div>
          <div class="skill-item">
            <span class="skill-name">Vue.js</span>
            <div class="skill-bar"><div class="skill-fill" style="width: 5%;"></div></div>
          </div>
          <div class="skill-item">
            <span class="skill-name">Django</span>
            <div class="skill-bar"><div class="skill-fill" style="width: 5%;"></div></div>
          </div>
        </div>
      </div>
    </div>

    <div class="footer">
      <p>© 2026 Sblog · 用心创造</p>
    </div>
  </div>
</template>

<style scoped lang='scss'>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  width: 100vw;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 20px;
  overflow-y: auto;
}

.title {
  width: 90%;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px 30px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  animation: slideDown 0.6s ease-out;
}

.avatar-wrapper {
  margin-bottom: 20px;
}

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 5px solid white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: scale(1.05) rotate(5deg);
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
  }
}

.name {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
}

.bio {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}

.social-link {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  }
}

.content {
  width: 90%;
  max-width: 600px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-1,
.conten-2 {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
  animation: slideUp 0.6s ease-out;
  animation-fill-mode: both;
}

.content-1 {
  animation-delay: 0.2s;
}

.conten-2 {
  animation-delay: 0.4s;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 3px solid #667eea;
  display: inline-block;
}

.description {
  font-size: 15px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 25px;
}

.subsection-title {
  font-size: 18px;
  font-weight: 600;
  color: #667eea;
  margin-bottom: 15px;
}

.hobbies {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hobby-tag {
  padding: 10px 20px;
  background: linear-gradient(135deg, #f5f7fa, #e8ecf3);
  border-radius: 25px;
  font-size: 14px;
  color: #555;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;

  &:hover {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    transform: translateY(-3px) scale(1.05);
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
  }
}

.skills {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.skill-item {
  .skill-name {
    display: block;
    font-size: 14px;
    color: #555;
    font-weight: 500;
    margin-bottom: 8px;
  }

  .skill-bar {
    width: 100%;
    height: 10px;
    background: #e8ecf3;
    border-radius: 10px;
    overflow: hidden;
  }

  .skill-fill {
    height: 100%;
    background: linear-gradient(90deg, #667eea, #764ba2);
    border-radius: 10px;
    transition: width 1s ease-out;
    animation: fillBar 1.5s ease-out;
    animation-fill-mode: both;
  }
}

.footer {
  width: 90%;
  max-width: 600px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 20px;
  text-align: center;
  margin-bottom: 10px;
  animation: fadeIn 0.8s ease-out;
  animation-delay: 0.6s;
  animation-fill-mode: both;

  p {
    color: white;
    font-size: 14px;
    margin: 0;
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fillBar {
  from {
    width: 0;
  }
}

// 深色模式样式
[data-theme='dark'] .container {
  background: linear-gradient(135deg, #3a3054 0%, #4a3a6a 100%);
}

[data-theme='dark'] .title {
  background: rgba(40, 40, 50, 0.95);
}

[data-theme='dark'] .name {
  background: linear-gradient(135deg, #a78bfa, #818cf8);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

[data-theme='dark'] .bio {
  color: #b0b0b0;
}

[data-theme='dark'] .content-1 {
  .subsection-title {
    color: #a78bfa;
  }

  .hobby-tag {
    background: linear-gradient(135deg, #3a3a4a, #2a2a3a);
    color: #e0e0e0;
    border-color: #3a3a4a;
  }
}

[data-theme='dark'] .content-2 {
  .subsection-title {
    color: #a78bfa;
  }

  .skill-item {
    .skill-name {
      color: #e0e0e0;
    }

    .skill-bar {
      background: #3a3a4a;
    }
  }
}
</style>