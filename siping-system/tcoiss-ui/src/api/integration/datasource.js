import request from '@/utils/request'

// 查询数据源配置列表
export function listDatasource(query) {
  return request({
    url: '/dbsource/datasource/list',
    method: 'get',
    params: query
  })
}

// 查询数据源配置详细
export function getDatasource(id) {
  return request({
    url: '/dbsource/datasource/' + id,
    method: 'get'
  })
}

// 新增数据源配置
export function addDatasource(data) {
  return request({
    url: '/dbsource/datasource',
    method: 'post',
    data: data
  })
}

// 修改数据源配置
export function updateDatasource(data) {
  return request({
    url: '/dbsource/datasource',
    method: 'put',
    data: data
  })
}

// 删除数据源配置
export function delDatasource(id) {
  return request({
    url: '/dbsource/datasource/' + id,
    method: 'delete'
  })
}
