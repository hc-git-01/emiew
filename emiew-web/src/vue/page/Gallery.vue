<template>
  <em-main :loading="fetchingBooks">
    <em-scroller @scroll="handleScroll">
      <div
        class="book"
        v-for="book of books"
        :key="book.url"
        @click="$push({ name: 'Book', query: { url: book.url } })"
      >
        <div class="book-cover">
          <img v-lazy="coverPrefix + book.coverUrl" />
        </div>
        <div class="book-details">
          <div class="book-title" @click="$emit('title-click', $event)">
            {{ book.title }}
          </div>
          <div class="book-tags">
            <em-tag size="small" icon="hashtag">
              {{ translateCategory(book.category) }}
            </em-tag>
            <em-tag size="small" icon="star">
              {{ book.rating }}
            </em-tag>
            <em-tag size="small" :icon="book.downloaded ? 'download' : 'image'">
              {{ book.pages }}
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
        <div id="page-number">{{ params.pageNumber + 1 }}</div>
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
import constants from '@/script/conf/constants'
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
          this.totalPages = page.totalPages
          this.books = this.books.concat(
            page.elements.filter(
              (e) => this.books.map((book) => book.url).indexOf(e.url) < 0
            )
          )
          this.fetchingBooks = false
        })
        .catch(() => {
          if (this.pageTurned) {
            this.pageNumber--
            this.pageTurned = false
          }
          this.fetchingBooks = false
        })
    },
    handleReload() {
      this.books = []
      this.pageTurned = false
      this.fetchBooks()
    },
    handleScroll(e) {
      if (
        !this.fetchingBooks &&
        this.params.pageNumber < this.totalPages - 1 &&
        e.target.scrollTop + e.target.clientHeight >= e.target.scrollHeight - 70
      ) {
        this.pageTurned = true
        this.params.pageNumber++
        this.fetchBooks()
      }
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

.book-cover img {
  max-width: 100%;
  max-height: 100%;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.book-details {
  flex: 1;
  margin-left: 10px;
  height: 100%;
}

.book-title {
  width: 100%;
  height: 60px;
  line-height: 20px;
  overflow-x: hidden;
  overflow-y: auto;
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
