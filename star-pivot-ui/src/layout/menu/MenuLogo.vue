<template>
  <div class="logo">
    <img :src="MenuLogo" alt="系统Logo"/>
    <span v-if="show" class="logo-title">{{ title }}</span>
  </div>
</template>

<script setup lang="ts">
import {ref, watch} from 'vue'
import MenuLogo from "@/assets/image/logo2.png";
import { useMenuStore } from "@/store/menu";
const store = useMenuStore()
const title = ref('星枢权限管理系统')
//解决伸缩僵硬问题：延时
const show = ref(true)
watch(
    () => store.collapse,
    (collapsed: boolean) => {
      if (!collapsed) {
        setTimeout(() => {
          show.value = !collapsed;
        }, 300);
      } else {
        show.value = !collapsed;
      }
    },
    { immediate: true } // 立即执行一次，确保初始状态正确
)
</script>

<style scoped lang="scss">
.logo {
  display: flex;
  width: 100%;
  height: 60px;
  background-color: #304156;
  text-align: center;
  cursor: pointer;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  overflow: hidden;
  
  img {
    width: 40px;
    height: 40px;
    margin-right: 8px;
    flex-shrink: 0;
    transition: all 0.3s;
  }
  
  .logo-title {
    color: #FFF;
    font-weight: 800;
    font-size: 18px;
    font-family: FangSong, serif;
    white-space: nowrap;
    transition: opacity 0.3s;
    opacity: 1;
  }
}
</style>