import request from '@/utils/request'

// 查询轨迹服务配置列表
export function listTrackservice(query) {
  return request({
    url: '/webservice/trackservice/list',
    method: 'get',
    params: query
  })
}

export function getService() {
  return request({
    url: '/webservice/trackservice/getService',
    method: 'get',
    params: {}
  })
}

// 查询轨迹服务配置详细
export function getTrackservice(id) {
  return request({
    url: '/webservice/trackservice/' + id,
    method: 'get'
  })
}

// 新增轨迹服务配置
export function addTrackservice(data) {
  return request({
    url: '/webservice/trackservice',
    method: 'post',
    data: data
  })
}

// 修改轨迹服务配置
export function updateTrackservice(data) {
  return request({
    url: '/webservice/trackservice',
    method: 'put',
    data: data
  })
}

// 删除轨迹服务配置
export function delTrackservice(id) {
  return request({
    url: '/webservice/trackservice/' + id,
    method: 'delete'
  })
}
