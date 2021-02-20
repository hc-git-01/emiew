<template>
  <em-main :loading="fetchingBooks">
    <em-scroller
      ref="scroller"
      @scroll="handleScroll"
      @touchend.native="handleScroll"
    >
      <div
        class="book"
        v-for="(book, index) of books"
        :key="book.url"
        @click="$push({ name: 'Book', query: { url: book.url } })"
      >
        <template v-if="index >= bookLoadFrom && index < bookLoadTo">
          <div class="book-cover">
            <em-image :src="coverPrefix + book.coverUrl" />
          </div>

          <div class="book-details">
            <div class="book-title" @click="$emit('title-click', $event)">
              {{ book.title }}
            </div>
            <div class="book-tags">
              <em-tag
                size="small"
                icon="hashtag"
                class="category"
                :style="{ backgroundColor: color[book.category] }"
              >
                {{ translateCategory(book.category) }}
              </em-tag>
              <em-tag
                size="small"
                :icon="book.downloaded ? 'download' : 'image'"
              >
                {{ book.pages }}
              </em-tag>
              <em-tag size="small" icon="star">
                {{ book.rating }}
              </em-tag>
              <em-tag v-if="book.language" size="small" icon="globe">
                {{ translateLanguage(book.language) }}
              </em-tag>
              <em-tag size="small" icon="user">
                {{ book.uploader }}
              </em-tag>
              <em-tag size="small" icon="clock">
                {{ book.uploadTime }}
              </em-tag>
            </div>
          </div>
        </template>
      </div>
    </em-scroller>

    <em-bar>
      <template slot="default">
        <button
          id="search-value"
          @click="$push({ name: 'Search', query: params })"
        >
          <span v-if="params.searchValue">{{ params.searchValue }}</span>
          <fa-icon v-else name="search" scale="1.5" />
        </button>
      </template>

      <template slot="functions-button">
        <div id="page-number">{{ currPage + 1 }}</div>
        <div id="total-pages">{{ totalPages || '-' }}</div>
      </template>

      <template slot="functions">
        <em-bar-button icon="sync-alt" @click="handleReload" />
        <em-bar-button
          icon="map-marker-alt"
          @click="pageModalShow = totalPages > 1"
        />
        <em-bar-button icon="cog" @click="$push({ name: 'Config' })" />
        <em-bar-button icon="download" @click="$push({ name: 'Download' })" />
      </template>
    </em-bar>

    <em-modal :show="pageModalShow">
      <template slot="default">
        <span>请输入跳转页码（1 ~ {{ totalPages }}）：</span>
        <em-input id="jump-page-input" type="number" v-model="jumpPage" />
      </template>

      <template slot="buttons">
        <em-button type="gray" @click="pageModalShow = false">取消</em-button>
        <em-button @click="handleJumpPage">跳转</em-button>
      </template>
    </em-modal>
  </em-main>
</template>

<script>
import constants from '@/data/constants'
import categoryColor from '@/data/categoryColor'
import { copyValue } from '@/script/util/object'
import { translateLanguage, translateCategory } from '@/script/util/translate'

