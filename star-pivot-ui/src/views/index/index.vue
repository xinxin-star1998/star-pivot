<!-- 布局容器 -->
<template>
  <div class="app-layout">
    <aside id="app-sidebar">
      <ArtSidebarMenu />
    </aside>

    <main id="app-main">
      <div id="app-header">
        <ArtHeaderBar />
      </div>
      <div id="app-content">
        <ArtPageContent />
      </div>
    </main>

    <div id="app-global">
      <ArtGlobalComponent />
    </div>
  </div>
</template>

<script setup lang="ts">
  import ArtSidebarMenu from '@/components/core/layouts/art-menus/art-sidebar-menu/index.vue'
  import ArtHeaderBar from '@/components/core/layouts/art-header-bar/index.vue'
  import ArtPageContent from '@/components/core/layouts/art-page-content/index.vue'
  import ArtGlobalComponent from '@/components/core/layouts/art-global-component/index.vue'

  // Layout 组件不应该接收任何外部属性（class、style等）
  // 这些属性应该只应用到实际的页面组件上
  defineOptions({ name: 'AppLayout', inheritAttrs: false })
</script>

<style lang="scss" scoped>
  @use './style';
</style>
