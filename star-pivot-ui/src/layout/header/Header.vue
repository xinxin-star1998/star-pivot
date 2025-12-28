<template>
  <div class="header-content">
    <div class="header-left">
      <Collapse></Collapse>
      <BreadCrumb></BreadCrumb>
    </div>
    <div class="header-right">
      <LoginOut></LoginOut>
    </div>
  </div>
</template>
<script setup lang="ts">
import Collapse from './Collapse.vue';
import BreadCrumb from './BreadCrumb.vue';
import LoginOut from './LoginOut.vue';
</script>
<style scoped lang='scss'>
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 100%;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}
</style>