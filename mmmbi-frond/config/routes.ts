export default [

  {path: '/chart', name: '智能分析', icon: 'pieChart',
  hideChildrenInMenu: false,
  routes: [
    {path: '/chart/add', name: '分析图表(同步)', component: './Chart/AddChart'},
    {path: '/chart/add_async', name: '分析图表(异步)', component: './Chart/AddChartAsync'},
  ]},

  {path: '/analysis', name: '分析结果', icon: 'barChart',
  hideChildrenInMenu: false,
  routes: [
    {path: '/analysis/my_chart',name: '我的图表', component: './My/MyChart'},
  ]},

  {path: '/user/edit',name: '我的信息', icon: 'user',component: './User/UserEdit'},
  {
    path: '/user',
    layout: false,
    routes: [
      {name: '登录', path: '/user/login', component: './User/Login'},
      {name: '注册', path: '/user/register', component: './User/Register'},
    ],
  },
  // {
  //   path: '/admin',
  //   icon: 'crown',
  //   access: 'canAdmin',
  //   name: '管理人员页面',
  //   routes: [
  //   ],
  // },
  {path: '/',redirect:'/analysis/my_chart'},
  {path: '*', layout: false, component: './404'},

];
