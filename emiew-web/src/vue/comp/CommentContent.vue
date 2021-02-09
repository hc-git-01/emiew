<template>
  <component :is="buildContent()" />
</template>

<script>
export default {
  name: 'CommentContent',
  props: {
    content: String,
  },
  methods: {
    buildContent() {
      let content = this.content

      content = content.replace(
        /<img src="/g,
        `<img src="${this.$project.api}image/direct?url=`
      )

      let matches = content.match(
        /<a href="https:\/\/(e-hentai|exhentai)\.org\/g\/[0-9]+\/[0-9a-z]+\/">.*?<\/a>/g
      )
      if (matches !== null) {
        for (let match of matches) {
          let url = match.split('"')[1]
          content = content.replace(
            match,
            `<span 
              style="text-decoration: underline; color: #0af"
              @click="$push({name: 'Book', query: {url: '${url}'}})"
            > 
              ${url}
            </span>`
          )
        }
      }

      matches = content.match(
        /<a href="https:\/\/(e-hentai|exhentai)\.org\/uploader\/.*?">.*?<\/a>/g
      )
      if (matches !== null) {
        for (let match of matches) {
          let uploader = match.split('"')[1].split('/')[4]
          content = content.replace(
            match,
            `<span 
              style="text-decoration: underline; color: #0af"
              @click="handleSearchUploader('${uploader}')"
            > 
              ${uploader}
            </span>`
          )
        }
      }

      return {
        template: `<div class="comment-content">${content}</div>`,
        methods: {
          handleSearchUploader(uploader) {
            this.$push({
              name: 'Gallery',
              query: { searchValue: `uploader:"${uploader}"` },
            })
          },
        },
      }
    },
  },
}
</script>

<style>
.comment-content {
  padding: 5px;
  background-color: #f6f6f6;
  border-radius: 5px;
  border: 1px dashed #ccc;
  word-wrap: break-word;
}

.comment-content * {
  max-width: 100%;
}
</style>
