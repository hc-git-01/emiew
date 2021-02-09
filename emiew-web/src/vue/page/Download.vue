<template>
  <em-main :loading="firstFetching || fetchingOperation">
    <em-scroller>
      <transition-group name="book">
        <div
          class="book"
          :class="{ selected: book.selected }"
          v-for="book of books"
          :key="book.url"
          @click="handleBookClick(book)"
        >
          <div class="book-cover">
            <img v-lazy="coverPrefix + book.coverUrl" />
          </div>
          <div class="book-details">
            <div class="book-title" @click.stop="handleTitleClick(book)">
              {{ book.title }}
            </div>
            <div class="book-tags">
              <em-tag size="small" icon="hashtag">
                {{ book.category }}
              </em-tag>
              <em-tag size="small" icon="star">
                {{ book.rating }}
              </em-tag>
              <em-tag size="small" :icon="calcPagesIcon(book)">
                {{ book.progress }}/{{ book.pages }}
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
      </transition-group>
    </em-scroller>

    <em-bar>
      <template slot="default">
        <em-bar-button icon="pause-circle" @click="fetchPause" />
        <em-bar-button icon="play-circle" @click="fetchResume" />
        <em-bar-button
          v-show="selectMode"
          icon="times-circle"
          @click="fetchRemove"
        />
        <em-bar-button
          :class="{ active: selectMode }"
          icon="list-ul"
          @click="handleChangeMode"
        />
      </template>

      <template slot="functions-button">
        <div id="finished-books">{{ firstFetching ? '-' : finishedBooks }}</div>
        <div id="total-books">{{ firstFetching ? '-' : totalBooks }}</div>
      </template>

      <template slot="functions">
        <em-bar-button
          :class="{ active: sortBy === 'title' }"
          icon="sort-alpha-down"
          @click="changeSort"
        />
        <em-bar-button icon="sync-alt" @click="setFetchInterval" />
      </template>
    </em-bar>
  </em-main>
</template>

<script>
import { translateLanguage } from '@/script/util/translate'

export default {
  name: 'Download',
  activated() {
    this.readSort()
    this.setFetchInterval()
  },
  deactivated() {
    clearInterval(this.interval)
  },
  beforeDestroy() {
    clearInterval(this.interval)
  },
  data() {
    return {
      coverPrefix: `${this.$project.api}image/cover?url=`,

      books: [],
      fetchingBooks: false,
      firstFetching: true,

      interval: '',

      selectMode: false,

      fetchingOperation: false,

      sortBy: 'time',
    }
  },
  methods: {
    fetchBooks() {
      if (this.selectMode || this.fetchingBooks) {
        return
      }

      this.fetchingBooks = true
      this.$axios
        .get('/download')
        .then((books) => {
          switch (this.sortBy) {
            case 'title':
              this.books = books.sort((p, n) =>
                this.cleanTitle(p.title).localeCompare(this.cleanTitle(n.title))
              )
              break
            case 'time':
              this.books = books.sort((p, n) => n.id - p.id)
              break
            default:
              break
          }

          this.fetchingBooks = false
          this.firstFetching = false
        })
        .catch(() => {
          clearInterval(this.interval)
          this.fetchingBooks = false
          this.firstFetching = false
        })
    },
    fetchPause() {
      let ids = this.books
        .filter((book) => book.selected)
        .map((book) => book.id)

      if (this.selectMode && ids.length <= 0) {
        this.$toast('请先进行选择', 'warn')
        return
      }

      this.fetchingOperation = true
      this.$axios
        .post('/download/pause', { ids: ids })
        .then((msg) => {
          this.selectMode = false
          this.$toast(msg)
          this.fetchingOperation = false
        })
        .catch(() => {
          clearInterval(this.interval)
          this.fetchingOperation = false
        })
    },
    fetchResume() {
      let ids = this.books
        .filter((book) => book.selected)
        .map((book) => book.id)

      if (this.selectMode && ids.length <= 0) {
        this.$toast('请先进行选择', 'warn')
        return
      }

      this.fetchingOperation = true
      this.$axios
        .post('/download/resume', { ids: ids })
        .then((msg) => {
          this.selectMode = false
          this.$toast(msg)
          this.fetchingOperation = false
        })
        .catch(() => {
          clearInterval(this.interval)
          this.fetchingOperation = false
        })
    },
    fetchRemove() {
      let ids = this.books
        .filter((book) => book.selected)
        .map((book) => book.id)

      if (ids.length <= 0) {
        this.$toast('请先进行选择', 'warn')
        return
      }

      if (!confirm('确定要删除吗？')) {
        return
      }

      this.fetchingOperation = true
      this.$axios
        .post('/download/remove', { ids: ids })
        .then((msg) => {
          this.selectMode = false
          this.$toast(msg)
          this.fetchingOperation = false
        })
        .catch(() => {
          clearInterval(this.interval)
          this.fetchingOperation = false
        })
    },
    setFetchInterval() {
      this.firstFetching = true

      if (this.interval) {
        clearInterval(this.interval)
      }

      this.interval = setInterval(() => {
        this.fetchBooks()
      }, 1000)
    },
    handleBookClick(book) {
      if (this.selectMode) {
        this.handleClickInSelectMode(book)
      } else {
        this.$push({
          name: 'Read',
          query: { url: book.url, pages: book.pages },
        })
      }
    },
    handleTitleClick(book) {
      if (this.selectMode) {
        this.handleClickInSelectMode(book)
      } else {
        this.$push({ name: 'Book', query: { url: book.url } })
      }
    },
    handleClickInSelectMode(book) {
      this.$set(book, 'selected', !book.selected)
    },
    handleChangeMode() {
      this.books.forEach((book) => this.$set(book, 'selected', false))
      this.selectMode ^= true
    },
    translateLanguage(language) {
      return translateLanguage(language)
    },
    calcPagesIcon(book) {
      if (book.status === 'PAUSED') {
        return 'pause'
      }

      if (book.status === 'FAILED') {
        return 'times'
      }

      if (book.finished) {
        return 'check'
      }

      if (book.status === 'NORMAL') {
        return 'download'
      }

      return 'image'
    },
    cleanTitle(title) {
      return title
        .replace(/\[.*?\]/g, '')
        .replace(/【.*?】/g, '')
        .replace(/\(.*?\)/g, '')
        .replace(/\（.*?\）/g, '')
        .trim()
    },
    changeSort() {
      if (this.sortBy === 'time') {
        this.sortBy = 'title'
        this.$toast('以标题排序')
      } else {
        this.sortBy = 'time'
        this.$toast('以下载时间排序')
      }
      this.saveSort()
    },
    readSort() {
      this.sortBy = localStorage.getItem('download-sort') || 'time'
    },
    saveSort() {
      localStorage.setItem('download-sort', this.sortBy)
    },
  },
  computed: {
    finishedBooks() {
      return this.books.filter((book) => book.finished).length
    },
    totalBooks() {
      return this.books.length
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

.book.selected,
.book.selected:active {
  background-color: #eee;
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

#finished-books {
  min-width: 40px;
  height: 25px;
  line-height: 25px;
  font-size: 14px;
  border-bottom: 1px solid #567;
  overflow: visible;
}

.active #finished-books {
  border-bottom: 1px solid #0af;
}

#total-books {
  min-width: 40px;
  height: 25px;
  line-height: 25px;
  font-size: 14px;
  overflow: visible;
}

.book-leave-active {
  transition: transform 0.2s ease, height 0.2s ease;
}

.book-leave-to {
  transform: scaleY(0);
  height: 0;
}
</style>
