<template>
  <em-main
    :loading="
      fetchingBook || fetchingRefresh || fetchingAddBlock || fetchingDownload
    "
  >
    <em-scroller v-if="book" :gap="true">
      <div id="thumbs" @scroll="handleThumbsScroll">
        <img
          v-for="thumb of thumbs"
          :key="thumb"
          v-lazy="imagePrefix + thumb"
        />
      </div>

      <div id="title">
        {{ book.title }}
      </div>

      <div v-if="book.subtitle" id="subtitle">
        {{ book.subtitle }}
      </div>

      <em-tag class="url" icon="link" @click="handleUrlClick">
        {{ book.url }}
      </em-tag>

      <em-tag
        v-if="book.parentUrl"
        class="url"
        icon="arrow-up"
        @click="$push({ name: 'Book', query: { url: book.parentUrl } })"
      >
        {{ book.parentUrl }}
      </em-tag>

      <div id="details">
        <em-tag icon="hashtag">{{ translateCategory(book.category) }}</em-tag>
        <em-tag icon="star">{{ book.rating }}</em-tag>
        <em-tag icon="image">{{ book.pages }}</em-tag>
        <em-tag icon="user" @click="handleTagClick('uploader', book.uploader)">
          {{ book.uploader }}
        </em-tag>
        <em-tag icon="clock">{{ book.uploadTime }}</em-tag>
      </div>

      <em-title v-if="book.tagGroups && book.tagGroups.length > 0" icon="tags">
        标签
      </em-title>
      <div v-if="book.tagGroups && book.tagGroups.length > 0">
        <div
          class="tag-group"
          v-for="tagGroup of book.tagGroups"
          :key="tagGroup.name"
        >
          <div class="tag-group-name">
            {{ translateTagGroup(tagGroup.name) }}
          </div>
          <div class="tag-group-tags">
            <em-tag
              class="tag"
              v-for="tag of tagGroup.tags"
              :key="tag"
              @click="handleTagClick(tagGroup.name, tag)"
            >
              {{ translateTag(tagGroup.name, tag) }}
            </em-tag>
          </div>
        </div>
      </div>

      <em-title
        v-if="book.comments && book.comments.length > 0"
        icon="comments"
      >
        评论
      </em-title>
      <div v-if="book.comments && book.comments.length > 0">
        <div
          class="comment"
          v-for="(comment, index) of book.comments"
          :key="index"
        >
          <div>
            <em-tag :icon="comment.isUploader ? 'upload' : 'user'">
              {{ comment.publisher }}
            </em-tag>
            <em-tag icon="clock">
              {{ comment.publishTime }}
            </em-tag>
            <em-tag
              v-if="comment.score"
              :icon="comment.score > 0 ? 'smile' : 'frown'"
            >
              {{ comment.score }}
            </em-tag>
          </div>

          <comment-content :content="comment.content" />
        </div>
      </div>
    </em-scroller>

    <em-bar>
      <em-bar-button icon="sync-alt" @click="fetchRefresh" />
      <em-bar-button
        v-show="book"
        icon="eye"
        @click="
          $push({
            name: 'Read',
            query: { url: $route.query.url, pages: book.pages },
          })
        "
      />
      <em-bar-button
        v-show="book && !book.downloaded"
        icon="download"
        @click="fetchDownload"
      />
    </em-bar>

    <em-modal :show="tagModalShow">
      <div slot="default" id="tag-modal-content">
        {{ translateTagGroup(tagModalType) }}:
        {{ translateTag(tagModalType, tagModalValue) }}
      </div>

      <template slot="buttons">
        <em-button type="gray" @click="tagModalShow = false">取消</em-button>
        <em-button type="orange" @click="fetchAddBlock">屏蔽 </em-button>
        <em-button
          @click="
            tagModalShow = false
            $push({ name: 'Gallery', query: { searchValue: searchValue } })
          "
        >
          搜索
        </em-button>
      </template>
    </em-modal>
  </em-main>
</template>

<script>
import CommentContent from '@/vue/comp/CommentContent'
import {
  translateLanguage,
  translateTagGroup,
  translateTag,
  translateCategory,
} from '@/script/util/translate'

