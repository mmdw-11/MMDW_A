// import Vue from 'vue'
// import Vuex from 'vuex'

// Vue.use(Vuex)

// export default new Vuex.Store({
//   state: {
//     username: window.localStorage.getItem('username') == null ? '' : JSON.parse(window.localStorage.getItem('username' || '[]')),
//     adminMenus: []
//   },
//   mutations: {
//     initAdminMenu (state, menus) {
//       state.adminMenus = menus
//     },
//     login (state, data) {
//       state.username = data
//       window.localStorage.setItem('username', JSON.stringify(data))
//     },
//     logout (state) {
//       // 注意不能用 null 清除，否则将无法判断 user 里具体的内容
//       state.username = ''
//       window.localStorage.removeItem('username')
//       state.adminMenus = []
//     }
//   },
//   actions: {
//   }
// })
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    // 【修复】统一叫 'user'，并增加容错处理
    user: (function () {
        try {
            var localUser = window.localStorage.getItem('user')
            // 如果缓存里有东西，尝试解析；如果是纯字符串解析失败，就把它当做对象的一个属性存起来
            return localUser ? JSON.parse(localUser) : { username: '' }
        } catch (e) {
            // 如果解析报错（说明存的是旧的纯字符串），为了兼容，我们手动构造一个对象
            return { username: window.localStorage.getItem('user') || '' }
        }
    })(),
    adminMenus: []
  },
  mutations: {
    initAdminMenu (state, menus) {
      state.adminMenus = menus
    },
    login (state, data) {
      // 【修复】无论后端传回来的是字符串还是对象，我们都把它统一成对象格式
      if (typeof data === 'string') {
          state.user = { username: data, id: null } // 兼容旧后端
      } else {
          state.user = data // 新后端返回的完整对象
      }
      // 存入浏览器缓存
      window.localStorage.setItem('user', JSON.stringify(state.user))
    },
    logout (state) {
      state.user = { username: '' }
      window.localStorage.removeItem('user')
      state.adminMenus = []
    }
  },
  actions: {
  }
})