export default {
  name: 'Gallery',
  mounted() {
    copyValue(this.$route.query, this.params)
    this.fetchBooks()
  },
  data() {
    return {
      coverPrefix: `${this.$project.api}image/cover?url=`,

      books: [],
      currPage: 0,
      nextPage: 0,
      totalPages: 0,

      params: {
        pageNumber: constants.pageNumber,
        searchValue: constants.searchValue,
        categories: constants.categories,
        rating: constants.rating,
        minPages: constants.minPages,
        maxPages: constants.maxPages,
      },

      pageTurned: false,
      fetchingBooks: false,

      jumpPage: 0,
      pageModalShow: false,

      color: categoryColor,

      bookLoadFrom: 0,
      bookLoadTo: 0,
    }
  },
  methods: {
    fetchBooks() {
      if (this.fetchingBooks) {
        return
      }
      this.fetchingBooks = true
      this.$axios
        .get('/crawl/gallery', this.params)
        .then((page) => {
          this.currPage = page.pageNumber
          this.nextPage = page.nextPage
          this.totalPages = page.totalPages
          this.books = this.books.concat(
            page.elements.filter(
              (e) => this.books.map((book) => book.url).indexOf(e.url) < 0
            )
          )
          this.hideBook()
          this.fetchingBooks = false
        })
        .catch(() => {
          this.params.pageNumber = this.currPage
          this.fetchingBooks = false
        })
    },
    handleReload() {
      this.books = []
      this.pageTurned = false
      this.fetchBooks()
    },
    handleScroll() {
      let scroller = this.$refs.scroller.$el

      if (
        !this.fetchingBooks &&
        this.params.pageNumber < this.totalPages - 1 &&
        scroller.scrollTop + scroller.clientHeight >= scroller.scrollHeight - 71
      ) {
        this.params.pageNumber = this.nextPage || this.params.pageNumber + 1
        this.fetchBooks()
      }

      this.hideBook()
    },
    hideBook() {
      let scrollTop = this.$refs.scroller.$el.scrollTop
      let screenHeight = window.innerHeight
      this.bookLoadFrom = Math.floor((scrollTop - 0.5 * screenHeight) / 162)
      this.bookLoadTo = Math.ceil((scrollTop + 1.5 * screenHeight) / 162)
    },
    handleJumpPage() {
      if (
        !this.jumpPage ||
        this.jumpPage <= 0 ||
        this.jumpPage > this.totalPages
      ) {
        this.$toast('请输入正确的页码', 'warn')
        return
      }

      this.books = []
      this.params.pageNumber = this.jumpPage - 1
      this.fetchBooks()
      this.pageModalShow = false
    },
    translateLanguage(lang) {
      return translateLanguage(lang)
    },
    translateCategory(category) {
      return translateCategory(category)
    },
  },
  watch: {
    pageModalShow(next) {
      if (next) {
        this.jumpPage = this.params.pageNumber + 1
      }
    },
  },
}
</script>

<style scoped>
.book {
  display: flex;
  flex-flow: nowrap;
  height: 162px;
  padding: 10px;
  overflow: hidden;
  border-bottom: 0.5px solid #ccc;
}

.book:active {
  background-color: #ddd;
}

.book-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100%;
}

.em-image {
  max-width: 100%;
  max-height: 100%;
  border-radius: 5px;
  box-shadow: 0 1px 3px #0002;
}

.book-details {
  width: 0;
  flex: 1;
  margin-left: 10px;
  height: 100%;
}

.book-title {
  width: 100%;
  height: 60px;
  line-height: 20px;
  overflow: hidden;
  font-weight: bold;
  overflow-wrap: break-word;
  overflow: hidden;
  display: -moz-box;
  display: -webkit-box;
  line-clamp: 3;
  -moz-box-orient: vertical;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.book-tags {
  display: flex;
  align-content: flex-end;
  align-items: center;
  flex-flow: wrap;
  margin-top: 12px;
  height: 70px;
  overflow: hidden;
}

.book-tags .em-tag {
  margin: 5px 5px 0 0;
}

.category {
  color: #fff;
  fill: #fff;
}

#search-value {
  width: 0;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: calc(100% - 10px);
  margin: 0 5px;
  padding: 0 10px;
  background-color: #eee;
  border-radius: 2.5px;
  border: none;
}

#search-value:active {
  background-color: #ddd;
}

#search-value span {
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

#page-number {
  min-width: 40px;
  height: 25px;
  line-height: 25px;
  font-size: 14px;
  border-bottom: 1px solid #567;
  overflow: visible;
}

.active #page-number {
  border-bottom: 1px solid #0af;
}

#total-pages {
  min-width: 40px;
  height: 25px;
  line-height: 25px;
  font-size: 14px;
  overflow: visible;
}

#jump-page-input {
  margin-top: 10px;
}
</style>
