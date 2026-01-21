<!-- 表格按钮 -->
<template>
  <div
    :class="[
      'inline-flex items-center justify-center min-w-8 h-8 px-2.5 mr-2.5 text-sm c-p rounded-md align-middle',
      buttonClass
    ]"
    :style="{ backgroundColor: buttonBgColor, color: iconColor }"
    @click="handleClick"
  >
    <ArtSvgIcon :icon="iconContent" />
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'ArtButtonTable' })

  interface Props {
    /** 按钮类型 */
    type?: 'add' | 'edit' | 'delete' | 'more' | 'view' | 'sync' | 'generate'
    /** 按钮图标 */
    icon?: string
    /** 按钮样式类 */
    iconClass?: string
    /** icon 颜色 */
    iconColor?: string
    /** 按钮背景色 */
    buttonBgColor?: string
  }

  const props = withDefaults(defineProps<Props>(), {})

  const emit = defineEmits<{
    (e: 'click'): void
  }>()

  // 默认按钮配置
  const defaultButtons: Record<NonNullable<Props['type']>, { icon: string; class: string }> = {
    add: { icon: 'ri:add-fill', class: 'bg-theme/12 text-theme' },
    edit: { icon: 'ri:pencil-line', class: 'bg-secondary/12 text-secondary' },
    delete: { icon: 'ri:delete-bin-5-line', class: 'bg-error/12 text-error' },
    view: { icon: 'ri:eye-line', class: 'bg-info/12 text-info' },
    sync: { icon: 'ri:refresh-line', class: 'bg-info/12 text-info' },
    generate: { icon: 'ri:code-s-slash-line', class: 'bg-success/12 text-success' },
    more: { icon: 'ri:more-2-fill', class: '' }
  }

  // 获取图标内容
  const iconContent = computed(() => {
    if (props.icon) return props.icon
    if (props.type && props.type in defaultButtons) {
      return defaultButtons[props.type as keyof typeof defaultButtons]?.icon || ''
    }
    return ''
  })

  // 获取按钮样式类
  const buttonClass = computed(() => {
    if (props.iconClass) return props.iconClass
    if (props.type && props.type in defaultButtons) {
      return defaultButtons[props.type as keyof typeof defaultButtons]?.class || ''
    }
    return ''
  })

  const handleClick = () => {
    emit('click')
  }
</script>
