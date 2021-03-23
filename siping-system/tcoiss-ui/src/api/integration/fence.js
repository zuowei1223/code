import request from '@/utils/request'

// 查询电子围栏列表
export function listFence(query) {
  return request({
    url: '/webservice/fence/list',
    method: 'get',
    params: query
  })
}


// 查询电子围栏详细
export function getFence(id) {
  return request({
    url: '/webservice/fence/' + id,
    method: 'get'
  })
}

// 新增电子围栏
export function addFence(data) {
  return request({
    url: '/webservice/fence/addFence',
    method: 'post',
    data: data
  })
}


// 修改电子围栏
export function updateFence(data) {
  return request({
    url: '/webservice/fence/editFence',
    method: 'post',
    data: data
  })
}

// 删除电子围栏
export function delFence(id) {
  return request({
    url: '/webservice/fence/' + id,
    method: 'delete'
  })
}





