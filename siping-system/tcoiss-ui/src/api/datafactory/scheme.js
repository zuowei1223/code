import request from '@/utils/request'

// 查询执行方案列表
export function listScheme(query) {
  return request({
    url: '/datafactory/scheme/list',
    method: 'get',
    params: query
  })
}

// 查询执行方案列表
export function getSchemes(query) {
  return request({
    url: '/datafactory/scheme/getSchemes',
    method: 'get',
    params: query
  })
}

// 查询执行方案详细
export function getScheme(schemeId) {
  return request({
    url: '/datafactory/scheme/' + schemeId,
    method: 'get'
  })
}

// 新增执行方案
export function addScheme(data) {
  return request({
    url: '/datafactory/scheme',
    method: 'post',
    data: data
  })
}

// 修改执行方案
export function updateScheme(data) {
  return request({
    url: '/datafactory/scheme',
    method: 'put',
    data: data
  })
}

// 删除执行方案
export function delScheme(schemeId) {
  return request({
    url: '/datafactory/scheme/' + schemeId,
    method: 'delete'
  })
}
