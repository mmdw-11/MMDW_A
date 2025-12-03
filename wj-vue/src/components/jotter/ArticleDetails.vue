<!-- <template>
  <div class="articles-area">
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
      <div>
        <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
        <el-divider content-position="left">{{article.articleDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'ArticleDetails',
    data () {
      return {
        article: []
      }
    },
    mounted () {
      this.loadArticle()
    },
    methods: {
      loadArticle () {
        var _this = this
        this.$axios.get('/article/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.article = resp.data.result
          }
        })
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";
</style> -->

<template>
  <div class="articles-area">
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
      <div>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>

          <el-tooltip effect="dark" :content="isCollected ? '取消收藏' : '收藏本文'" placement="top">
            <el-button
              type="text"
              style="padding: 0; margin-right: 10px;"
              @click="handleCollection">
              <i :class="isCollected ? 'el-icon-star-on' : 'el-icon-star-off'"
                 style="font-size: 28px; color: #ffac38;">
              </i>
            </el-button>
          </el-tooltip>
        </div>

        <el-divider content-position="left">{{article.articleDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'ArticleDetails',
    data () {
      return {
        article: {}, // 这里建议改成对象 {}，因为文章详情通常是一个对象
        isCollected: false // 【新增】记录当前文章是否被收藏
      }
    },
    mounted () {
      this.loadArticle()
      // 【新增】页面一加载，立刻检查收藏状态
      this.checkCollectionStatus()
    },
    methods: {
      loadArticle () {
        var _this = this
        this.$axios.get('/article/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.article = resp.data.result
          }
        })
      },
      // 【核心逻辑 1】检查收藏状态 (防御性编程：没登录就不查)
      checkCollectionStatus () {
        // 1. 获取当前用户 (从 Vuex 仓库拿)
        let user = this.$store.state.user

        // 2. 严格检查：如果 Store 里没有 user，或者 user 里没有 id
        // 说明是游客，或者刷新页面导致状态丢失，直接停止，绝对不发请求给后端
        if (!user || !user.id) {
          console.log('游客模式或未登录，不查询收藏状态')
          return
        }

        // 3. 只有确认有 ID 了，才向后端发起查询
        this.$axios.get('/collection/status', {
          params: {
            uid: user.id,
            aid: this.$route.query.id
          }
        }).then(resp => {
          if (resp && resp.status === 200) {
            // 后端返回 true (已收藏) 或 false (未收藏)
            this.isCollected = resp.data.result
          }
        })
      },
      // 【核心逻辑 2】点击收藏按钮
      handleCollection () {
        // 1. 获取当前用户
        let user = this.$store.state.user

        // 2. 严格检查：如果没登录，点击无效，只弹窗提示
        if (!user || !user.id) {
          this.$message.warning('请先登录后收藏')
          // 这里不跳转，只提示，体验更好
          return
        }

        // 3. 发送请求（uid 和 aid）
        this.$axios.post('/collection', {
          uid: user.id,
          aid: this.$route.query.id
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            // 4. 根据当前状态给提示
            if (this.isCollected) {
               this.$message.success('已取消收藏')
            } else {
               this.$message.success('收藏成功')
            }
            // 5. 前端取反状态，星星变色
            this.isCollected = !this.isCollected
          }
        })
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";
</style>
