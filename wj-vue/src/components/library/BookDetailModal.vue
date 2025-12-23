<template>
  <el-dialog
    :title="book.title"
    :visible.sync="dialogVisible"
    width="80%"
    top="5vh"
    @close="handleClose">

    <div class="book-detail-container" v-loading="loading">
      <!-- 左侧：图书信息 -->
      <div class="left-panel">
        <div class="book-cover-container">
          <img :src="book.cover" alt="封面" class="book-cover">
        </div>
        <div class="basic-info">
          <h3>{{ book.title }}</h3>
          <p><strong>作者：</strong>{{ book.author }}</p>
          <p><strong>出版社：</strong>{{ book.press }}</p>
          <p><strong>出版日期：</strong>{{ book.date }}</p>
        </div>
      </div>

      <!-- 右侧：标签页 -->
      <div class="right-panel">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <!-- 标签页1：简介 -->
          <el-tab-pane label="简介" name="summary">
            <div class="summary-section">
              <h4>内容简介</h4>
              <p>{{ book.abs || '暂无简介' }}</p>
            </div>
          </el-tab-pane>

          <!-- 标签页2：读者评价 -->
          <el-tab-pane label="读者评价" name="reviews">
            <div class="reviews-section">
              <!-- 发表评论表单 -->
              <div class="add-review-form">
                <el-input
                  type="textarea"
                  :rows="3"
                  placeholder="写下你对这本书的看法..."
                  v-model="newReview"
                  :maxlength="500"
                  show-word-limit>
                </el-input>
                <div class="form-actions">
                  <el-button
                    type="primary"
                    size="small"
                    @click="submitReview"
                    :disabled="!newReview.trim()">
                    发表评论
                  </el-button>
                </div>
              </div>

              <!-- 评论列表 -->
              <div class="reviews-list">
                <div v-if="reviews.length === 0" class="empty-message">
                  暂无评论，快来发表第一条评论吧！
                </div>

                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="review-header">
                    <span class="review-user">{{ review.user ? review.user.username : '匿名用户' }}</span>
                    <span class="review-time">{{ formatTime(review.createTime) }}</span>
                  </div>
                  <div class="review-content">{{ review.content }}</div>

                  <div class="review-footer">
                    <el-button
                      type="text"
                      size="mini"
                      @click="likeReview(review.id)"
                      :icon="review.liked ? 'el-icon-thumb' : 'el-icon-thumb'"
                      :class="{ 'liked': review.liked }">
                      {{ review.likes || 0 }}
                    </el-button>

                    <el-button
                      type="text"
                      size="mini"
                      icon="el-icon-delete"
                      style="color: #F56C6C; margin-left: 10px;"
                      @click="handleDeleteReview(review.id)">
                      删除
                    </el-button>
                  </div>
                </div>
              </div> </div> </el-tab-pane>

          <el-tab-pane label="金句摘录" name="quotes">
            <div class="quotes-section">
              <div class="add-quote-form">
                <el-input
                  type="textarea"
                  :rows="2"
                  placeholder="摘录你喜欢的句子..."
                  v-model="newQuote"
                  :maxlength="200"
                  show-word-limit>
                </el-input>
                <div class="quote-meta-input">
                  <el-input
                    placeholder="页码（可选）"
                    v-model="quotePage"
                    style="width: 100px; margin-right: 10px;"
                    size="small">
                  </el-input>
                  <el-button
                    type="success"
                    size="small"
                    @click="submitQuote"
                    :disabled="!newQuote.trim()">
                    添加摘录
                  </el-button>
                </div>
              </div>

              <!-- 金句列表 -->
              <div class="quotes-list">
                <div v-if="quotes.length === 0" class="empty-message">
                  暂无摘录，快来添加第一条金句吧！
                </div>

                <div v-for="quote in quotes" :key="quote.id" class="quote-item">
                  <div class="quote-content">"{{ quote.content }}"</div>
                  <div class="quote-meta">
                    <span class="quote-user">{{ quote.user ? quote.user.username : '匿名用户' }}</span>
                    <span v-if="quote.pageNumber" class="quote-page">· 第{{ quote.pageNumber }}页</span>
                    <span class="quote-time">· {{ formatTime(quote.createTime) }}</span>
                  </div>
                  <div class="quote-footer">
                    <el-button
                      type="text"
                      size="mini"
                      @click="likeQuote(quote.id)"
                      :icon="quote.liked ? 'el-icon-star-on' : 'el-icon-star-off'"
                      :class="{ 'liked': quote.liked }">
                      {{ quote.likes || 0 }}
                    </el-button>

                    <el-button
                      type="text"
                      size="mini"
                      icon="el-icon-delete"
                      style="color: #F56C6C; margin-left: 10px;"
                      @click="handleDeleteQuote(quote.id)">
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'BookDetailModal',
  data () {
    return {
      dialogVisible: false,
      loading: false,
      bookId: null,
      book: {},
      activeTab: 'summary',

      // 评论相关
      reviews: [],
      newReview: '',

      // 金句相关
      quotes: [],
      newQuote: '',
      quotePage: ''
    }
  },
  watch: {
    activeTab (newTab) {
      if (newTab === 'reviews') {
        this.loadReviews()
      } else if (newTab === 'quotes') {
        this.loadQuotes()
      }
    }
  },
  methods: {
    show (bookId) {
      this.bookId = bookId
      this.dialogVisible = true
      this.loadBookDetail()
    },

    async loadBookDetail () {
      this.loading = true
      try {
        const resp = await this.$axios.get(`/books/${this.bookId}`)
        console.log('API响应:', resp)
        console.log('响应数据:', resp.data)
        console.log('结果对象:', resp.data.result)
        if (resp && resp.data.code === 200) {
          this.book = resp.data.result
          console.log('book对象:', this.book)
          console.log('所有字段:', Object.keys(this.book))
          console.log('abs字段值:', this.book.abs)
          console.log('has abs属性:', 'abs' in this.book)
          console.log('JSON字符串:', JSON.stringify(this.book, null, 2))
        }
      } catch (error) {
        console.error('加载图书详情失败:', error)
      } finally {
        this.loading = false
      }
    },

    async loadReviews () {
      try {
        const resp = await this.$axios.get(`/books/${this.bookId}/reviews`)
        if (resp && resp.data.code === 200) {
          this.reviews = resp.data.result.map(review => ({
            ...review,
            liked: false // 这里需要根据用户是否点赞来设置
          }))
        }
      } catch (error) {
        console.error('加载评论失败:', error)
      }
    },

    async loadQuotes () {
      try {
        const resp = await this.$axios.get(`/books/${this.bookId}/quotes`)
        if (resp && resp.data.code === 200) {
          this.quotes = resp.data.result.map(quote => ({
            ...quote,
            liked: false // 这里需要根据用户是否点赞来设置
          }))
        }
      } catch (error) {
        console.error('加载金句失败:', error)
      }
    },

    handleTabClick (tab) {
      // 标签切换时加载对应数据
      if (tab.name === 'reviews' && this.reviews.length === 0) {
        this.loadReviews()
      } else if (tab.name === 'quotes' && this.quotes.length === 0) {
        this.loadQuotes()
      }
    },

    // async submitReview () {
    //   if (!this.newReview.trim()) return
    //
    //   try {
    //     const resp = await this.$axios.post('/books/reviews', {
    //       bookId: this.bookId,
    //       content: this.newReview
    //     })
    //
    //     if (resp && resp.data.code === 200) {
    //       this.$message.success('评论发表成功')
    //       this.newReview = ''
    //       this.loadReviews()
    //     }
    //   } catch (error) {
    //     console.error('发表评论失败:', error)
    //     this.$message.error('发表评论失败')
    //   }
    // },

    async submitReview () {
      if (!this.newReview.trim()) {
        this.$message.warning('评论内容不能为空')
        return
      }

      try {
        // 确保使用的是 this.book.id 或者你在 show 方法中保存的那个 ID
        const resp = await this.$axios.post('/books/reviews', {
          bookId: this.book.id, // 修改这里：通常 ID 存在 book 对象里
          content: this.newReview
        })

        if (resp && resp.data.code === 200) {
          this.$message.success('评论发表成功')
          this.newReview = ''
          this.loadReviews() // 重新加载评论列表
        } else {
          // 捕获后端返回的错误信息，比如“图书不存在”
          this.$message.error(resp.data.message || '发表失败')
        }
      } catch (error) {
        console.error('发表评论失败:', error)
        this.$message.error('发表评论失败，请检查网络或登录状态')
      }
    },

    async submitQuote () {
      if (!this.newQuote.trim()) return

      try {
        const resp = await this.$axios.post('/books/quotes', {
          bookId: this.bookId,
          content: this.newQuote,
          pageNumber: this.quotePage ? parseInt(this.quotePage) : 0
        })

        if (resp && resp.data.code === 200) {
          this.$message.success('金句添加成功')
          this.newQuote = ''
          this.quotePage = ''
          this.loadQuotes()
        }
      } catch (error) {
        console.error('添加金句失败:', error)
        this.$message.error('添加金句失败')
      }
    },

    // async likeReview (reviewId) {
    //   try {
    //     const resp = await this.$axios.post(`/books/reviews/${reviewId}/like`)
    //     if (resp && resp.data.code === 200) {
    //       // 更新本地数据
    //       const index = this.reviews.findIndex(r => r.id === reviewId)
    //       if (index !== -1) {
    //         this.reviews[index].likes = resp.data.result.likes
    //         this.reviews[index].liked = !this.reviews[index].liked
    //       }
    //     }
    //   } catch (error) {
    //     console.error('点赞失败:', error)
    //   }
    // }
    async likeReview (reviewId) {
      const review = this.reviews.find(r => r.id === reviewId)
      // 如果当前是 liked=true，则下一步动作 isLike=false (取消)
      const nextStatus = !review.liked

      try {
        // 使用 query 参数传递 isLike
        const resp = await this.$axios.post(`/books/reviews/${reviewId}/like?isLike=${nextStatus}`)
        if (resp && resp.data.code === 200) {
          review.likes = resp.data.result.likes
          review.liked = nextStatus // 同步前端状态
        }
      } catch (error) {
        console.error('操作失败', error)
      }
    },

    // async likeQuote (quoteId) {
    //   try {
    //     const resp = await this.$axios.post(`/books/quotes/${quoteId}/like`)
    //     if (resp && resp.data.code === 200) {
    //       // 更新本地数据
    //       const index = this.quotes.findIndex(q => q.id === quoteId)
    //       if (index !== -1) {
    //         this.quotes[index].likes = resp.data.result.likes
    //         this.quotes[index].liked = !this.quotes[index].liked
    //       }
    //     }
    //   } catch (error) {
    //     console.error('点赞失败:', error)
    //   }
    // },
    async likeQuote (quoteId) {
      // 1. 在本地列表中找到对应的金句对象
      const quote = this.quotes.find(q => q.id === quoteId)
      if (!quote) return

      // 2. 确定下一步状态：如果当前 liked 为 false，则下一步是点赞 (true)
      const nextStatus = !quote.liked

      try {
        // 3. 关键修复：必须带上 ?isLike= 参数，否则后端会因为缺少参数而报错或不执行
        const resp = await this.$axios.post(`/books/quotes/${quoteId}/like?isLike=${nextStatus}`)

        if (resp && resp.data.code === 200) {
          // 4. 同步后端返回的最新点赞数
          quote.likes = resp.data.result.likes
          // 5. 切换前端的红心/星星状态
          quote.liked = nextStatus
        }
      } catch (error) {
        console.error('金句点赞操作失败:', error)
        this.$message.error('点赞失败')
      }
    },

    formatTime (timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleDateString()
    },

    handleClose () {
      // 重置数据
      this.book = {}
      this.reviews = []
      this.quotes = []
      this.newReview = ''
      this.newQuote = ''
      this.quotePage = ''
      this.activeTab = 'summary'
    },

    // 删除评论
    handleDeleteReview (id) {
      this.$confirm('确定要删除这条评论吗?', '提示', {
        type: 'warning'
      }).then(() => {
        this.$axios.delete('/books/reviews/' + id).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadReviews() // 必须重新加载列表，页面才会更新
          }
        })
      }).catch(() => {})
    },

