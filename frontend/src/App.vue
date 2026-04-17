<template>
  <div id="app">
    <AppHeader />
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import AppHeader from '@/components/common/AppHeader.vue'
</script>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 16px;
}

a {
  text-decoration: none;
  color: inherit;
}

button {
  cursor: pointer;
}
</style>
