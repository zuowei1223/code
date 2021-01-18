import request from '@/utils/request'

// 查询datasource列表
export function listDatasource(query) {
  return request({
    url: '/test/datasource/list',
    method: 'get',
    params: query
  })
}

// 查询datasource详细
export function getDatasource(datasourceId) {
  return request({
    url: '/test/datasource/' + datasourceId,
    method: 'get'
  })
}

// 新增datasource
export function addDatasource(data) {
  return request({
    url: '/test/datasource',
    method: 'post',
    data: data
  })
}

// 修改datasource
export function updateDatasource(data) {
  return request({
    url: '/test/datasource',
    method: 'put',
    data: data
  })
}

// 删除datasource
export function delDatasource(datasourceId) {
  return request({
    url: '/test/datasource/' + datasourceId,
    method: 'delete'
  })
}
