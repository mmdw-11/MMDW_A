<template>
  <div class="dashboard-container">
    <div class="header-section">
      <el-card shadow="never" class="welcome-card">
        <div class="welcome-content">
          <div class="user-info">
            <el-avatar
              :size="60"
              icon="el-icon-user-solid"
              src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
            />
            <div class="text-group">
              <h2 class="title">欢迎回来！</h2>
              <p class="subtitle">今日建议：保持热爱，奔赴山海。</p>
            </div>
          </div>
          <div class="quick-stats">
            <div class="stat-item">
              <div class="stat-label">待办任务</div>
              <div class="stat-val">{{ unfinishedCount }}</div>
            </div>
            <el-divider direction="vertical" />
            <div class="stat-item">
              <div class="stat-label">系统状态</div>
              <div class="stat-val green">稳定运行</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <el-row :gutter="20">
      <el-col :lg="16" :xs="24">
        <el-card shadow="never" header="快捷功能" class="margin-b-20">
          <el-row type="flex" justify="center" class="quick-nav">
            <el-col :span="6" v-for="nav in navs" :key="nav.title">
              <div class="nav-item" @click="handleJump(nav.path)">
                <i :class="nav.icon" :style="{ color: nav.color }" />
                <span>{{ nav.title }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <el-card shadow="never" class="todo-card">
          <div slot="header" class="todo-header">
            <span>待办事项</span>
            <el-input
              v-model="todoInput"
              placeholder="新增任务..."
              size="mini"
              style="width: 200px"
              @keyup.enter.native="handleAddTodo"
            >
              <el-button slot="append" icon="el-icon-plus" @click="handleAddTodo" />
            </el-input>
          </div>

          <el-table :data="todoList" :show-header="false" empty-text="暂无任务">
            <el-table-column width="40">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.completed" @change="saveTodos" />
              </template>
            </el-table-column>

            <el-table-column>
              <template slot-scope="scope">
                <span :class="{ 'is-done': scope.row.completed }">
                  {{ scope.row.content }}
                </span>
              </template>
            </el-table-column>

            <el-table-column width="60" align="right">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  icon="el-icon-delete"
                  @click="handleDeleteTodo(scope.$index)"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :lg="8" :xs="24">
        <el-card shadow="never" class="inspiration-card">
          <div slot="header" class="inspiration-header">
            <span><i class="el-icon-collection-tag" /> 灵感便签</span>
          </div>

          <div class="inspiration-list">
            <div class="ins-item">
              <p class="ins-text">“ 无论你走得多慢，只要你不停下脚步。 ”</p>
            </div>
            <el-divider />
            <div class="ins-item">
              <p class="ins-text">“ 所谓自由，不是随心所欲，而是自我主宰。 ”</p>
            </div>
            <el-divider />
            <div class="ins-item">
              <p class="ins-text">“ 生活原本沉闷，但跑起来就会有风。 ”</p>
            </div>
            <el-divider />
            <div class="ins-item">
              <p class="ins-text">“ 每一个不曾起舞的日子，都是对生命的辜负。 ”</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'DashboardAdmin',
  data () {
    return {
      todoInput: '',
      navs: [
        { title: '首页', icon: 'el-icon-s-home', color: '#409EFF', path: '/index' },
        { title: '笔记本', icon: 'el-icon-notebook-2', color: '#67C23A', path: '/jotter' },
        { title: '图书馆', icon: 'el-icon-collection', color: '#E6A23C', path: '/library' }
      ],
      todoList: JSON.parse(localStorage.getItem('dashboard_todos')) || [
        { content: '完善项目文档', completed: false }
      ]
    }
  },
  computed: {
    unfinishedCount () {
      return this.todoList.filter(item => !item.completed).length
    }
  },
  methods: {
    handleAddTodo () {
      if (!this.todoInput.trim()) return
      this.todoList.unshift({
        content: this.todoInput,
        completed: false
      })
      this.todoInput = ''
      this.saveTodos()
    },
    handleDeleteTodo (index) {
      this.todoList.splice(index, 1)
      this.saveTodos()
    },
    saveTodos () {
      localStorage.setItem('dashboard_todos', JSON.stringify(this.todoList))
    },
    handleJump (path) {
      this.$router.push(path).catch(() => {
        this.$message.info(`跳转到: ${path}（请确保路由已配置）`)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 24px;
  background: linear-gradient(180deg, #f5f7fa 0%, #f0f2f5 100%);
  min-height: calc(100vh - 84px);

  .margin-b-20 {
    margin-bottom: 20px;
  }

  ::v-deep .el-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  }

  /* 顶部欢迎区 */
  .header-section {
    margin-bottom: 24px;

    .welcome-card {
      background: linear-gradient(135deg, #ffffff, #f9fbff);

      .welcome-content {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .user-info {
          display: flex;
          align-items: center;

          .el-avatar {
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
          }

          .text-group {
            margin-left: 18px;

            .title {
              font-size: 22px;
              font-weight: 600;
              margin-bottom: 6px;
            }

            .subtitle {
              font-size: 14px;
              color: #909399;
            }
          }
        }

        .quick-stats {
          display: flex;
          background: #fff;
          border-radius: 10px;
          padding: 10px 0;

          .stat-item {
            padding: 0 24px;
            text-align: center;

            .stat-label {
              font-size: 13px;
              color: #909399;
            }

            .stat-val {
              font-size: 22px;
              font-weight: 600;
            }

            .green {
              color: #67c23a;
            }
          }
        }
      }
    }
  }

  /* 快捷功能 */
  .quick-nav {
    .nav-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 18px 10px;
      border-radius: 10px;
      cursor: pointer;
      transition: all 0.25s;

      i {
        font-size: 34px;
        margin-bottom: 10px;
      }

      span {
        font-size: 14px;
        font-weight: 500;
        color: #606266;
      }

      &:hover {
        background: #f5f7fa;
        transform: translateY(-4px);
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
      }
    }
  }

  /* 待办事项 */
  .todo-card {
    margin-top: 20px;

    .todo-header {
      font-weight: 600;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .is-done {
      text-decoration: line-through;
      color: #c0c4cc;
    }
  }

  /* 灵感便签 */
  .inspiration-card {
    background: linear-gradient(180deg, #ffffff, #fafcff);

    .inspiration-header {
      font-weight: 600;

      i {
        color: #409eff;
        margin-right: 6px;
      }
    }

    .ins-item {
      padding: 8px 4px;

      .ins-text {
        font-size: 15px;
        font-style: italic;
        line-height: 1.7;
        padding-left: 10px;
        border-left: 3px solid transparent;
        transition: all 0.25s;

        &:hover {
          color: #409eff;
          border-left-color: #409eff;
          background: #f5f9ff;
        }
      }
    }
  }
}
</style>