// 删除金句
    handleDeleteQuote (id) {
      this.$confirm('确定要删除这条金句吗?', '提示', {
        type: 'warning'
      }).then(() => {
        this.$axios.delete('/books/quotes/' + id).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadQuotes()// 必须重新加载列表
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.book-detail-container {
  display: flex;
  gap: 30px;
  min-height: 500px;
}

.left-panel {
  flex: 0 0 250px;
}

.book-cover-container {
  width: 200px;
  height: 280px;
  margin-bottom: 20px;
  border: 1px solid #eee;
  border-radius: 5px;
  overflow: hidden;
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.basic-info h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #333;
}

.basic-info p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

.right-panel {
  flex: 1;
}

/* 评论区域样式 */
.add-review-form,
.add-quote-form {
  margin-bottom: 30px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 5px;
}

.form-actions,
.quote-meta {
  margin-top: 10px;
  text-align: right;
}

.reviews-list,
.quotes-list {
  max-height: 400px;
  overflow-y: auto;
}

.empty-message {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}

.review-item,
.quote-item {
  padding: 15px;
  margin-bottom: 15px;
  border: 1px solid #eee;
  border-radius: 5px;
  background: white;
}

.review-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.review-user {
  font-weight: bold;
  color: #333;
}

.review-time {
  color: #999;
}

.review-content {
  line-height: 1.6;
  color: #333;
  margin-bottom: 10px;
}

.review-footer {
  text-align: right;
}

.quote-content {
  font-size: 16px;
  font-style: italic;
  color: #666;
  margin-bottom: 10px;
  line-height: 1.6;
}

.quote-meta {
  font-size: 13px;
  color: #999;
  margin-bottom: 10px;
}

.quote-user {
  font-weight: bold;
}

.quote-page {
  margin: 0 5px;
}

.quote-footer {
  text-align: right;
}

.liked {
  color: #f56c6c;
}

.summary-section {
  padding: 20px;
  line-height: 1.8;
  color: #333;
}

.summary-section h4 {
  margin-bottom: 15px;
  color: #333;
}
</style>
