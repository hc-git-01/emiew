<template>
  <em-main>
    <em-scroller :gap="true">
      <em-input icon="search" v-model="params.searchValue" />

      <em-title icon="hashtag">分类</em-title>
      <div>
        <em-tag
          class="category"
          v-for="category of categories"
          :key="category.value"
          :style="{
            backgroundColor: category.selected ? color[category.name] : '#fff',
            border: `1px solid ${color[category.name]}`,
            color: category.selected ? '#fff' : color[category.name],
            fill: category.selected ? '#fff' : color[category.name],
          }"
          @click="category.selected ^= true"
        >
          {{ translateCategory(category.name) }}
        </em-tag>
      </div>

      <em-title icon="star">评分</em-title>
      <div>
        <fa-icon
          class="rating"
          scale="2"
          v-for="i of 5"
          :key="i"
          :name="i <= params.rating ? 'star' : 'regular/star'"
          @click="params.rating = i"
        />
      </div>

      <em-title icon="image">页数</em-title>
      <div>
        <em-input class="pages-input" type="number" v-model="params.minPages" />
        <span id="pages-splitter">-</span>
        <em-input class="pages-input" type="number" v-model="params.maxPages" />
      </div>
    </em-scroller>

    <em-bar>
      <em-bar-button icon="undo" @click="handleReset" />
      <em-bar-button icon="search" @click="handleSearch" />
    </em-bar>
  </em-main>
</template>

<script>
import constants from '@/data/constants'
import categoryColor from '@/data/categoryColor'
import { copyValue } from '@/script/util/object'
import { translateCategory } from '@/script/util/translate'

export default {
  name: 'Search',
  mounted() {
    copyValue(this.$route.query, this.params)
    this.loadCategory(this.params.categories)
  },
  data() {
    return {
      params: {
        searchValue: constants.searchValue,
        categories: constants.categories,
        rating: constants.rating,
        minPages: constants.minPages,
        maxPages: constants.maxPages,
      },

      color: categoryColor,
      categories: [
        {
          name: 'Doujinshi',
          value: 2,
          selected: true,
        },
        {
          name: 'Manga',
          value: 4,
          selected: true,
        },
        {
          name: 'Artist CG',
          value: 8,
          selected: true,
        },
        {
          name: 'Game CG',
          value: 16,
          selected: true,
        },
        {
          name: 'Western',
          value: 512,
          selected: true,
        },
        {
          name: 'Non-H',
          value: 256,
          selected: true,
        },
        {
          name: 'Image Set',
          value: 32,
          selected: true,
        },
        {
          name: 'Cosplay',
          value: 64,
          selected: true,
        },
        {
          name: 'Asian Porn',
          value: 128,
          selected: true,
        },
        {
          name: 'Misc',
          value: 1,
          selected: true,
        },
      ],
    }
  },
  methods: {
    handleSearch() {
      this.params.categories =
        1023 -
        this.categories
          .filter((cat) => cat.selected)
          .reduce((total, next) => total + next.value, 0)

      this.$push({ name: 'Gallery', query: this.params })
    },
    handleReset() {
      copyValue(constants, this.params)
      this.categories.forEach((cat) => (cat.selected = true))
    },
    loadCategory(value) {
      value = 1023 - (value || 0)
      let cats = [...this.categories]
      cats.sort((p, n) => n.value - p.value)
      cats.forEach((cat) => {
        if (value - cat.value >= 0) {
          cat.selected = true
          value -= cat.value
        } else {
          cat.selected = false
        }
      })
    },
    translateCategory(category) {
      return translateCategory(category)
    },
  },
}
</script>

<style scoped>
.categories {
  display: flex;
  flex-flow: wrap;
  align-content: flex-end;
  align-items: center;
  overflow: hidden;
}

.category {
  white-space: nowrap;
  margin: 0 5px 5px 0;
}

.rating {
  margin-right: 10px;
  color: #ffd700;
  fill: #ffd700;;
}

.pages-input {
  display: inline-block;
  width: calc(50% - 10px);
}

#pages-splitter {
  display: inline-block;
  width: 20px;
  line-height: 40px;
  text-align: center;
}
</style>
