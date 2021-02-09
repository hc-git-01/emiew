<template>
  <div
    class="em-tag"
    :class="[{ 'text-only': !icon }, size]"
    @click="$emit('click')"
  >
    <fa-icon v-if="icon" :name="icon" :scale="iconScale" />
    <span>
      <slot />
    </span>
  </div>
</template>

<script>
export default {
  name: 'EmTag',
  props: {
    icon: {
      type: String,
    },
    size: {
      type: String,
      default: 'normal',
    },
  },
  computed: {
    iconScale() {
      return {
        small: 0.8,
        normal: 1,
        big: 1.2,
      }[this.size]
    },
  },
}
</script>

<style>
.em-tag {
  display: inline-flex;
  align-items: center;
  background-color: #f6f6f6;
  border-radius: 5px;
  padding: 5px 7.5px;
  flex-wrap: nowrap;
  white-space: normal;
  vertical-align: top;
  min-height: 30px;
  line-height: 16px;
  font-size: 16px;
}

.em-tag.small {
  min-height: 20px;
  line-height: 14px;
  font-size: 14px;
  padding: 2px 5px;
}

.em-tag.big {
  min-height: 40px;
  line-height: 18px;
  font-size: 18px;
  padding: 5px 10px;
}

.em-tag span {
  margin-left: 5px;
  word-break: break-all;
}

.em-tag.small span {
  margin-left: 2.5px;
}

.em-tag.text-only span {
  margin-left: 0;
}
</style>
