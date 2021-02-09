<template>
  <em-main :loading="fetchingBlocks || fetchingDelete">
    <em-scroller>
      <transition-group name="blocks">
        <div
          class="block-item"
          v-for="block of blocks"
          :key="block.id"
          :class="{ selected: block.selected }"
          @click="block.selected ^= true"
        >
          {{ translateTagGroup(block.type) }}:
          {{ translateTag(block.type, block.value) }}
        </div>
      </transition-group>
    </em-scroller>

    <em-bar>
      <em-bar-button icon="sync-alt" @click="fetchBlocks" />
      <em-bar-button
        v-show="!fetchingBlocks && blocks.length > 0"
        icon="trash"
        @click="fetchDelete"
      />
    </em-bar>
  </em-main>
</template>

<script>
import { translateTagGroup, translateTag } from '@/script/util/translate'

export default {
  name: 'Block',
  mounted() {
    this.fetchBlocks()
  },
  data() {
    return {
      blocks: [],

      fetchingBlocks: false,
      fetchingDelete: false,
    }
  },
  methods: {
    fetchBlocks() {
      if (this.fetchingBlocks) {
        return
      }
      this.fetchingBlocks = true
      this.$axios
        .get('/block')
        .then((blocks) => {
          blocks.forEach((block) => (block.selected = false))
          this.blocks = blocks
          this.fetchingBlocks = false
        })
        .catch(() => {
          this.fetchingBlocks = false
        })
    },
    fetchDelete() {
      if (this.fetchingDelete) {
        return
      }

      let ids = this.blocks
        .filter((block) => block.selected)
        .map((block) => block.id)
      if (ids.length <= 0) {
        return
      }

      this.fetchingDelete = true
      this.$axios
        .delete('/block', { ids: ids })
        .then((blocks) => {
          blocks.forEach((block) => (block.selected = false))
          this.blocks = blocks
          this.$toast(`已删除${ids.length}条屏蔽内容`)
          this.fetchingDelete = false
        })
        .catch(() => {
          this.fetchingDelete = false
        })
    },
    translateTagGroup(group) {
      if (group === 'uploader') {
        return '上传者'
      }

      return translateTagGroup(group)
    },
    translateTag(group, tag) {
      return translateTag(group, tag)
    },
  },
}
</script>

<style scoped>
.block-item {
  border-bottom: 0.5px solid #ccc;
  line-height: 20px;
  padding: 0 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 40px;
  line-height: 40px;
}

.block-item.selected {
  background-color: #eee;
}

.blocks-leave-active {
  transition: transform 0.2s ease, height 0.2s ease;
}

.blocks-leave-to {
  transform: scaleY(0);
  height: 0;
}
</style>