export default {
  name: 'Book',
  components: {
    CommentContent,
  },
  mounted() {
    if (!this.$route.query.url) {
      this.$push({ name: 'Gallery' })
    }
    this.fetchBook()
  },
  data() {
    return {
      imagePrefix: `${this.$project.api}image/direct?url=`,

      pageNumber: 0,

      book: '',
      thumbs: [],

      fetchingBook: false,

      tagModalType: '',
      tagModalValue: '',
      tagModalShow: false,

      fetchingRefresh: false,
      fetchingAddBlock: false,
      fetchingDownload: false,
    }
  },
  methods: {
    fetchBook() {
      if (this.fetchingBook) {
        return
      }
      this.fetchingBook = true
      this.$axios
        .get('/crawl/book', {
          url: this.$route.query.url,
          pageNumber: this.pageNumber,
        })
        .then((book) => {
          if (!this.book) {
            this.book = book
          }

          this.thumbs = this.thumbs.concat(book.thumbUrls)
          this.fetchingBook = false
        })
        .catch(() => {
          if (this.pageNumber > 0) {
            this.pageNumber--
          }
          this.fetchingBook = false
        })
    },
    fetchRefresh() {
      if (this.fetchingRefresh) {
        return
      }
      this.fetchingRefresh = true
      this.$axios
        .get('/crawl/book/refresh', {
          url: this.$route.query.url,
        })
        .then(() => {
          this.pageNumber = 0
          this.book = ''
          this.thumbs = []
          this.fetchingRefresh = false
          this.fetchBook()
        })
        .catch(() => {
          this.fetchingRefresh = false
        })
    },
    fetchDownload() {
      if (this.fetchingDownload) {
        return
      }
      this.fetchingDownload = true
      this.$axios
        .post('/download', {
          url: this.$route.query.url,
        })
        .then(() => {
          this.book.downloaded = true
          this.$toast('已加入下载队列')
          this.fetchingDownload = false
        })
        .catch(() => {
          this.fetchingDownload = false
        })
    },
    fetchAddBlock() {
      if (this.fetchingAddBlock) {
        return
      }
      this.fetchingAddBlock = true
      this.$axios
        .post('/block', {
          type: this.tagModalType,
          value: this.tagModalValue,
        })
        .then(() => {
          this.tagModalShow = false
          this.$toast('已添加屏蔽内容')
          this.fetchingAddBlock = false
        })
        .catch(() => {
          this.fetchingAddBlock = false
        })
    },
    handleThumbsScroll(e) {
      if (
        !this.fetchingBook &&
        (this.pageNumber + 1) * 40 < this.book.pages &&
        e.target.scrollLeft + e.target.clientWidth >= e.target.scrollWidth
      ) {
        this.pageNumber++
        this.fetchBook()
      }
    },
    handleTagClick(type, value) {
      this.tagModalType = type
      this.tagModalValue = value
      this.tagModalShow = true
    },
    handleUrlClick() {
      window.open(this.book.url, '_blank')
    },
    translateLanguage(language) {
      return translateLanguage(language)
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
    translateCategory(category) {
      return translateCategory(category)
    },
  },
  computed: {
    searchValue() {
      return `${this.tagModalType}:"${this.tagModalValue}"`
    },
  },
}
</script>

<style scoped>
.em-tag {
  margin: 0 5px 5px 0;
  max-width: calc(100% - 5px);
}

#thumbs {
  margin: -10px;
  display: flex;
  align-items: center;
  height: 142px;
  overflow-x: auto;
  overflow-y: hidden;
}

#thumbs img {
  max-height: 100%;
}

#title {
  font-weight: bold;
  margin-top: 20px;
}

#subtitle {
  margin-top: 10px;
}

.url {
  display: flex;
  margin-top: 10px;
}

.url:active {
  background-color: #ddd;
}

#details {
  margin-top: 10px;
}

.tag-group {
  position: relative;
  margin-bottom: 5px;
}

.tag-group-name {
  position: absolute;
  top: 0;
  bottom: 5px;
  left: 0;
  width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #eee;
  border-radius: 5px;
  line-height: 30px;
}

.tag-group-tags {
  margin-left: 85px;
  display: flex;
  flex-flow: wrap;
}

.tag:active {
  background-color: #ddd;
}

.comment {
  margin-top: 20px;
}

.comment:first-of-type {
  margin-top: 0;
}

#tag-modal-content {
  text-align: center;
}
</style>
