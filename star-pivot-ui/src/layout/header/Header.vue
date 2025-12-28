<template>
  <div class="header-container">
    <div class="left">
      <Collapse></Collapse>
      <BreadCrumb></BreadCrumb>
    </div>
    <div class="right">
      <span style="padding-right: 15px;color: #FFF;">欢迎您,{{ username }}</span>
      <LoginOut></LoginOut>
    </div>
  </div>
</template>

<script setup lang="ts">
import Collapse from "./Collapse.vue";
import LoginOut from "./LoginOut.vue";
import { useUserStore } from "@/store/user";
import { computed } from "vue";
const store = useUserStore()
const username = computed(()=>{
  return store.getUsername
})
</script>

<style scoped lang="scss">
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  .left{
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .right{
    display: flex;
    align-items: center;
  }
}
// :deep(.el-dropdown-link:focus){
//     outline: none;
// }
</style>
