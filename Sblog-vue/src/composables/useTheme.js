import { ref, watch, computed } from 'vue'

const THEME_KEY = 'sblog-theme'

const theme = ref(localStorage.getItem(THEME_KEY) || 'light')

// 监听主题变化，保存到 localStorage
watch(theme, (newTheme) => {
  localStorage.setItem(THEME_KEY, newTheme)
  // 更新 HTML 的 data-theme 属性
  document.documentElement.setAttribute('data-theme', newTheme)
  document.body.setAttribute('data-theme', newTheme)
})

// 初始化主题
if (typeof window !== 'undefined') {
  document.documentElement.setAttribute('data-theme', theme.value)
  document.body.setAttribute('data-theme', theme.value)
}

export function useTheme() {
  const isDark = computed(() => theme.value === 'dark')

  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
  }

  const setTheme = (newTheme) => {
    theme.value = newTheme
  }

  return {
    theme,
    isDark,
    toggleTheme,
    setTheme
  }
}
